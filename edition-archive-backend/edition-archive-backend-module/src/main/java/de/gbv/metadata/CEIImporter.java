package de.gbv.metadata;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.Text;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.mycore.common.MCRException;
import org.mycore.datamodel.metadata.MetaJSON;

import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CEIImporter implements Iterator<Regest> {
    public static final String YEAR_GROUP_NAME = "Year";
    public static final String MONTH_GROUP_NAME = "Month";
    public static final String DAY_GROUP_NAME = "Day";
    public static final String NUMBER_GROUP_NAME = "Number";
    public static final String IDNO_REGEXP = "^[?*†(]*(?<" + NUMBER_GROUP_NAME + ">[0-9]+)?[)]*.*$";
    public static final String DATE_REGEXP = "^(?<" + YEAR_GROUP_NAME + ">[0-9][0-9]?[0-9]?[0-9]?)((?<" + MONTH_GROUP_NAME + ">[0-9][0-9])(?<" + DAY_GROUP_NAME + ">[0-9][0-9]))?$";
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Namespace CEI_NAMESPACE = Namespace.getNamespace("cei", "http://www.monasterium.net/NS/cei");
    final Pattern datePattern = Pattern.compile(DATE_REGEXP);
    final Pattern idnoPattern = Pattern.compile(IDNO_REGEXP);
    // holds the ceiGroup which contains all cei:text
    private final Element ceiGroup;
    Document document;
    // holds all text elements below ceiGroup
    List<Element> textElements;
    // holds the current <cei:text type="charter">
    private int currentTextElementIndex;

    public CEIImporter(Path gesamtXML) throws IOException, JDOMException {
        SAXBuilder builder = new SAXBuilder();
        document = builder.build(gesamtXML.toFile());
        ceiGroup = document.getRootElement().getChild("text", CEI_NAMESPACE).getChild("group", CEI_NAMESPACE);
        textElements = ceiGroup.getChildren("text", CEI_NAMESPACE);
        currentTextElementIndex = 1;
    }

    public static void main(String[] args) throws IOException, JDOMException {

    }

    public static Element createMetadata(Regest regest) {
        Element metadata = new Element("metadata");

        Element defRegestElement = new Element("def.regest");
        defRegestElement.setAttribute("class", "MetaJSON");
        defRegestElement.setAttribute("heritable", "false");
        defRegestElement.setAttribute("notinherit", "true");

        Element regestElement = new Element("regest");
        regestElement.setAttribute("inherited", "0");
        regestElement.setAttribute("class", Regest.class.getName());

        defRegestElement.addContent(regestElement);
        String regestJson = MetaJSON.getGson().toJson(regest);
        regestElement.setText(regestJson);

        metadata.addContent(defRegestElement);

        return metadata;
    }

    private static Date matcherToDate(Matcher matcher, boolean from) throws ParseException {
        String year = matcher.group(YEAR_GROUP_NAME);
        String month = matcher.group(MONTH_GROUP_NAME);
        String day = matcher.group(DAY_GROUP_NAME);

        StringBuilder dateStr = new StringBuilder();
        dateStr.append(year);

        for (int i = dateStr.length(); i < 4; i++) {
            dateStr.insert(0, '0');
        }

        if (month != null) {
            dateStr.append('-').append(month);
        } else {
            if (from) {
                dateStr.append('-').append("00");
            } else {
                dateStr.append('-').append("12");
            }
        }

        if (day != null) {
            dateStr.append('-').append(day);
        } else {
            if (from) {
                dateStr.append('-').append("00");
            } else {
                dateStr.append('-').append("31");
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.parse(dateStr.toString());
    }

    @Override
    public boolean hasNext() {
        return textElements.size() > currentTextElementIndex;
    }

    @Override
    public Regest next() {
        Element currentTextElement = textElements.get(currentTextElementIndex);
        Element currentBodyElement = currentTextElement.getChild("body", CEI_NAMESPACE);

        Regest regest = new Regest();
        extractIdno(currentBodyElement, regest);
        extractIssuedPlace(currentBodyElement, regest);
        extractIssuer(currentBodyElement, regest);
        extractRecipient(currentBodyElement, regest);
        extractIssuedDate(currentBodyElement, regest);
        extractInitium(currentBodyElement, regest);

        currentTextElementIndex++;
        return regest;
    }

    private void extractIdno(Element currentBodyElement, Regest regest) {
        Element idnoElement = currentBodyElement.getChild("idno", CEI_NAMESPACE);
        String idnoElementContent = idnoElement.getText();
        Matcher idnoMatcher = idnoPattern.matcher(idnoElementContent);
        if(!idnoMatcher.matches()){
            throw new MCRException(idnoElementContent + " doesnt match the pattern " + idnoPattern);
        }
        String idnoMatched = idnoMatcher.group(NUMBER_GROUP_NAME);
        regest.setIdno(idnoMatched);
        String status = idnoElement.getAttributeValue("n");

        Authenticity authenticity = new Authenticity();

        if(status!=null){


        if(status.contains("*")){
            authenticity.setLost(true);
        }

        if(status.contains("†")){
            authenticity.setFake(true);
        }

        if(!status.contains("?")){
            authenticity.setCertainly(true);
        }
        }

        regest.setAuthenticityStatus(authenticity);
    }

    private void extractInitium(Element currentBodyElement, Regest regest) {
        Element foreign = getXpathFirst(".//cei:diplomaticAnalysis/cei:incipit/cei:foreign", currentBodyElement);
        if (foreign != null) {
            String initium = flattenText(foreign);
            regest.setInitium(initium);
        }

    }

    private void extractIssuedDate(Element currentBodyElement, Regest regest) {
        Element dateRange = getXpathFirst(".//cei:issued/cei:dateRange", currentBodyElement);

        if (dateRange != null) {
            String issuedDateText = flattenText(dateRange);
            DateRangeText dateRangeText = new DateRangeText();
            dateRangeText.setText(issuedDateText);

            String from = dateRange.getAttributeValue("from");
            String to = dateRange.getAttributeValue("to");

            if (from != null) {
                final Matcher fromMatcher = datePattern.matcher(from);
                if (!fromMatcher.matches()) {
                    LOGGER.info("Date does not match pattern " + from);
                } else {
                    try {
                        dateRangeText.setFrom(matcherToDate(fromMatcher, true));
                    } catch (ParseException e) {
                        throw new MCRException(e);
                    }
                }
            }


            if (to != null) {
                final Matcher toMatcher = datePattern.matcher(to);
                if (!toMatcher.matches()) {
                    LOGGER.info("Date does not match pattern " + to);
                } else {
                    try {
                        dateRangeText.setTo(matcherToDate(toMatcher, false));
                    } catch (ParseException e) {
                        throw new MCRException(e);
                    }
                }
            }

            regest.setIssued(dateRangeText);

        }
    }

    private void extractIssuedPlace(Element currentBodyElement, Regest regest) {
        // the places might need to be extracted to an extra field
        Element issuedPlaceName = getXpathFirst(".//cei:issued/cei:placeName", currentBodyElement);
        if (issuedPlaceName != null) {
            String issuedPlace = flattenText(issuedPlaceName);
            regest.setIssuedPlace(issuedPlace);
        }
    }

    private void extractIssuer(Element currentBodyElement, Regest regest) {
        Element issuerPersNameElement = getXpathFirst(".//cei:issuer/cei:persName", currentBodyElement);
        if (issuerPersNameElement == null) {
            LOGGER.info("NO ISSUER SET!");
        } else {
            String issuerString = flattenText(issuerPersNameElement);
            regest.setIssuer(issuerString);
        }
    }

    private void extractRecipient(Element currentBodyElement, Regest regest) {
        Element recipientPersNameElement = getXpathFirst(".//cei:recipient/cei:persName", currentBodyElement);
        if (recipientPersNameElement == null) {
            LOGGER.info("NO recipient SET!");
        } else {
            String recipientString = flattenText(recipientPersNameElement);
            regest.setRecipient(recipientString);
        }
    }

    private Element getXpathFirst(String xpathString, Element context) {
        XPathExpression<Element> r = XPathFactory.instance().compile(xpathString, Filters.element(), null, CEI_NAMESPACE);
        return r.evaluateFirst(context);
    }

    /**
     * Returns all text content of this element including text of children.
     *
     * @param element the elment
     * @return
     */
    private String flattenText(Element element) {
        return flattenText(element, null).toString();
    }

    private StringBuilder flattenText(Element element, StringBuilder textContent) {
        List<Content> contentList = element.getContent();
        if (textContent == null) {
            textContent = new StringBuilder();
        }
        for (Content content : contentList) {
            if (content instanceof Text) {
                textContent.append(((Text) content).getText());
            } else if (content instanceof Element) {
                flattenText((Element) content, textContent);
            }
        }
        return textContent;
    }
}