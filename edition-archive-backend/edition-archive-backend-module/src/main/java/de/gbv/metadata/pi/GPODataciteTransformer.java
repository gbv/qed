package de.gbv.metadata.pi;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.Text;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.mycore.common.MCRClassTools;
import org.mycore.common.MCRConstants;
import org.mycore.common.MCRException;
import org.mycore.common.content.MCRContent;
import org.mycore.common.content.MCRJDOMContent;
import org.mycore.common.content.transformer.MCRContentTransformer;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MetaJSON;
import org.xml.sax.SAXException;

import de.gbv.metadata.MetaJSONHelper;
import de.gbv.metadata.model.Regest;

public class GPODataciteTransformer extends MCRContentTransformer {
    private static Regest loadRegest(MCRContent mcrContent) throws IOException {
        MCRObject object;

        try {
            Document xml = mcrContent.asXML();
            object = new MCRObject(xml);
        } catch (JDOMException | SAXException e) {
            throw new MCRException(e);
        }

        Regest regest = MetaJSONHelper.getMetaJsonObject(object, "regest");
        return regest;
    }

    private static Document loadTemplate() {
        try (InputStream templateStream
            = MCRClassTools.getClassLoader().getResourceAsStream("reims-gpo-doi-template.xml")) {
            SAXBuilder saxBuilder = new SAXBuilder();
            return saxBuilder.build(templateStream);
        } catch (IOException | JDOMException e) {
            throw new MCRException("Could not load template", e);
        }
    }

    @Override
    public MCRContent transform(MCRContent mcrContent) throws IOException {
        Regest regestObject = loadRegest(mcrContent);
        Document template = loadTemplate();


        getXpathSingleElement(template, "/datacite:resource/datacite:identifier").setText(regestObject.getDoi());
        getXpathElements(template.getRootElement(), ".//datacite:nrReplace").forEach(e-> {
          int i = e.getParent().indexOf(e);
          e.getParent().addContent(i, new Text(regestObject.getIdno()));
          e.detach();
        });

        return new MCRJDOMContent(template);
    }

    public Element getXpathSingleElement(Document element, String xpath) {
        XPathExpression<Element> expr
            = XPathFactory.instance().compile(xpath, Filters.element(), null, MCRConstants.XLINK_NAMESPACE,
                Namespace.getNamespace("datacite", "http://datacite.org/schema/kernel-4"));

        return expr.evaluateFirst(element);
    }

    public List<Element> getXpathElements(Element element, String xpath) {
        XPathExpression<Element> expr
            = XPathFactory.instance().compile(xpath, Filters.element(), null, MCRConstants.XLINK_NAMESPACE,
                Namespace.getNamespace("datacite", "http://datacite.org/schema/kernel-4"));

        return expr.evaluate(element);
    }
}
