package de.gbv.metadata;

import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MetaJSON;

public class MetaJSONHelper {

    public static <T> T getMetaJsonObject(MCRObject object, String tag){
        MetaJSON<T> regest = (MetaJSON<T>)object.getMetadata().getMetadataElement("def." + tag).getElementByName(tag);
        return regest.getObject();
    }

    public static <T> void setMetaJsonObject(MCRObject object, String tag, T value){
        MetaJSON<T> regest = (MetaJSON<T>)object.getMetadata().getMetadataElement("def." + tag).getElementByName(tag);
        regest.setObject(value);
    }
}
