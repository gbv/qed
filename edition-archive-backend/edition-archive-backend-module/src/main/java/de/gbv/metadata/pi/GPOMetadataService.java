package de.gbv.metadata.pi;

import java.util.Optional;

import org.mycore.datamodel.metadata.MCRBase;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MetaJSON;
import org.mycore.pi.MCRPIMetadataService;
import org.mycore.pi.MCRPersistentIdentifier;
import org.mycore.pi.doi.MCRDOIParser;
import org.mycore.pi.doi.MCRDigitalObjectIdentifier;
import org.mycore.pi.exceptions.MCRPersistentIdentifierException;

import de.gbv.metadata.MetaJSONHelper;
import de.gbv.metadata.model.Regest;

public class GPOMetadataService extends MCRPIMetadataService<MCRDigitalObjectIdentifier> {

    @Override
    public void insertIdentifier(MCRDigitalObjectIdentifier doi, MCRBase mcrBase, String additional)
        throws MCRPersistentIdentifierException {
        if (!(mcrBase instanceof MCRObject mcrObject)) {
            throw new MCRPersistentIdentifierException("Only MCRObjects are supported");
        }
        Regest regest = MetaJSONHelper.getMetaJsonObject(mcrObject, "regest");
        regest.setDoi(doi.asString());
        MetaJSONHelper.setMetaJsonObject(mcrObject, "regest", regest);
    }

    @Override
    public void removeIdentifier(MCRDigitalObjectIdentifier doi, MCRBase mcrBase, String additional) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<MCRPersistentIdentifier> getIdentifier(MCRBase mcrBase, String additional)
        throws MCRPersistentIdentifierException {
        if (!(mcrBase instanceof MCRObject mcrObject)) {
            throw new MCRPersistentIdentifierException("Only MCRObjects are supported");
        }
        Regest regest = MetaJSONHelper.getMetaJsonObject(mcrObject, "regest");
        String doi = regest.getDoi();
        MCRDOIParser parser = new MCRDOIParser();
        return parser.parse(doi).map(MCRDigitalObjectIdentifier.class::cast);
    }
}
