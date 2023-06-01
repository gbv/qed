package de.gbv.metadata;

import de.gbv.metadata.model.EntityLink;
import de.gbv.metadata.model.Manuscript;
import de.gbv.metadata.model.Organization;
import de.gbv.metadata.model.Person;
import de.gbv.metadata.model.Place;
import de.gbv.metadata.model.Regest;
import de.gbv.metadata.model.RegestSource;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
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
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.mycore.common.MCRException;
import org.mycore.datamodel.metadata.MetaJSON;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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
    public static final String DATE_REGEXP = "^(?<" + YEAR_GROUP_NAME + ">[0-9][0-9]?[0-9]?[0-9]?)((?<"
        + MONTH_GROUP_NAME + ">[0-9][0-9])(?<" + DAY_GROUP_NAME + ">[0-9][0-9]))?$";
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Namespace CEI_NAMESPACE = Namespace.getNamespace("cei", "http://www.monasterium.net/NS/cei");
    public static final Pattern datePattern = Pattern.compile(DATE_REGEXP);
    final Pattern idnoPattern = Pattern.compile(IDNO_REGEXP);
    // holds the ceiGroup which contains all cei:text
    private final Element ceiGroup;
    Document document;
    // holds all text elements below ceiGroup
    List<Element> textElements;
    // holds the current <cei:text type="charter">
    List<Regest> regests = new LinkedList<>();
    List<Person> persons = new LinkedList<>();

    List<Organization> organizations = new LinkedList<>();

    List<Place> places = new LinkedList<>();
    HashMap<Regest, Element> regestTextMap = new HashMap<>();
    HashMap<Person, List<EntityLink>> personLinksHashMap = new HashMap<>();
    HashMap<Place, List<EntityLink>> placeLinksHashMap = new HashMap<>();

    HashMap<Organization, List<EntityLink>> organizationLinksHashMap = new HashMap<>();

    public final List<String> REPORT_MESSAGES = new LinkedList<>();

    HashMap<String, RegestSource> keyRegestSourceHashMap = new HashMap<>();
    final String RECORD_N = "n";
    final String RECORD_LONG_TITLE = "Langtitel";
    final String RECORD_SHORT_TITLE = "Kurztitel";
    final String RECORD_KEY = "key";
    final String RECORD_SIGNATUR = "Signatur";
    final String RECORD_DATIERUNG = "Datierung";
    final String RECORD_KATALOGISAT = "Katalogisat";
    final String RECORD_DIGITALISAT = "Digitalisat";
    final Set<String> QUELLEN_LINK_COL = Stream
        .of("Online-Zugriff", "Geschichtsquellen.de", "Arlima.net", "Band I", "Band I,1", "Band I,2", "Band II",
            "Digitalisat", "Supplement",
            "Band III", "Band IV", "Band V", "Band VI", "Band VII", "Band VIII", "Band IX", "Band X", "Band XI",
            "Band XII", "Band XIII", "Band XIV", "Band XVI", "Band XV", "Band VII,1", "Band VII,2")
        .collect(Collectors.toSet());
    final Set<String> HANDSCHRIFTEN_LINK_COL
        = Stream.of(RECORD_KATALOGISAT, RECORD_DIGITALISAT).collect(Collectors.toSet());
    HashMap<String, Manuscript> keyManuscriptHashMap = new HashMap<>();

    private final HashMap<EntityLink, List<Consumer<String>>> personLinkApplierMap = new HashMap<>();

    private final HashMap<EntityLink, List<Consumer<String>>> organizationLinkApplierMap = new HashMap<>();

    private final HashMap<EntityLink, List<Consumer<String>>> placeLinkApplierMap = new HashMap<>();
    private final List<CSVRecord> quellenRecordsList = new LinkedList<>();
    private final List<CSVRecord> handSchriftenRecordList = new LinkedList<>();

    public CEIImporter(Path gesamtXML, Path quellenUndLiteratur, Path hssVerzeichnis)
        throws IOException, JDOMException {

        File file = gesamtXML.toFile();

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] bytes = fis.readAllBytes();
            String s = new String(bytes, StandardCharsets.UTF_8);
            String normalized = Normalizer.normalize(s, Normalizer.Form.NFC);
            try (ByteArrayInputStream bis = new ByteArrayInputStream(normalized.getBytes(StandardCharsets.UTF_8))) {
                SAXBuilder builder = new SAXBuilder();
                document = builder.build(bis);
                ceiGroup = document.getRootElement().getChild("text", CEI_NAMESPACE).getChild("group", CEI_NAMESPACE);
                textElements = ceiGroup.getChildren("text", CEI_NAMESPACE);

                processCSV(quellenUndLiteratur, this.quellenRecordsList);
                processCSV(hssVerzeichnis, this.handSchriftenRecordList);
            }
        }
    }

    private void processCSV(Path quellenUndLiteratur, List<CSVRecord> list) throws IOException {
        String defaultEncoding = "UTF-8";
        try (InputStream inputStream = new FileInputStream(quellenUndLiteratur.toFile());
            BOMInputStream bOMInputStream = new BOMInputStream(inputStream)) {
            ByteOrderMark bom = bOMInputStream.getBOM();
            String charsetName = bom == null ? defaultEncoding : bom.getCharsetName();
            try (InputStreamReader isr = new InputStreamReader(bOMInputStream, charsetName);
                BufferedReader br = new BufferedReader(isr)) {
                String csvAsString = br.lines().collect(Collectors.joining("\n"));
                String normalize = Normalizer.normalize(csvAsString, Normalizer.Form.NFC);
                try (StringReader sr = new StringReader(normalize)) {
                    Iterable<CSVRecord> records = CSVFormat.DEFAULT
                        .builder()
                        .setIgnoreEmptyLines(true)
                        .setDelimiter(";")
                        .setQuote('"')
                        .setHeader()
                        .setAllowMissingColumnNames(true)
                        .setSkipHeaderRecord(true)
                        .setNullString("")
                        .build().parse(sr);
                    records.forEach(list::add);
                }
            }
        }
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

    public List<Organization> getOrganizations() {
        return organizations;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public HashMap<EntityLink, List<Consumer<String>>> getPersonLinkApplierMap() {
        return personLinkApplierMap;
    }

    public HashMap<EntityLink, List<Consumer<String>>> getOrganizationLinkApplierMap() {
        return organizationLinkApplierMap;
    }

    public HashMap<EntityLink, List<Consumer<String>>> getPlaceLinkApplierMap() {
        return placeLinkApplierMap;
    }

    public HashMap<Regest, Element> getRegestTextMap() {
        return regestTextMap;
    }

    public void runImport() {
        quellenRecordsList.forEach(record -> {
            RegestSource regestSource = new RegestSource();

            Optional.ofNullable(record.get(RECORD_LONG_TITLE))
                .filter(Predicate.not(String::isBlank))
                .ifPresent(regestSource::setLongTitle);

            Optional.ofNullable(record.get(RECORD_SHORT_TITLE))
                .filter(Predicate.not(String::isBlank))
                .ifPresent(regestSource::setShortTitle);

            if (regestSource.getShortTitle() == null && regestSource.getLongTitle() == null) {
                LOGGER.warn("RegestSource has no title: " + record);
                return;
            }

            IdentifierType recordKey = new IdentifierType("key", record.get(RECORD_KEY));
            regestSource.getIdentifier().add(recordKey);

            QUELLEN_LINK_COL.forEach(linkType -> {
                Optional.ofNullable(record.get(linkType))
                    .filter(Predicate.not(String::isBlank))
                    .ifPresent(link -> {
                        regestSource.getUrls().add(new URLType(linkType, link));
                    });
            });

            this.keyRegestSourceHashMap.put(recordKey.getIdentifier(), regestSource);
        });

        handSchriftenRecordList.forEach(record -> {
            Manuscript manuscript = new Manuscript();

            Optional.ofNullable(record.get(RECORD_N))
                .filter(Predicate.not(String::isBlank))
                .ifPresent(manuscript::setId);

            if (manuscript.getId() == null) {
                LOGGER.warn("Manuscript has no id: " + record);
                return;
            }

            Optional.ofNullable(record.get(RECORD_SIGNATUR))
                .filter(Predicate.not(String::isBlank))
                .ifPresent(manuscript::setShelfmark);

            Optional.ofNullable(record.get(RECORD_DATIERUNG))
                .filter(Predicate.not(String::isBlank))
                .ifPresent(manuscript::setDate);

            HANDSCHRIFTEN_LINK_COL.forEach(linkType -> {
                Optional.ofNullable(record.get(linkType))
                    .filter(Predicate.not(String::isBlank))
                    .ifPresent(link -> {
                        manuscript.getUrls().add(new URLType(linkType, link));
                    });
            });

            this.keyManuscriptHashMap.put(manuscript.getId(), manuscript);
        });

        textElements.forEach(currentTextElement -> {
            try {
                Element textElement = currentTextElement.clone();

                Element currentBodyElement = textElement.getChild("body", CEI_NAMESPACE);

                Regest regest = new Regest();
                extractIdno(currentBodyElement, regest);
                extractIssuedPlace(currentBodyElement, regest);
                extractOtherPlaces(currentBodyElement, regest);
                extractIssuer(currentBodyElement, regest);
                extractRecipient(currentBodyElement, regest);
                extractIssuedDate(currentBodyElement, regest);
                extractInitium(currentBodyElement, regest);
                extractUeberlieferungsform(currentBodyElement, regest);
                extractPersonParagraph(currentBodyElement, "PontifikatPP", regest::setPontifikatPP, regest);
                extractPersonParagraph(currentBodyElement, "PontifikatAEP", regest::setPontifikatAEP, regest);
                extractOtherPersons(currentBodyElement, regest);
                extractOtherOrganizations(currentBodyElement, regest);

                regests.add(regest);
                regestTextMap.put(regest, textElement);
            } catch (Exception e) {
                LOGGER.error("Error in document: {}", new XMLOutputter().outputString(currentTextElement));
                throw e;
            }
        });
    }

    private void extractOtherPersons(Element currentBodyElement, Regest regest) {
        List<Element> otherPersNames = getXpathList(
            ".//cei:persName[not(ancestor::cei:issuer) and not(ancestor::cei:recipient) and not(ancestor::cei:p[@type='PontifikatPP' or @type='PontifikatAEP'])]",
            currentBodyElement);
        otherPersNames.forEach(otherPersName -> {
            extractPerson(otherPersName, p -> {
                regest.getBodyPersons().add(p);
                personLinkApplierMap.computeIfAbsent(p, k -> new ArrayList<>()).add(s -> {
                    otherPersName.setAttribute("key", s);
                });
            }, regest.getIdno());
        });
    }

    private void extractOtherPlaces(Element currentBodyElement, Regest regest) {
        List<Element> otherPlaces = getXpathList(".//cei:placeName[not(ancestor::cei:issued)]", currentBodyElement);
        otherPlaces.forEach(otherPlace -> {
            extractPlace(otherPlace, p -> {
                regest.getBodyPlaces().add(p);
                placeLinkApplierMap.computeIfAbsent(p, k -> new ArrayList<>()).add(s -> {
                    otherPlace.setAttribute("key", s);
                });
            }, regest.getIdno());
        });
    }

  private void extractOtherOrganizations(Element currentBodyElement, Regest regest) {
        List<Element> otherOrganizations = getXpathList(".//cei:orgName[not(ancestor::cei:issuer) and not(ancestor::cei:recipient)]", currentBodyElement);
        otherOrganizations.forEach(otherOrganization -> {
            extractOrganization(otherOrganization, p -> {
                regest.getBodyOrganizations().add(p);
                organizationLinkApplierMap.computeIfAbsent(p, k -> new ArrayList<>()).add(s -> {
                    otherOrganization.setAttribute("key", s);
                });
            }, regest.getIdno());
        });
    }


    private void extractPlace(Element placeName, Consumer<EntityLink> placeApplier, String regestId) {
        String flattenName = flattenText(placeName);
        String normalizedName = placeName.getAttributeValue("reg");
        String key = placeName.getAttributeValue("key");

        List<Place> places = key != null ? getPlaces().stream().filter(p -> {
            Optional<IdentifierType> optionalId
                = p.getIdentifier().stream().filter(id -> id.getType().equals("key")).findFirst();

            if (optionalId.isPresent()) {
                IdentifierType id = optionalId.get();
                return id.getIdentifier().equals(key);
            }

            return false;
        }).toList() : List.of();

      EntityLink pl = new EntityLink(EntityLink.Type.PLACE);
        pl.setLabel(flattenName);

        Place match = null;
        if (places.size() == 1) {
            match = places.get(0);
            if (normalizedName != null) {
                match.setDisplayName(normalizedName);
            }
            applyIds(placeName, match.getIdentifier());
        } else if (places.size() == 0) {
            if (key != null) {
                match = new Place();
                match.setDisplayName(Objects.requireNonNullElse(normalizedName, flattenName));
                match.setIdentifier(Stream.of(key).map(k -> new IdentifierType("key", k)).collect(Collectors.toList()));
                applyIds(placeName, match.getIdentifier());
                getPlaces().add(match);
            }
        } else {
            this.REPORT_MESSAGES.add("Can not distinguish between the places "
                + places.stream().map(
                    place -> place.getIdentifier().stream().map(IdentifierType::toString).collect(Collectors.joining())
                        + "-" + place.getDisplayName())
                    .collect(Collectors.joining()));
            LOGGER.error("Can not distinguish between the persons "
                + places.stream().map(Place::getDisplayName).collect(Collectors.joining()));
            return;
        }
        if (match == null) {
          LOGGER.info("Place(" + flattenName + ") has no key in regest (" + regestId + "), no object created");
          return;
        }
        List<EntityLink> ll = this.placeLinksHashMap.computeIfAbsent(match, (a) -> new LinkedList<>());
        ll.add(pl);
        placeApplier.accept(pl);
    }

    public void applyIds(Element element, List<IdentifierType> list) {
        String idAttr = element.getAttributeValue("id");
        if (idAttr != null) {
            Stream.of(idAttr.split("[|]")).forEach(idPair -> {
                String[] split = idPair.split(":", 2);
                String type = split[0];
                String value = split[1];
                IdentifierType identifierTypePair = new IdentifierType(type, value);
                if (!list.contains(identifierTypePair)) {
                    list.add(identifierTypePair);
                }
            });
        }
    }

    private void extractPerson(Element persName, Consumer<EntityLink> regestApplier, String regestId) {
        String flattenName = flattenText(persName);
        String normalizedName = persName.getAttributeValue("reg");

        String key = persName.getAttributeValue("key");

        List<Person> people = key != null ? getPersons().stream().filter(p -> {
            Optional<IdentifierType> optionalId
                = p.getIdentifier().stream().filter(id -> id.getType().equals("key")).findFirst();

            if (optionalId.isPresent()) {
                IdentifierType id = optionalId.get();
                return id.getIdentifier().equals(key);
            }

            return false;
        }).toList() : List.of();

      EntityLink pl = new EntityLink(EntityLink.Type.PERSON);
        pl.setLabel(flattenName);

        Person match = null;
        if (people.size() == 1) {
            match = people.get(0);
            if (normalizedName != null) {
                match.setDisplayName(normalizedName);
            }
            applyIds(persName, match.getIdentifier());
        } else if (people.isEmpty()) {
            if (key != null) {
                match = new Person();
                match.setDisplayName(Objects.requireNonNullElse(normalizedName, flattenName));
                match.setIdentifier(Stream.of(key).map(k -> new IdentifierType("key", k)).collect(Collectors.toList()));
                applyIds(persName, match.getIdentifier());
                getPersons().add(match);
            }
        } else {
            this.REPORT_MESSAGES.add("Can not distinguish between the persons "
                + people.stream().map(person -> person.getIdentifier().stream().map(IdentifierType::toString)
                    .collect(Collectors.joining()) + "-" + person.getDisplayName()).collect(Collectors.joining()));
            LOGGER.error("Can not distinguish between the persons "
                + people.stream().map(Person::getDisplayName).collect(Collectors.joining()));
            return;
        }
        if (match == null) {
            LOGGER.info("Person (" + flattenName + ") has no key in regest (" + regestId + "), no object created");
            return;
        }
        List<EntityLink> ll = this.personLinksHashMap.computeIfAbsent(match, (a) -> new LinkedList<>());
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
        List<Element> foreign = getXpathList(".//cei:diplomaticAnalysis/cei:incipit/cei:foreign", currentBodyElement);
        foreign.stream().map(this::flattenText).forEach(regest.getInitium()::add);
    }

    private void extractUeberlieferungsform(Element currentBodyElement, Regest regest) {
        Element ueberlieferungsform
            = getXpathFirst(".//cei:diplomaticAnalysis/cei:p[@type='Überlieferungsform']", currentBodyElement);
        if (ueberlieferungsform != null) {
            String ueberlieferungsformText = flattenText(ueberlieferungsform);
            regest.setUeberlieferungsform(ueberlieferungsformText);
        }
    }

    private void extractPersonParagraph(Element currentBodyElement, String pType, Consumer<EntityLink> applyFn,
        Regest regest) {
        Element paragraphElement = getXpathFirst(".//cei:p[@type='" + pType + "']", currentBodyElement);
        if (paragraphElement != null) {
            Element persName = paragraphElement.getChild("persName", CEI_NAMESPACE);
            if (persName == null) {
                return;
            }
            extractPerson(persName, (link) -> {
                applyFn.accept(link);
                personLinkApplierMap.computeIfAbsent(link, (k) -> new LinkedList<>())
                    .add((id) -> persName.setAttribute("key", id));
            }, regest.getIdno());
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
            extractPlace(issuedPlaceName, (issued) -> {
                regest.setIssuedPlace(issued);
                placeLinkApplierMap.computeIfAbsent(issued, (k) -> new LinkedList<>())
                    .add((id) -> issuedPlaceName.setAttribute("key", id));
            }, regest.getIdno());
        }
    }

    public HashMap<Person, List<EntityLink>> getPersonLinksHashMap() {
        return personLinksHashMap;
    }

    public HashMap<Place, List<EntityLink>> getPlaceLinksHashMap() {
        return placeLinksHashMap;
    }

    public HashMap<Organization, List<EntityLink>> getOrganizationLinksHashMap() {
        return organizationLinksHashMap;
    }

    private void extractIssuer(Element currentBodyElement, Regest regest) {
        List<Element> issuerEntityElementList = getXpathList(
            ".//cei:issuer/*[local-name()='persName' or local-name()='orgName']", currentBodyElement);
        for(Element issuerEntityElement : issuerEntityElementList){
          if (issuerEntityElement.getName().equals("persName")) {
            extractPerson(issuerEntityElement, (p) -> {
              regest.getIssuers().add(p);
              personLinkApplierMap.computeIfAbsent(p, k -> new ArrayList<>()).add(s -> {
                issuerEntityElement.setAttribute("key", s);
              });
            }, regest.getIdno());
          } else if (issuerEntityElement.getName().equals("orgName")) {
            extractOrganization(issuerEntityElement, (o) -> {
              regest.getIssuers().add(o);
              organizationLinkApplierMap.computeIfAbsent(o, k -> new ArrayList<>()).add(s -> {
                issuerEntityElement.setAttribute("key", s);
              });
            }, regest.getIdno());

          }
        }
        if(issuerEntityElementList.isEmpty()){
          LOGGER.info("NO ISSUER SET (" + regest.getIdno() + ")");
        }
    }

    private void extractOrganization(Element orgName, Consumer<EntityLink> regestApplier, String regestId) {
        String flattenName = flattenText(orgName);
        String normalizedName = orgName.getAttributeValue("reg");

        String key = orgName.getAttributeValue("key");

        List<Organization> orgs = key != null ? getOrganizations().stream().filter(p -> {
            Optional<IdentifierType> optionalId
                = p.getIdentifier().stream().filter(id -> id.getType().equals("key")).findFirst();

            if (optionalId.isPresent()) {
                IdentifierType id = optionalId.get();
                return id.getIdentifier().equals(key);
            }

            return false;
        }).toList() : List.of();

      EntityLink ol = new EntityLink(EntityLink.Type.ORGANIZATION);
        ol.setLabel(flattenName);

        Organization match = null;
        if (orgs.size() == 1) {
            match = orgs.get(0);
            if (normalizedName != null) {
                match.setDisplayName(normalizedName);
            }
            applyIds(orgName, match.getIdentifier());
        } else if (orgs.isEmpty()) {
            if (key != null) {
                match = new Organization();
                match.setDisplayName(Objects.requireNonNullElse(normalizedName, flattenName));
                match.setIdentifier(Stream.of(key).map(k -> new IdentifierType("key", k)).collect(Collectors.toList()));
                applyIds(orgName, match.getIdentifier());
                getOrganizations().add(match);
            }
        } else {
            this.REPORT_MESSAGES.add("Can not distinguish between the Organizations"
                + orgs.stream().map(person -> person.getIdentifier().stream().map(IdentifierType::toString)
                    .collect(Collectors.joining()) + "-" + person.getDisplayName()).collect(Collectors.joining()));
            LOGGER.error("Can not distinguish between the Organizations "
                + orgs.stream().map(Organization::getDisplayName).collect(Collectors.joining()));
            return;
        }
        if (match == null) {
            LOGGER.info("Organization(" + flattenName + ") has no key in regest (" + regestId + "), no object created");
            return;
        }
        List<EntityLink> ll = this.organizationLinksHashMap.computeIfAbsent(match, (a) -> new LinkedList<>());
        ll.add(ol);
        regestApplier.accept(ol);
    }

    private void extractRecipient(Element currentBodyElement, Regest regest) {
        List<Element> entityElementList = getXpathList(".//cei:recipient/*[local-name()='persName' or local-name()='orgName']", currentBodyElement);

        if (entityElementList.isEmpty()) {
            LOGGER.info("NO recipient SET (" + regest.getIdno() + ")");
        }

        for (Element entityElement : entityElementList) {
          if (entityElement.getName().equals("persName")) {
            extractPerson(entityElement, (p) -> {
              regest.getRecipients().add(p);
              personLinkApplierMap.computeIfAbsent(p, k -> new ArrayList<>()).add(s -> {
                entityElement.setAttribute("key", s);
              });
            }, regest.getIdno());
          } else if (entityElement.getName().equals("orgName")) {
            extractOrganization(entityElement, (o) -> {
              regest.getRecipients().add(o);
              organizationLinkApplierMap.computeIfAbsent(o, k -> new ArrayList<>()).add(s -> {
                entityElement.setAttribute("key", s);
              });
            }, regest.getIdno());
          }
        }
    }



    private Element getXpathFirst(String xpathString, Element context) {
        XPathExpression<Element> r
            = XPathFactory.instance().compile(xpathString, Filters.element(), null, CEI_NAMESPACE);
        return r.evaluateFirst(context);
    }

    private List<Element> getXpathList(String xpathString, Element context) {
        XPathExpression<Element> r
            = XPathFactory.instance().compile(xpathString, Filters.element(), null, CEI_NAMESPACE);
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

    public HashMap<String, RegestSource> getKeyRegestSourceHashMap() {
        return keyRegestSourceHashMap;
    }

    public HashMap<String, Manuscript> getKeyManuscriptHashMap() {
        return keyManuscriptHashMap;
    }
}