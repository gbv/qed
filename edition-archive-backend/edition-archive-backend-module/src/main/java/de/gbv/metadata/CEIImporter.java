package de.gbv.metadata;

import de.gbv.metadata.model.Person;
import de.gbv.metadata.model.PersonLink;
import de.gbv.metadata.model.Place;
import de.gbv.metadata.model.PlaceLink;
import de.gbv.metadata.model.Regest;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CEIImporter {
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
  List<Regest> regests = new LinkedList<>();
  List<Person> persons = new LinkedList<>();

  List<Place> places = new LinkedList<>();
  HashMap<Regest, Element> regestTextMap = new HashMap<>();
  HashMap<Person, List<PersonLink>> personLinksHashMap = new HashMap<>();
  HashMap<Place, List<PlaceLink>> placeLinksHashMap = new HashMap<>();

  public CEIImporter(Path gesamtXML) throws IOException, JDOMException {
    SAXBuilder builder = new SAXBuilder();
    document = builder.build(gesamtXML.toFile());
    ceiGroup = document.getRootElement().getChild("text", CEI_NAMESPACE).getChild("group", CEI_NAMESPACE);
    textElements = ceiGroup.getChildren("text", CEI_NAMESPACE);
  }

    public static void main(String[] args) throws IOException, JDOMException {

    }

    public static Element createMetadata(Object regest, String className, String type) {
        Element metadata = new Element("metadata");

        Element defRegestElement = new Element("def." + type);
        defRegestElement.setAttribute("class", "MetaJSON");
        defRegestElement.setAttribute("heritable", "false");
        defRegestElement.setAttribute("notinherit", "true");

        Element regestElement = new Element(type);
        regestElement.setAttribute("inherited", "0");
        regestElement.setAttribute("class", className);

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

  public List<Regest> getRegests() {
    return regests;
  }

  public List<Person> getPersons() {
    return persons;
  }

  public List<Place> getPlaces() {
    return places;
  }

  public HashMap<Regest, Element> getRegestTextMap() {
    return regestTextMap;
  }

  public void runImport() {
    textElements.forEach(currentTextElement -> {
      Element currentBodyElement = currentTextElement.getChild("body", CEI_NAMESPACE);

      Regest regest = new Regest();
      extractIdno(currentBodyElement, regest);
      extractIssuedPlace(currentBodyElement, regest);
      extractOtherPlaces(currentBodyElement, regest);
      extractIssuer(currentBodyElement, regest);
      extractRecipient(currentBodyElement, regest);
      extractIssuedDate(currentBodyElement, regest);
      extractInitium(currentBodyElement, regest);
      extractDeliveryForm(currentBodyElement, regest);
      extractPersonParagraph(currentBodyElement, "PontifikatPP", regest::setPontifikatPP);
      extractPersonParagraph(currentBodyElement, "PontifikatAEP", regest::setPontifikatAEP);
      extractOtherPersons(currentBodyElement, regest);
      regests.add(regest);
      Element textElement = currentTextElement.clone();
      regestTextMap.put(regest, textElement);
    });
    }

    private void extractOtherPersons(Element currentBodyElement, Regest regest) {
      List<Element> otherPersNames = getXpathList(".//cei:persName", currentBodyElement);
      otherPersNames.forEach(otherPersName -> {
        extractPerson(otherPersName, p -> {
          regest.getBodyPersons().add(p);
        });
      });
    }

  private void extractOtherPlaces(Element currentBodyElement, Regest regest) {
    List<Element> otherPlaces = getXpathList(".//cei:placeName", currentBodyElement);
    otherPlaces.forEach(otherPlace -> {
      extractPlace(otherPlace, p -> {
        regest.getBodyPlaces().add(p);
      });
    });
  }

  private void extractPlace(Element placeName, Consumer<PlaceLink> placeApplier) {
    String flattenName = flattenText(placeName);
    String key = placeName.getAttributeValue("key");

    List<Place> places = getPlaces().stream().filter(p -> {
      if (key != null) {
        Optional<IdentifierType> matchingIdentifierFound = p.getIdentifier().stream().filter(id -> id.getIdentifier().equals(key)).findFirst();

        if (matchingIdentifierFound.isPresent()) {
          LOGGER.info("FOUND Match by key: " + matchingIdentifierFound.get().getIdentifier());
          return true;
        }
      }

      if (p.getDisplayName().equals(flattenName)) {
        LOGGER.info("FOUND Match by flatten name: " + flattenName);
        return true;
      }

      return false;
    }).toList();

    PlaceLink pl = new PlaceLink();
    pl.setLabel(flattenName);

    Place match;
    if (places.size() == 1) {
      match = places.get(0);
    } else if (places.size() == 0) {
      match = new Place();
      match.setDisplayName(flattenName);
      if (key != null) {
        match.setIdentifier(Stream.of(key).map(k -> new IdentifierType("key", k)).collect(Collectors.toList()));
      }
      getPlaces().add(match);
    } else {
      LOGGER.error("Can not distiguish between the persons " + places.stream().map(Place::getDisplayName).collect(Collectors.joining()));
      return;
    }
    List<PlaceLink> ll = this.placeLinksHashMap.computeIfAbsent(match, (a) -> new LinkedList<>());
    ll.add(pl);
    placeApplier.accept(pl);
  }

  private void extractPerson(Element persName, Consumer<PersonLink> regestApplier) {
    String flattenName = flattenText(persName);
    String key = persName.getAttributeValue("key");

    List<Person> people = getPersons().stream().filter(p -> {
      if (key != null) {
        Optional<IdentifierType> matchingIdentifierFound = p.getIdentifier().stream().filter(id -> id.getIdentifier().equals(key)).findFirst();

        if (matchingIdentifierFound.isPresent()) {
                    LOGGER.info("FOUND Match by key: " + matchingIdentifierFound.get().getIdentifier());
                    return true;
                }
            }

            if (p.getDisplayName().equals(flattenName)) {
                LOGGER.info("FOUND Match by flatten name: " + flattenName);
                return true;
            }

            return false;
        }).toList();
        PersonLink pl = new PersonLink();
        pl.setLabel(flattenName);

        Person match;
        if (people.size() == 1) {
            match = people.get(0);
        } else if (people.size() == 0) {
            match = new Person();
            match.setDisplayName(flattenName);
            if (key != null) {
                match.setIdentifier(Stream.of(key).map(k -> new IdentifierType("key", k)).collect(Collectors.toList()));
            }
            getPersons().add(match);
        } else {
            LOGGER.error("Can not distiguish between the persons " + people.stream().map(Person::getDisplayName).collect(Collectors.joining()));
            return;
        }
        List<PersonLink> ll = this.personLinksHashMap.computeIfAbsent(match, (a) -> new LinkedList<>());
        ll.add(pl);
        regestApplier.accept(pl);
    }

    private void extractIdno(Element currentBodyElement, Regest regest) {
        Element idnoElement = currentBodyElement.getChild("idno", CEI_NAMESPACE);
        String idnoElementContent = idnoElement.getText();
        Matcher idnoMatcher = idnoPattern.matcher(idnoElementContent);
        if (!idnoMatcher.matches()) {
            throw new MCRException(idnoElementContent + " doesnt match the pattern " + idnoPattern);
        }
        String idnoMatched = idnoMatcher.group(NUMBER_GROUP_NAME);
        regest.setIdno(idnoMatched);
        String status = idnoElement.getAttributeValue("n");

        Authenticity authenticity = new Authenticity();

        if (status != null) {


            if (status.contains("*")) {
                authenticity.setLost(true);
            }

            if (status.contains("†")) {
                authenticity.setFake(true);
            }

            if (!status.contains("?")) {
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

  private void extractPersonParagraph(Element currentBodyElement, String pType, Consumer<PersonLink> applyFn) {
    Element paragraphElement = getXpathFirst(".//cei:p[@type='" + pType + "']", currentBodyElement);
    if (paragraphElement != null) {
      Element persName = paragraphElement.getChild("persName", CEI_NAMESPACE);
      extractPerson(persName, applyFn);
    }
  }

    private void extractDeliveryForm(Element currentBodyElement, Regest regest) {
        Element überlieferungsformP = getXpathFirst(".//cei:diplomaticAnalysis/cei:p[@type='Überlieferungsform']", currentBodyElement);
        if (überlieferungsformP != null) {
            String überlieferungsformList = flattenText(überlieferungsformP);
            String[] values = überlieferungsformList.split(",");

            List<String> classIds = Stream.of(values).filter(Predicate.not(String::isBlank)).map(val -> switch (val.trim()) {
                case "Kopie" -> "copy";
                case "Deperditum oder chronikalische Notiz" -> "deperditum_oder_chronikalische_notiz";
                case "Dekretale" -> "dekretale";
                case "Fälschungsverdacht" -> "faelschungverdacht";
                case "Briefsammlung" -> "briefsammlung";
                case "Insert" -> "insert";
                case "Original" -> "original";
                case "Unsichere Information" -> "unsure_information";
                default -> throw new MCRException("Unknown überlieferungsform Value: " + überlieferungsformList);
            }).toList();
            regest.setDeliveryForm(new ClassificationMultivalue("delivery_form", classIds));
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
            if (from != null && from.equals(to)) {
                to = null;
            }

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
          extractPlace(issuedPlaceName, regest::setIssuedPlace);
        }
    }

    public HashMap<Person, List<PersonLink>> getPersonLinksHashMap() {
        return personLinksHashMap;
    }

  public HashMap<Place, List<PlaceLink>> getPlaceLinksHashMap() {
    return placeLinksHashMap;
  }

  private void extractIssuer(Element currentBodyElement, Regest regest) {
        Element issuerPersNameElement = getXpathFirst(".//cei:issuer/cei:persName", currentBodyElement);
        if (issuerPersNameElement == null) {
            LOGGER.info("NO ISSUER SET!");
        } else {
            extractPerson(issuerPersNameElement, regest::setIssuer);
        }
    }

    private void extractRecipient(Element currentBodyElement, Regest regest) {
        Element recipientPersNameElement = getXpathFirst(".//cei:recipient/cei:persName", currentBodyElement);
        if (recipientPersNameElement == null) {
            LOGGER.info("NO recipient SET!");
        } else {
            extractPerson(recipientPersNameElement, regest::setRecipient);
        }
    }

    private Element getXpathFirst(String xpathString, Element context) {
        XPathExpression<Element> r = XPathFactory.instance().compile(xpathString, Filters.element(), null, CEI_NAMESPACE);
        return r.evaluateFirst(context);
    }

    private List<Element> getXpathList(String xpathString, Element context){
      XPathExpression<Element> r = XPathFactory.instance().compile(xpathString, Filters.element(), null, CEI_NAMESPACE);
      return r.evaluate(context);
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