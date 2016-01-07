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


public class FlowConstructor {
    private DocumentBuilderFactory docFactory;
    private DocumentBuilder docBuilder;
    public Document doc;

    public FlowConstructor() {
        try {
            docFactory = DocumentBuilderFactory.newInstance();
            docBuilder = docFactory.newDocumentBuilder();

            doc = docBuilder.newDocument();

            Element rootElement = doc.createElement("flow");
            doc.appendChild(rootElement);

            Attr attr = doc.createAttribute("xmlns");
            attr.setValue("urn:opendaylight:flow:inventory");
            rootElement.setAttributeNode(attr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
    @param priority The priority of this flow.
    @param hardTimeOut Hard time out.
    @param idleTimeOut Idle time out.
    @param flowName Name of this flow.
    @param flowID The ID of this flow.
    @param tableID The table ID, where this flow installed.
     */
    public FlowConstructor(int priority, int hardTimeOut, int idleTimeOut, String flowName, String tableID, String flowID) {
        try {
            docFactory = DocumentBuilderFactory.newInstance();
            docBuilder = docFactory.newDocumentBuilder();

            doc = docBuilder.newDocument();

            Element rootElement = doc.createElement("flow");
            doc.appendChild(rootElement);

            Attr attr = doc.createAttribute("xmlns");
            attr.setValue("urn:opendaylight:flow:inventory");
            rootElement.setAttributeNode(attr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.setPriority(priority);
        this.setHardTimeout(hardTimeOut);
        this.setIdleTimeout(idleTimeOut);
        this.setFlowName(flowName);
        this.setFlowID(flowID);
        this.setTableID(tableID);
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
        Element ele = (Element)doc.getElementsByTagName(value).item(0);
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

    public void setFlowName(String value) {
        Element ele = getElement((Element) doc.getElementsByTagName("flow").item(0), "flow-name");
        setText(ele, value);
    }

    public void setPriority(int value) {
        Element ele = getElement((Element) doc.getElementsByTagName("flow").item(0), "priority");
        setText(ele, Integer.toString(value));
    }

    public void setHardTimeout(int value) {
        Element ele = getElement((Element) doc.getElementsByTagName("flow").item(0), "hard-timeout");
        setText(ele, Integer.toString(value));
    }

    public void setIdleTimeout(int value) {
        Element ele = getElement((Element) doc.getElementsByTagName("flow").item(0), "idle-timeout");
        setText(ele, Integer.toString(value));
    }

    public void setFlowID(String value) {
        Element ele = getElement((Element) doc.getElementsByTagName("flow").item(0), "id");
        setText(ele, value);
    }

    public void setTableID(String value) {
        Element ele = getElement((Element) doc.getElementsByTagName("flow").item(0), "table_id");
        setText(ele, value);
    }

    public void setIpv4Src(String value) {
        Element match = getElement((Element) doc.getElementsByTagName("flow").item(0), "match");
        Element ele = getElement(match, "ipv4-source");
        setText(ele, value);
    }

    public void setIpv4Dst(String value) {
        Element match = getElement((Element) doc.getElementsByTagName("flow").item(0), "match");
        Element ele = getElement(match, "ipv4-destination");
        setText(ele, value);
    }

    public Element getEthMatch() {
        Element match = getElement((Element) doc.getElementsByTagName("flow").item(0), "match");
        Element eth = getElement(match, "ethernet-match");
        return eth;
    }

    public void setEthType(int value) {
        Element eth = getEthMatch();
        Element ethType = getElement(eth, "ethernet-type");
        Element type = getElement(ethType, "type");
        setText(type, Integer.toString(value));
    }

    public Element getByOrder(Element container,String multiContainerName, String name, int order) {
        Element multiElements = getElement(container, multiContainerName);
        NodeList list = multiElements.getChildNodes();
        for (int i = 0; i < list.getLength(); ++i) {
            Element x = (Element) list.item(i);
            if (x.getElementsByTagName("order").getLength() == 0) continue;
            Element orderElement = (Element) x.getElementsByTagName("order").item(0);
            if (orderElement.getFirstChild().getNodeValue().equals(Integer.toString(order))) {
                return x;
            }
        }
        Element newInstruction = doc.createElement(name);
        multiElements.appendChild(newInstruction);
        Element newOrder = getElement(newInstruction, "order");
        setText(newOrder, Integer.toString(order));
        return newInstruction;
    }

    public void setMeter(int order, String meterID) {
        Element instruction = getByOrder((Element) doc.getElementsByTagName("flow").item(0),"instructions", "instruction", order);
        Element meter = getElement(instruction, "meter");
        Element meterIDElement = getElement(meter, "meter-id");
        setText(meterIDElement, meterID);
    }

    public void setAction(int insOrder, int actOrder, String[] path) {
        Element instruction = getByOrder((Element) doc.getElementsByTagName("flow").item(0),"instructions", "instruction", insOrder);
        Element action = getByOrder(instruction,"apply-actions", "action", actOrder);
        for (int i = 0; i < path.length - 1; ++i) {
            action = getElement(action, path[i]);
        }
        setText(action, path[path.length - 1]);
    }
}
