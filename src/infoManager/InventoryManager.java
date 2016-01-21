package infoManager;

/**
 * Created by jiachen on 21/12/15.
 */

import basicUtil.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class InventoryManager {
    HashMap<String, Switch> switches;
    HashMap<String, Host> hosts;
    Connector connector;

    public InventoryManager() {
        switches = new HashMap<String, Switch>();
        hosts = new HashMap<String, Host>();
        connector = new Connector();
        getUpdate();
    }

    public InventoryManager(Connector connector) {
        switches = new HashMap<String, Switch>();
        hosts = new HashMap<String, Host>();
        this.connector = connector;
        getUpdate();
    }

    public void getUpdate() {
        String str = connector.getAllInOperational();
        switches.clear();
        hosts.clear();
        try {
            JSONObject doc = new JSONObject(str);
            doc = doc.getJSONObject("nodes");
            JSONArray nodes = doc.getJSONArray("node");
            for (int i = 0; i < nodes.length(); i++) {
                JSONObject node = nodes.getJSONObject(i);
                String id = node.getString("id");
                Switch newSwitch = new Switch(id);
                switches.put(id, newSwitch);
                JSONArray nodeConnectors = node.getJSONArray("node-connector");
                for (int j = 0; j < nodeConnectors.length(); j++) {
                    JSONObject nodeConnector = nodeConnectors.getJSONObject(j);
                    try {
                        int switchPort = Integer.parseInt(nodeConnector.getString("id").split(":")[2]);
                        //System.out.println("Switch port:" + switchPort);
                        SwitchPort port = newSwitch.addPort(switchPort);

                        JSONObject hostTracker = nodeConnector.getJSONArray("address-tracker:addresses").getJSONObject(0);
                        String hostID = Integer.toString(hostTracker.getInt("id"));
                        String hostIP = hostTracker.getString("ip");

                        Host newHost = new Host(hostID, hostIP, port);
                        newSwitch.addLinkedHost(port, newHost);
                        hosts.put(hostIP, newHost);

                        String hostMAC = hostTracker.getString("mac");
                        newHost.setMAC(hostMAC);
                    }
                    catch (Exception e) {
                        //e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        getTopoUpdate();
    }

    public Switch getSwitch(String name) {
        return switches.get(name);
    }

    public Host getHost(String ip) { return hosts.get(ip); }

    public void getTopoUpdate() {
        String str = connector.getFromURL(BasicParam.topoURL);
        try {
            JSONObject doc = new JSONObject(str);
            doc = doc.getJSONObject("network-topology");
            JSONArray networks = doc.getJSONArray("topology");
            for (int i = 0; i < networks.length(); i++) {
                JSONObject node = networks.getJSONObject(i);
                JSONArray links = node.getJSONArray("link");
                for (int j = 0; j < links.length(); j++) {
                    JSONObject link = links.getJSONObject(j);
                    String srcSwitchName = link.getJSONObject("source").getString("source-node");
                    Switch srcSwitch = this.getSwitch(srcSwitchName);
                    int srcPort = Integer.parseInt(link.getJSONObject("source").getString("source-tp").split(":")[2]);
                    SwitchPort srcSwitchPort = srcSwitch.addPort(srcPort);

                    String dstSwitchName = link.getJSONObject("destination").getString("dest-node");
                    Switch dstSwitch = this.getSwitch(dstSwitchName);
                    int dstPort = Integer.parseInt(link.getJSONObject("destination").getString("dest-tp").split(":")[2]);
                    SwitchPort dstSwitchPort = dstSwitch.addPort(dstPort);

                    srcSwitch.addLinkedSwitch(srcSwitchPort, dstSwitch, dstSwitchPort);
                    dstSwitch.addLinkedSwitch(dstSwitchPort, srcSwitch, srcSwitchPort);
                }
            }
        } catch (Exception e) {

        }
    }

    public ArrayList<Switch> getSwitches() {
        getUpdate();
        ArrayList<Switch> ans = new ArrayList<Switch>();
        for (String name : switches.keySet()) {
            Switch tmp = switches.get(name);
            ans.add(tmp);
        }
        return ans;
    }

    public ArrayList<Host> getHosts() {
        getUpdate();
        ArrayList<Host> ans = new ArrayList<Host>();
        for (String name : hosts.keySet()) {
            Host tmp = hosts.get(name);
            ans.add(tmp);
        }
        return ans;
    }
}
