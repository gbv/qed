import de.gbv.metadata.CEIImporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.JDOMException;
import org.jdom2.output.XMLOutputter;
import org.mycore.common.MCRTestCase;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

public class Test extends MCRTestCase {
    private static final Logger LOGGER = LogManager.getLogger();

    @org.junit.Test
    public void testTest() throws IOException, JDOMException {
        CEIImporter ceiImporter = new CEIImporter(Paths.get("../../import/formatted-2022-09-08_regesten_gesamt_red_vi-02.xml"));
        XMLOutputter xmlOutputter = new XMLOutputter();
        ceiImporter.runImport();
        ceiImporter.getPersons().forEach(LOGGER::info);
        ceiImporter.getPlaces().forEach(LOGGER::info);
    }

    @Override
    protected Map<String, String> getTestProperties() {
        Map<String, String> testProperties = super.getTestProperties();

        testProperties.put("MCR.Metadata.JSON.Type.AllowedClasses","de.gbv.metadata.model.Regest");

        return testProperties;
    }
}
