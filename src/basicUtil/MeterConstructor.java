package basicUtil; /**
 * Created by jiachen on 04/12/15.
 */

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;


public class MeterConstructor {
    public DocumentBuilderFactory docFactory;
    public DocumentBuilder docBuilder;
    public Document doc;

    public MeterConstructor() {
        try {
            docFactory = DocumentBuilderFactory.newInstance();
            docBuilder = docFactory.newDocumentBuilder();

            doc = docBuilder.newDocument();

            Element rootElement = doc.createElement("meter");
            doc.appendChild(rootElement);

            Attr attr = doc.createAttribute("xmlns");
            attr.setValue("urn:opendaylight:flow:inventory");
            rootElement.setAttributeNode(attr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Element createElement(String id) {
        Element ele = doc.createElement(id);
        return ele;
    }

    public Text createText(String value) {
        Text text = doc.createTextNode(value);
        return text;
    }

    public void addChild(Element father, Node son) {
        father.appendChild(son);
    }

    public Element getElement(String value) {
        Element ele = doc.getElementById(value);
        return ele;
    }

    public String getXMLString() {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);

            transformer.transform(source, result);
            return sw.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setText(Element container, String value) {
        if (container.getChildNodes().getLength() == 0) {
            Text text = doc.createTextNode(value);
            container.appendChild(text);
        }
        else {
            Text text = (Text) container.getChildNodes().item(0);
            text.replaceWholeText(value);
        }
    }

    public Element getElement(Element container, String tagName) {
        Element ele;
        if (container.getElementsByTagName(tagName).getLength() == 0) {
            ele = doc.createElement(tagName);
            container.appendChild(ele);
        }
        else {
            ele = (Element) container.getElementsByTagName(tagName).item(0);
        }
        return ele;
    }

    public void setMeterID(String value) {
        Element ele = getElement((Element) doc.getElementsByTagName("meter").item(0), "meter-id");
        setText(ele, value);
    }

    public void setMeterName(String value) {
        Element ele = getElement((Element) doc.getElementsByTagName("meter").item(0), "meter-name");
        setText(ele, value);
    }

    public void setFlag(String value) {
        Element ele = getElement((Element) doc.getElementsByTagName("meter").item(0), "flags");
        setText(ele, value);
    }

    public Element getByOrder(Element container, String name, int order) {
        Element multiElements = getElement(container, name + "s");
        NodeList list = multiElements.getChildNodes();
        for (int i = 0; i < list.getLength(); ++i) {
            Element x = (Element) list.item(i);
            if (x.getElementsByTagName("band-id").getLength() == 0) continue;
            Element orderElement = (Element) x.getElementsByTagName("band-id").item(0);
            if (orderElement.getFirstChild().getNodeValue().equals(Integer.toString(order))) {
                return x;
            }
        }
        Element newInstruction = doc.createElement(name);
        multiElements.appendChild(newInstruction);
        Element newOrder = getElement(newInstruction, "band-id");
        setText(newOrder, Integer.toString(order));
        return newInstruction;
    }

    public void setMeterBandType(int bandID, String type, int dropRate, int dropBurstSize) {
        Element bandHeader = getByOrder((Element) doc.getElementsByTagName("meter").item(0), "meter-band-header", bandID);
        Element bandType = getElement(bandHeader, "meter-band-types");
        Element flag = getElement(bandType, "flags");
        setText(flag, type);
        Element dropRateElement = getElement(bandHeader, "drop-rate");
        setText(dropRateElement, Integer.toString(dropRate));
        Element dropBurstSizeElement = getElement(bandHeader, "drop-burst-size");
        setText(dropBurstSizeElement, Integer.toString(dropBurstSize));
    }
}
