package de.gbv.cli;

import de.gbv.metadata.CEIImporter;
import de.gbv.metadata.MetaJSONHelper;
import de.gbv.metadata.Regest;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.output.XMLOutputter;
import org.mycore.access.MCRAccessException;
import org.mycore.common.MCRException;
import org.mycore.common.MCRPersistenceException;
import org.mycore.common.MCRUtils;
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
import org.mycore.datamodel.niofs.utils.MCRDerivateUtil;
import org.mycore.frontend.cli.annotation.MCRCommand;
import org.mycore.frontend.cli.annotation.MCRCommandGroup;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.AbstractMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.gbv.metadata.CEIImporter.createMetadata;

@MCRCommandGroup(name = "Import Commands")
public class EditionArchiveImportCommands {

    @MCRCommand(syntax = "import regests from cei file {0}", help = "")
    public static void importRegestsFromCEI(String ceiFilePath) throws IOException, JDOMException, MCRAccessException {
        CEIImporter ceiImporter = new CEIImporter(Paths.get(ceiFilePath));
        while (ceiImporter.hasNext()) {
            AbstractMap.SimpleEntry<Element, Regest> next = ceiImporter.next();
            MCRObject object = new MCRObject();
            object.setId(MCRObjectID.getNextFreeId("gpo", "regest"));
            object.setSchema("datamodel-regest.xsd");

            Element metadata = createMetadata(next.getValue());
            object.getMetadata().setFromDOM(metadata);
            MCRMetadataManager.create(object);

            MCRDerivate derivate = createDerivate(object.getId(), getClassifications("derivate_types:content"));
            MCRObjectID derivateId = derivate.getId();

            MCRPath regestPath = MCRPath.getPath(derivateId.toString(), "/regest.xml");
            Document regestDocument = new Document(next.getKey());

            try(OutputStream os = Files.newOutputStream(regestPath, StandardOpenOption.WRITE, StandardOpenOption.CREATE)){
                new XMLOutputter().output(regestDocument, os);
            }
        }
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
