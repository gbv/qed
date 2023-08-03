package de.gbv.metadata.pi;

import java.net.URI;
import java.net.URISyntaxException;

import org.mycore.datamodel.metadata.MCRBase;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.pi.doi.MCRDOIService;

import de.gbv.metadata.MetaJSONHelper;
import de.gbv.metadata.model.Regest;

public class GPODOIService extends MCRDOIService {

    @Override
    public URI getRegisteredURI(MCRBase base) throws URISyntaxException {
        String registerURL = getRegisterURL();
        if (!(base instanceof MCRObject obj)) {
            throw new IllegalArgumentException("Only MCRObjects are supported");
        }

        Regest regest = MetaJSONHelper.getMetaJsonObject(obj, "regest");
        String idno = regest.getIdno();

        return new URI(registerURL + idno);
    }

}
