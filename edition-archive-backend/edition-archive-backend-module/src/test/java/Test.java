import de.gbv.metadata.CEIImporter;
import de.gbv.metadata.MetaJSONHelper;
import de.gbv.metadata.Regest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.output.XMLOutputter;
import org.mycore.common.MCRTestCase;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.Map;

import static de.gbv.metadata.CEIImporter.createMetadata;

public class Test extends MCRTestCase {
    private static final Logger LOGGER = LogManager.getLogger();

    @org.junit.Test
    public void testTest() throws IOException, JDOMException {
        CEIImporter ceiImporter = new CEIImporter(Paths.get("../../import/2022-08-18_regesten_gesamt_red_v-09-formatted.xml"));
        XMLOutputter xmlOutputter = new XMLOutputter();

        while (ceiImporter.hasNext()) {
            AbstractMap.SimpleEntry<Element, Regest> next = ceiImporter.next();
            LOGGER.info(next.getValue());
        }
    }

    @Override
    protected Map<String, String> getTestProperties() {
        Map<String, String> testProperties = super.getTestProperties();

        testProperties.put("MCR.Metadata.JSON.Type.AllowedClasses","de.gbv.metadata.Regest");

        return testProperties;
    }
}
