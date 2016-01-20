package optimizationAlgorithm;

import basicUtil.*;
import controlPanel.MainManagerInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by jiachen on 24/12/15.
 */
public class SimpleShare implements OptimizationInterface {

    HashMap<String, ArrayList<FlowInfo>> flowsOnPorts;
    MainManagerInterface parent;
    HashMap<FlowFeature, Route> routeStore;

    final int linkBandWidth = 200000;

    public SimpleShare(MainManagerInterface parent) {
        this.parent = parent;
        flowsOnPorts = new HashMap<String, ArrayList<FlowInfo>>();
        routeStore = new HashMap<>();
    }

    @Override
    public ArrayList<Route> initialOptimization() {
        return null;
    }

    @Override
    public NeedModifyList addNewFlow(FlowFeature feature) {
        if (routeStore.containsKey(feature)) return new NeedModifyList();

        Route route = findRoute(feature.srcIP, feature.dstIP);

        routeStore.put(feature, route);

        NeedModifyList result = new NeedModifyList();

        for (int i = 0; i < route.size(); i++) {
            RouteNode node = route.get(i);
            SwitchPort outPort = node.outPort;
            ArrayList<FlowInfo> relatedFlow = flowsOnPorts.get(outPort.container.name + ":" + outPort.port);
            if (relatedFlow == null) {
                relatedFlow = new ArrayList<FlowInfo>();
                flowsOnPorts.put(outPort.container.name + ":" + outPort.port, relatedFlow);

            }
            System.out.println(relatedFlow.size());
            int realBand = linkBandWidth / (relatedFlow.size() + 1);

            MeterInfo meter = addMeter(node.switchNode.name, realBand);
            FlowInfo flow = addFlow(node.switchNode.name, feature.srcIP + "/32", feature.dstIP + "/32", node.outPort, 2048);
            flow.setLinkedMeter(meter);
            result.addModifyFlow(flow);
            result.addMeter(meter);

            for (FlowInfo info : relatedFlow) {
                meter = null;
                if (info.linkedMeter == null) {
                    meter = addMeter(node.switchNode.name, realBand);
                    info.setLinkedMeter(meter);
                    result.addModifyFlow(info);
                    result.addMeter(meter);
                }
                else {
                    meter = info.linkedMeter;
                    meter.bandWidth = realBand;
                    result.addMeter(meter);
                }
            }

            relatedFlow.add(flow);
        }

        return result;
    }

    @Override
    public NeedModifyList deleteFlow(FlowFeature feature) {
        if (!routeStore.containsKey(feature)) {
            System.err.println("Cannot find this flow.");
            return new NeedModifyList();
        }

        Route route = routeStore.get(feature);
        System.out.println(route.size());
        NeedModifyList result = new NeedModifyList();

        for (int i = 0; i < route.size(); i++) {
            RouteNode node = route.get(i);
            SwitchPort outPort = node.outPort;
            ArrayList<FlowInfo> relatedFlow = flowsOnPorts.get(outPort.container.name + ":" + outPort.port);
            if (relatedFlow == null) {
                System.err.println("Error: there is no related flow on " + outPort.container.name + ":" + outPort.port);
                continue;
            }
            //System.out.println(relatedFlow.size());
            int realBand = 0;
            if (relatedFlow.size() > 1)
                realBand = linkBandWidth / (relatedFlow.size() - 1);

            for (FlowInfo info : relatedFlow) {
                System.out.println("Related Flow:" + info.srcIP + " - " + info.dstIP);
                if (info.srcIP.equals(feature.srcIP + "/32") && info.dstIP.equals(feature.dstIP + "/32")) {
                    result.addDeleteFlow(info);
                    continue;
                }

                MeterInfo meter = null;
                if (info.linkedMeter == null) {
                    meter = addMeter(node.switchNode.name, realBand);
                    info.setLinkedMeter(meter);
                    result.addModifyFlow(info);
                    result.addMeter(meter);
                }
                else {
                    meter = info.linkedMeter;
                    meter.bandWidth = realBand;
                    result.addMeter(meter);
                }
            }
        }

        return result;
    }

    @Override
    public void clearAll() {
        flowsOnPorts.clear();
        routeStore.clear();
    }

    private FlowInfo addFlow(String switchID, String src, String dst, SwitchPort outPort, int ethType) {
        String tableID = "0";
        Integer flowID = parent.getAvailableFlowID(switchID, tableID);
        ArrayList<String> outs = new ArrayList<>();
        outs.add(outPort.port + "");
        FlowInfo info = new FlowInfo(switchID, tableID, flowID.toString(), src, dst, ethType, outs);
        return info;
    }

    private MeterInfo addMeter(String switchID, int band) {
        int meterID = parent.getAvailableMeterID(switchID);
        MeterInfo meter = new MeterInfo(switchID + meterID, switchID, meterID + "", band);
        return meter;
    }

    private Route findRoute(String src, String dst) {
        Route ans = new Route();

        HashSet<String> haveBeenDealed = new HashSet<String>();
        ArrayList<Switch> queue = new ArrayList<Switch>();
        ArrayList<Link> links = new ArrayList<Link>();
        ArrayList<Integer> preIndex = new ArrayList<Integer>();

        Host srcHost = parent.getHost(src);
        Switch nowSwitch = srcHost.linkedSwitchPort.container;
        int head = -1;
        queue.add(nowSwitch);
        haveBeenDealed.add(nowSwitch.name);
        links.add(null);
        preIndex.add(-1);


        SwitchPort lastPort = null;
        int lastIndex = -1;

        while (head + 1 < queue.size()) {
            head++;
            nowSwitch = queue.get(head);
            for (SwitchPort outPort : nowSwitch.getPortsLinkedSwitch()) {
                Pair<Switch, SwitchPort> linkedSwitch = nowSwitch.getLinkedSwitch(outPort);
                Switch nextSwitch = linkedSwitch.first();
                SwitchPort inPort = linkedSwitch.second();

                if (!haveBeenDealed.contains(nextSwitch.name)) {
                    Link newLink = new Link(outPort, inPort);
                    queue.add(nextSwitch);
                    haveBeenDealed.add(nextSwitch.name);
                    links.add(newLink);
                    preIndex.add(head);
                }
            }

            for (SwitchPort outPort : nowSwitch.getPortLinkedHost()) {
                Host host = nowSwitch.getLinkedHost(outPort);
                if (host.ip.equals(dst)) {
                    lastPort = outPort;
                    lastIndex = head;
                    break;
                }
            }

            if (lastPort != null) break;
        }

        while (lastIndex >= 0) {
            Link nowLink = links.get(lastIndex);
            nowSwitch = queue.get(lastIndex);


            RouteNode node = null;
            if (nowLink == null)
                node = new RouteNode(nowSwitch, srcHost.linkedSwitchPort, lastPort);
            else node = new RouteNode(nowSwitch, nowLink.inPort, lastPort);
            ans.addNode(node);

            if (nowLink != null) lastPort = nowLink.outPort;
            else break;
            lastIndex = preIndex.get(lastIndex);
        }
        
        return ans;
    }
}
