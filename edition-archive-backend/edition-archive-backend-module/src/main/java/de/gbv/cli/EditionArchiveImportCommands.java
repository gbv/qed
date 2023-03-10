package de.gbv.cli;

import de.gbv.metadata.CEIImporter;
import de.gbv.metadata.model.EntityLink;
import de.gbv.metadata.model.Manuscript;
import de.gbv.metadata.model.Organization;
import de.gbv.metadata.model.Person;
import de.gbv.metadata.model.Place;
import de.gbv.metadata.model.Regest;
import de.gbv.metadata.model.RegestSource;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.output.XMLOutputter;
import org.mycore.access.MCRAccessException;
import org.mycore.common.MCRException;
import org.mycore.common.MCRPersistenceException;
import org.mycore.common.config.MCRConfiguration2;
import org.mycore.datamodel.classifications2.MCRCategoryDAO;
import org.mycore.datamodel.classifications2.MCRCategoryDAOFactory;
import org.mycore.datamodel.classifications2.MCRCategoryID;
import org.mycore.datamodel.metadata.MCRDerivate;
import org.mycore.datamodel.metadata.MCRMetaClassification;
import org.mycore.datamodel.metadata.MCRMetaIFS;
import org.mycore.datamodel.metadata.MCRMetaLinkID;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.datamodel.niofs.MCRPath;
import org.mycore.frontend.cli.annotation.MCRCommand;
import org.mycore.frontend.cli.annotation.MCRCommandGroup;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.gbv.metadata.CEIImporter.createMetadata;

@MCRCommandGroup(name = "Import Commands")
public class EditionArchiveImportCommands {

    @MCRCommand(syntax = "import regests from cei file {0} and source {1} and manuscript {2}", help = "")
    public static void importRegestsFromCEI(String ceiFilePath, String sourceCSVPath, String manuscriptCSVPath)
        throws IOException, JDOMException, MCRAccessException {
        CEIImporter ceiImporter = new CEIImporter(Paths.get(ceiFilePath),
            Paths.get(sourceCSVPath),
            Paths.get(manuscriptCSVPath));
        ceiImporter.runImport();

      HashMap<String, RegestSource> keyRegestSourceHashMap = ceiImporter.getKeyRegestSourceHashMap();

      Set<Map.Entry<String, RegestSource>> keyRegestSourceEntries = keyRegestSourceHashMap.entrySet();
      for (Map.Entry<String, RegestSource> entry:keyRegestSourceEntries) {
        RegestSource source = entry.getValue();
        MCRObject sourceObject = new MCRObject();
        MCRObjectID sourceMyCoReId = MCRObjectID.getNextFreeId("gpo", "source");
        sourceObject.setId(sourceMyCoReId);
        sourceObject.setSchema("datamodel-source.xsd");
        Element metadata = createMetadata(source, RegestSource.class.getName(), "source");
        sourceObject.getMetadata().setFromDOM(metadata);
        MCRMetadataManager.create(sourceObject);
      }

      HashMap<String, Manuscript> keyManuscriptHashMap = ceiImporter.getKeyManuscriptHashMap();
      for (Map.Entry<String, Manuscript> entry:keyManuscriptHashMap.entrySet()) {
        Manuscript manuscript = entry.getValue();
        MCRObject manuscriptObject = new MCRObject();
        MCRObjectID manuscriptMyCoReId = MCRObjectID.getNextFreeId("gpo", "manuscript");
        manuscriptObject.setId(manuscriptMyCoReId);
        manuscriptObject.setSchema("datamodel-manuscript.xsd");
        Element metadata = createMetadata(manuscript, Manuscript.class.getName(), "manuscript");
        manuscriptObject.getMetadata().setFromDOM(metadata);
        MCRMetadataManager.create(manuscriptObject);
      }

      HashMap<Regest, Element> regestTextMap = ceiImporter.getRegestTextMap();
      HashMap<EntityLink, List<Consumer<String>>> personLinkApplierMap = ceiImporter.getPersonLinkApplierMap();
      HashMap<Person, List<EntityLink>> personLinksHashMap = ceiImporter.getPersonLinksHashMap();
        for (Person person : ceiImporter.getPersons()) {
            MCRObject personObject = new MCRObject();
            MCRObjectID personMyCoReId = MCRObjectID.getNextFreeId("gpo", "person");
            personObject.setId(personMyCoReId);
            personObject.setSchema("datamodel-person.xsd");

            Element metadata = createMetadata(person, Person.class.getName(), "person");
            personObject.getMetadata().setFromDOM(metadata);
            MCRMetadataManager.create(personObject);
            for (EntityLink link : personLinksHashMap.get(person)) {
                Optional.ofNullable(personLinkApplierMap.get(link))
                    .ifPresent(list -> {
                      list.forEach(c -> c.accept(personMyCoReId.toString()));
                    });
              link.setMycoreId(personMyCoReId.toString());
            }
        }

      HashMap<Place, List<EntityLink>> placeLinksHashMap = ceiImporter.getPlaceLinksHashMap();
      HashMap<EntityLink, List<Consumer<String>>> placeLinkApplierMap = ceiImporter.getPlaceLinkApplierMap();
      for (Place place : ceiImporter.getPlaces()) {
        MCRObject personObject = new MCRObject();
        MCRObjectID placeMyCoReId = MCRObjectID.getNextFreeId("gpo", "place");
        personObject.setId(placeMyCoReId);
        personObject.setSchema("datamodel-place.xsd");

        Element metadata = createMetadata(place, Place.class.getName(), "place");
        personObject.getMetadata().setFromDOM(metadata);
        MCRMetadataManager.create(personObject);
        for (EntityLink link : placeLinksHashMap.get(place)) {
            Optional.ofNullable(placeLinkApplierMap.get(link))
                .ifPresent(list -> {
                  list.forEach(c -> c.accept(placeMyCoReId.toString()));
                });
            link.setMycoreId(placeMyCoReId.toString());
        }
      }

      HashMap<Organization, List<EntityLink>> organizationLinksHashMap = ceiImporter.getOrganizationLinksHashMap();
      HashMap<EntityLink, List<Consumer<String>>> organizationLinkApplierMap = ceiImporter.getOrganizationLinkApplierMap();
      for (Organization organization : ceiImporter.getOrganizations()) {
        MCRObject organizationObject = new MCRObject();
        MCRObjectID organizationMyCoReId = MCRObjectID.getNextFreeId("gpo", "organization");
        organizationObject.setId(organizationMyCoReId);
        organizationObject.setSchema("datamodel-organization.xsd");

        Element metadata = createMetadata(organization, Organization.class.getName(), "organization");
        organizationObject.getMetadata().setFromDOM(metadata);
        MCRMetadataManager.create(organizationObject);
        for (EntityLink link : organizationLinksHashMap.get(organization)) {
            Optional.ofNullable(organizationLinkApplierMap.get(link))
                .ifPresent(list -> {
                  list.forEach(c -> c.accept(organizationMyCoReId.toString()));
                });
            link.setMycoreId(organizationMyCoReId.toString());
        }
      }

      for (Regest regest : ceiImporter.getRegests()) {
            MCRObject object = new MCRObject();
            object.setId(MCRObjectID.getNextFreeId("gpo", "regest"));
            object.setSchema("datamodel-regest.xsd");

            Element metadata = createMetadata(regest, Regest.class.getName(), "regest");
            object.getMetadata().setFromDOM(metadata);
            MCRMetadataManager.create(object);

            MCRDerivate derivate = createDerivate(object.getId(), getClassifications("derivate_types:content"));
            MCRObjectID derivateId = derivate.getId();

            MCRPath regestPath = MCRPath.getPath(derivateId.toString(), "/regest.xml");
            Document regestDocument = new Document(regestTextMap.get(regest));

            try(OutputStream os = Files.newOutputStream(regestPath, StandardOpenOption.WRITE, StandardOpenOption.CREATE)){
                new XMLOutputter().output(regestDocument, os);
            }
        }

        Files.writeString(Paths.get(ceiFilePath).getParent().resolve("import.log"),
            ceiImporter.REPORT_MESSAGES.stream().collect(Collectors.joining(System.lineSeparator())));
    }

    private static MCRDerivate createDerivate(MCRObjectID objectID, List<MCRMetaClassification> classifications) throws MCRPersistenceException, MCRAccessException {

        MCRObjectID derivateID = getNewCreateDerivateID(objectID);
        MCRDerivate derivate = new MCRDerivate();
        derivate.setId(derivateID);
        derivate.getDerivate().getClassifications().addAll(classifications);

        String schema = MCRConfiguration2.getString("MCR.Metadata.Config.derivate").orElse("datamodel-derivate.xml").replaceAll(".xml", ".xsd");
        derivate.setSchema(schema);

        MCRMetaLinkID linkId = new MCRMetaLinkID();
        linkId.setSubTag("linkmeta");
        linkId.setReference(objectID, null, null);
        derivate.getDerivate().setLinkMeta(linkId);

        MCRMetaIFS ifs = new MCRMetaIFS();
        ifs.setSubTag("internal");
        ifs.setSourcePath(null);
        derivate.getDerivate().setInternals(ifs);

        MCRMetadataManager.create(derivate);

        return derivate;
    }

    private static MCRObjectID getNewCreateDerivateID(MCRObjectID objId) {
        String projectID = objId.getProjectId();
        return MCRObjectID.getNextFreeId(projectID + "_derivate");
    }


    private static List<MCRMetaClassification> getClassifications(String classifications) {
        final MCRCategoryDAO dao = MCRCategoryDAOFactory.getInstance();
        final List<MCRCategoryID> categoryIDS = Stream.of(classifications).filter(Objects::nonNull).filter(Predicate.not(String::isBlank)).flatMap(c -> Stream.of(c.split(","))).filter(Predicate.not(String::isBlank)).filter(cv -> cv.contains(":")).map(classValString -> {
            final String[] split = classValString.split(":");
            return new MCRCategoryID(split[0], split[1]);
        }).collect(Collectors.toList());

        if (!categoryIDS.stream().allMatch(dao::exist)) {
            final String parsedIDS = categoryIDS.stream().map(Object::toString).collect(Collectors.joining(","));
            throw new MCRException(String.format(Locale.ROOT, "One of the Categories \"%s\" was not found", parsedIDS));
        }

        return categoryIDS.stream().map(category -> new MCRMetaClassification("classification", 0, null, category.getRootID(), category.getID())).collect(Collectors.toList());
    }

}
