package de.gbv.cli;

import de.gbv.metadata.CEIImporter;
import de.gbv.metadata.MetaJSONHelper;
import de.gbv.metadata.Regest;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.output.XMLOutputter;
import org.mycore.access.MCRAccessException;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;
import org.mycore.frontend.cli.annotation.MCRCommand;
import org.mycore.frontend.cli.annotation.MCRCommandGroup;

import java.io.IOException;
import java.nio.file.Paths;

import static de.gbv.metadata.CEIImporter.createMetadata;

@MCRCommandGroup(name = "Import Commands")
public class EditionArchiveImportCommands {

    @MCRCommand(syntax = "import regests from cei file {0}",
            help = "")
    public static void importRegestsFromCEI(String ceiFilePath) throws IOException, JDOMException, MCRAccessException {
        CEIImporter ceiImporter = new CEIImporter(Paths.get(ceiFilePath));
        while (ceiImporter.hasNext()) {
            Regest next = ceiImporter.next();
            MCRObject object = new MCRObject();
            object.setId(MCRObjectID.getNextFreeId("gpo", "regest"));
            object.setSchema("datamodel-regest.xsd");

            Element metadata = createMetadata(next);
            object.getMetadata().setFromDOM(metadata);
            MCRMetadataManager.create(object);
        }
    }

}
