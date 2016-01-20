package infoManager;

import basicUtil.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by jiachen on 21/12/15.
 */

public class FlowManager {
    HashSet<FlowInfo> flowList;
    MeterManager meterManager;
    Connector connector;
    final int startFlowID = 1;

    HashMap<String, HashMap<String, Integer>> availableFlowID;

    public FlowManager() {
        flowList = new HashSet<FlowInfo>();
        availableFlowID = new HashMap<String, HashMap<String, Integer>>();
        connector = new Connector();
        //getUpdate();
    }

    public FlowManager(Connector connector) {
        flowList = new HashSet<FlowInfo>();
        availableFlowID = new HashMap<String, HashMap<String, Integer>>();
        this.connector = connector;
        //getUpdate();
    }

    public void setMeterManager(MeterManager meterManager) {
        this.meterManager = meterManager;
    }

    public int getAvailableFlowID(String switchID, String tableID) {
        HashMap<String, Integer> table = availableFlowID.get(switchID);
        if (table == null) {
            table = new HashMap<String, Integer>();
            availableFlowID.put(switchID, table);
        }
        int x = startFlowID;
        if (table.containsKey(tableID)) x = table.get(tableID);
        else table.put(tableID, x);
        return x + 1;
    }

    public void updateAvailableFlowID(String switchID, String tableID, String flowID) {
        int flow = Integer.parseInt(flowID);
        HashMap<String, Integer> table = availableFlowID.get(switchID);
        if (table == null) {
            table = new HashMap<String, Integer>();
            availableFlowID.put(switchID, table);
        }
        int now = startFlowID;
        if (table.containsKey(tableID)) now = table.get(tableID);
        else {
            table.put(tableID, now);
        }
        if (flow > now) {
            table.remove(tableID);
            table.put(tableID, flow);
        }
    }

    public FlowInfo getFlow(String switchID, String tableID, String flowID) {
        for (FlowInfo info : flowList) {
            if (info.switchID.equals(switchID) && info.tableID.equals(tableID) && info.flowID.equals(flowID)) return info;
        }
        return null;
    }

    public String addFlow(FlowInfo info) {
        if (flowList.contains(info)) flowList.remove(info);
        flowList.add(info);
        updateAvailableFlowID(info.switchID, info.tableID, info.flowID);

        FlowConstructor flow = new FlowConstructor(info.priority, 0, 0, info.name, info.tableID, info.flowID);
        flow.setEthType(info.ethType);
        if (info.srcIP.length() > 0) flow.setIpv4Src(info.srcIP);
        if (info.dstIP.length() > 0) flow.setIpv4Dst(info.dstIP);
        int insOrder = 0, actOrder = 0;
        if (info.linkedMeter != null) {
            flow.setMeter(0, info.linkedMeter.meterID);
            insOrder++;
        }

        String[] str = {"output-action", "output-node-connector", ""};
        for (String x : info.outPorts) {
            str[2] = x;
            flow.setAction(insOrder, actOrder++, str);
        }

        return connector.putFlow(info.switchID, info.tableID, info.flowID, flow.getXMLString());
    }

    public String deleteFlow(FlowInfo info) {
        for (FlowInfo tmp : flowList) {
            if (tmp.equals(info)) {
                flowList.remove(tmp);
                break;
            }
        }
        return connector.deleteFlow(info.switchID, info.tableID, info.flowID);
    }

    @Deprecated
    public String addFlowWithMeter(String switchID, String srcIP, String dstIP, String name, String outPort, int priority, String tableID, String flowID, String meterID) {
        FlowConstructor flow = new FlowConstructor(priority, 0, 0, name, tableID, flowID);
        flow.setEthType(2048);
        flow.setIpv4Src(srcIP);
        flow.setIpv4Dst(dstIP);
        flow.setMeter(0, meterID);
        String[] str = {"output-action", "output-node-connector", outPort};
        flow.setAction(1, 0, str);
        //System.out.println(flow.getXMLString());

        ArrayList<String> outPorts = new ArrayList<>();
        outPorts.add(outPort);
        FlowInfo flowInfo = new FlowInfo(switchID, tableID, flowID, srcIP, dstIP, 2048, outPorts);
        flowInfo.setLinkedMeter(meterManager.getMeter(switchID, meterID));
        if (flowList.contains(flowInfo)) flowList.remove(flowInfo);
        flowList.add(flowInfo);
        updateAvailableFlowID(switchID, tableID, flowID);

        return connector.putFlow(switchID, tableID, flowID, flow.getXMLString());
    }

    @Deprecated
    public String addFlow(String switchID, String srcIP, String dstIP, String name, String outPort, int priority, String tableID, String flowID) {
        FlowConstructor flow = new FlowConstructor(priority, 0, 0, name, flowID, tableID);
        flow.setEthType(2048);
        flow.setIpv4Src(srcIP);
        flow.setIpv4Dst(dstIP);
        String[] str = {"output-action", "output-node-connector", outPort};
        flow.setAction(0, 0, str);
        //System.out.println(flow.getXMLString());

        ArrayList<String> outPorts = new ArrayList<>();
        outPorts.add(outPort);
        FlowInfo flowInfo = new FlowInfo(switchID, tableID, flowID, srcIP, dstIP, 2048, outPorts);
        if (flowList.contains(flowInfo)) flowList.remove(flowInfo);
        flowList.add(flowInfo);
        updateAvailableFlowID(switchID, tableID, flowID);

        return connector.putFlow(switchID, tableID, flowID, flow.getXMLString());
    }

    @Deprecated
    public String deleteFlow(String switchID, String tableID, String flowID) {
        return connector.deleteFlow(switchID, tableID, flowID);
    }

    public String installARP(ArrayList<Switch> switches) {
        String ans = "Install ARP result:\n";
        for (Switch sw : switches) {
            ans = ans + "Switch: " +sw.name + installARP(sw) + "\n";
        }
        return ans;
    }

    public String installARP(Switch switchID) {
        String tableID = "0";
        Integer flowID = getAvailableFlowID(switchID.name, tableID);
        FlowConstructor flow = new FlowConstructor(10, 0, 0, "arpFlood", tableID, flowID.toString());
        flow.setEthType(2054);
        String[] str = {"output-action", "output-node-connector", ""};
        int actOrder = 0;
        for (SwitchPort port : switchID.getPorts()) {
            str[2] = Integer.toString(port.port);
            flow.setAction(0, actOrder++, str);
        }

        //System.out.println(flow.getXMLString());
        updateAvailableFlowID(switchID.name, tableID, flowID.toString());
        return connector.putFlow(switchID.name, tableID, flowID.toString(), flow.getXMLString());
    }

    public void getUpdate() {
        String str = connector.getAllInConfig();
        flowList.clear();
        try {
            JSONObject doc = new JSONObject(str);
            doc = doc.getJSONObject("nodes");
            JSONArray nodes = doc.getJSONArray("node");
            for (int i = 0; i < nodes.length(); i++) {
                JSONObject node = nodes.getJSONObject(i);
                String switchID = node.getString("id");
                JSONArray switchTables = node.getJSONArray("flow-node-inventory:table");
                for (int j = 0; j < switchTables.length(); j++) {
                    JSONObject table = switchTables.getJSONObject(j);
                    String tableID = table.getInt("id") + "";
                    JSONArray flows = table.getJSONArray("flow");
                    for (int k = 0; k < flows.length(); k++) {
                        JSONObject flow = flows.getJSONObject(k);
                        FlowInfo info = null;
                        try {
                            info = getFlowInfo(flow, switchID, tableID);
                        } catch (Exception e) {
                            //e.printStackTrace();
                            System.err.println("Cannot analyze flow:" + flow + " ------ FlowManager");
                        }
                        if (info != null) {
                            flowList.add(info);
                            //System.out.println(info.toString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Cannot analyze JSON string: " + str);
        }

    }

    public void deleteAllExceptDefault() {
        getUpdate();
        for (FlowInfo flow : flowList) {
            if (flow.tableID.equals("0") && flow.flowID.equals("1")) continue;
            String str = connector.deleteFlow(flow.switchID, flow.tableID, flow.flowID);
        }
        availableFlowID.clear();
    }

    public void deleteAll() {
        getUpdate();
        for (FlowInfo flow : flowList) {
            String str = connector.deleteFlow(flow.switchID, flow.tableID, flow.flowID);
        }
        availableFlowID.clear();
    }

    public ArrayList<FlowInfo> getFlowList() {
        getUpdate();
        ArrayList<FlowInfo> ans = new ArrayList<>();
        for (FlowInfo flow : flowList) {
            ans.add(flow);
        }
        return ans;
    }

    private FlowInfo getFlowInfo(JSONObject flow, String switchID, String tableID) throws Exception {
        String srcIP = "", dstIP = "";
        String flowID = flow.getString("id");
        JSONObject matchField = flow.getJSONObject("match");
        try {
            srcIP = matchField.getString("ipv4-source");
            dstIP = matchField.getString("ipv4-destination");
        } catch (Exception e) {
            System.err.println("This flow is not a ipv4 flow. ------- FlowManager");
        }

        int ethType = -1;
        try {
            ethType = matchField.getJSONObject("ethernet-match").getJSONObject("ethernet-type").getInt("type");
        } catch (Exception e) {
            System.err.println("This flow does not have ethType ------ FlowManager");
        }

        ArrayList<String> outPorts = new ArrayList<String>();
        MeterInfo meter = null;
        try {
            JSONArray instructions = flow.getJSONObject("instructions").getJSONArray("instruction");

            for (int i = 0; i < instructions.length(); i++) {
                JSONObject tmp = instructions.getJSONObject(i);
                try {
                    JSONObject actions = tmp.getJSONObject("apply-actions");
                    JSONArray actionList = actions.getJSONArray("action");
                    for (int j = 0; j < actionList.length(); j++) {
                        JSONObject output = actionList.getJSONObject(j);
                        String port = "";
                        port = output.getJSONObject("output-action").getString("output-node-connector");
                        outPorts.add(port);
                    }
                } catch (Exception e) {
                    JSONObject meterField = null;
                    try {
                        meterField = tmp.getJSONObject("meter");
                    } catch (Exception es) {
                        System.err.println("This action does not have output port and meter ------ FlowManager");
                    }
                    if (meterField != null) {
                        Integer meterID = meterField.getInt("meter-id");
                        //System.out.println(meterID.toString());
                        meter = meterManager.getMeter(switchID, meterID.toString());
                    }
                }
            }
        } catch (Exception e) {

        }


        FlowInfo info = new FlowInfo(switchID, tableID, flowID, srcIP, dstIP, ethType, outPorts);
        if (meter != null) info.setLinkedMeter(meter);
        return info;
    }
}
