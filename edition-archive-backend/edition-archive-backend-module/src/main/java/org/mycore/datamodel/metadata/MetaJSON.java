package org.mycore.datamodel.metadata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jdom2.Element;
import org.mycore.common.MCRClassTools;
import org.mycore.common.MCRException;
import org.mycore.common.config.MCRConfiguration2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

public class MetaJSON<T> extends MCRMetaDefault {

    private static final String ALLOWED_TYPE_LIST_PROPERTY = "MCR.Metadata.JSON.Type.AllowedClasses";

    private static final List<String> ALLOWED_TYPES = MCRConfiguration2.getOrThrow(ALLOWED_TYPE_LIST_PROPERTY, MCRConfiguration2::splitValue).collect(Collectors.toList());
    private T object;

    public static final Gson getGson() {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").setPrettyPrinting().create();
        return gson;
    }

    public T getObject() {
        return this.object;
    }

    public void setObject(T object) {
        String clazz = object.getClass().getName();
        checkClass(clazz);
        this.object = object;
    }

    @Override
    public void setFromDOM(Element element) throws MCRException {
        super.setFromDOM(element);
        String text = element.getText();
        try {
            String clazz = element.getAttributeValue("class");
            checkClass(clazz);
            Class<T> aClass = (Class<T>) MCRClassTools.forName(clazz);
            this.object = getGson().fromJson(text, aClass);
        } catch (ClassNotFoundException e) {
            throw new MCRException(e);
        }
    }

    public Element createXML() throws MCRException {
        Element elm = super.createXML();
        String clazz = this.getObject().getClass().getName();
        checkClass(clazz);
        elm.setAttribute("class", clazz);
        String s = getGson().toJson(object);
        elm.setText(s);

        return elm;
    }

    private static void checkClass(String clazz) {
        if (!ALLOWED_TYPES.contains(clazz)) {
            throw new MCRException("The class " + clazz + " is not allowed to be a MetaJSON class (see " + ALLOWED_TYPE_LIST_PROPERTY + ")!");
        }
    }

    @Override
    public boolean isValid() throws MCRException {
        if (object == null) {
            throw new MCRException("Object is Invalid because MetaJSON object is null!");
        }

        Method validateMethod;
        try {
            validateMethod = object.getClass().getMethod("validate");
        } catch (NoSuchMethodException e) {
            return true;
        }

        Class<?> returnType = validateMethod.getReturnType();
        if (returnType.equals(boolean.class)) {
            try {
                return (boolean) validateMethod.invoke(object);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new MCRException(e);
            }
        } else {
            throw new MCRException("The validate method of the MetaJSON Object needs to return a boolean!");
        }
    }

    @Override
    public JsonObject createJSON() {
        JsonElement jsonElement = getGson().toJsonTree(object);
        return jsonElement.getAsJsonObject();
    }
}