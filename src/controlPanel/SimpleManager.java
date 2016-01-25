package controlPanel; /**
 * Created by jiachen on 21/12/15.
 */

import basicUtil.*;
import infoManager.FlowManager;
import infoManager.InventoryManager;
import infoManager.MeterManager;
import infoManager.StatisticManager;
import optimizationAlgorithm.SimpleShare;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SimpleManager implements MainManagerInterface {
    Connector connector;
    InventoryManager inventoryManager;
    FlowManager flowManager;
    MeterManager meterManager;
    StatisticManager statisticManager;
    SimpleShare optManager;

    public SimpleManager() {
        connector = new Connector();
        inventoryManager = new InventoryManager(connector);
        meterManager = new MeterManager(connector);
        flowManager = new FlowManager(connector);
        flowManager.setMeterManager(meterManager);
        statisticManager = new StatisticManager(connector, flowManager, meterManager);
        clearAll();

        optManager = new SimpleShare(this);

        start();
    }

    private void addFlow(BufferedReader br) throws Exception {
        String input = "";
        System.out.println("Please input the flow which you want to add, input like 192.168.0.1 - 127.0.0.1 - 5.0");
        input=br.readLine();
        input = input.replace(" ", "");
        String src = input.split("-")[0];
        String dst = input.split("-")[1];
        Double w = Double.parseDouble(input.split("-")[2]);
        FlowFeature feature = new FlowFeature(src, dst, w);
        inventoryManager.getUpdate();
        NeedModifyList list = optManager.addNewFlow(feature);
        list.print();

        for (int i = 0; i < list.meterSize(); i++) {
            MeterInfo info = list.getMeter(i);
            meterManager.addMeter(info);
        }

        for (int i = 0; i < list.modifyFlowListSize(); i++) {
            FlowInfo info = list.getModifyFlow(i);
            flowManager.addFlow(info);
        }
    }

    private void deleteFlow(BufferedReader br) throws Exception {
        System.out.println("Please Input the flow you want to delete, input like 192.168.0.1 - 127.0.0.1");
        String input=br.readLine();
        input = input.replace(" ", "");
        String src = input.split("-")[0];
        String dst = input.split("-")[1];
        FlowFeature feature = new FlowFeature(src, dst);

        NeedModifyList list = optManager.deleteFlow(feature);
        list.print();

        for (int i = 0; i < list.meterSize(); i++) {
            MeterInfo info = list.getMeter(i);
            meterManager.addMeter(info);
        }

        for (int i = 0; i < list.modifyFlowListSize(); i++) {
            FlowInfo info = list.getModifyFlow(i);
            flowManager.addFlow(info);
        }

        for (int i = 0; i < list.deleteFlowListSize(); i++) {
            FlowInfo info = list.getDeleteFlow(i);
            flowManager.deleteFlow(info);
            if (info.linkedMeter != null)
                meterManager.deleteMeter(info.linkedMeter);
        }
    }

    private void installARP() {
        flowManager.installARP(inventoryManager.getSwitches());
    }

    private void printFlows(BufferedReader br) throws Exception {
        ArrayList<FlowInfo> flows = flowManager.getFlowList();
        for (int i = 0; i < flows.size(); i++) {
            System.out.println("No. " + (i + 1) + " Flow Entry: {");
            FlowInfo info = flows.get(i);
            System.out.println("\tSwitchID: " + info.switchID + ", TableID: " + info.tableID + ", FlowID: " + info.flowID);
            if (info.srcIP != null && info.srcIP.length() > 0)
                System.out.println("\tsrcIP:" + info.srcIP);
            if (info.dstIP != null && info.dstIP.length() > 0)
                System.out.println("\tdstIP:" + info.dstIP);
            if (info.ethType > 0)
                System.out.println("\tethType: " + info.ethType);
            System.out.println("}");
        }
    }

    public void start() {
        try{
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(System.in));

            String input;

            while(true) {
                System.out.println("Please select operations:");
                System.out.println("\t1. Print Switches");
                System.out.println("\t2. Print Hosts");
                System.out.println("\t3. Add new flow");
                System.out.println("\t4. Install ARP flood");
                System.out.println("\t5. Print all flows");
                System.out.println("\t6. Clear all flows");
                System.out.println("\t7. Delete flow");
                System.out.println("\t8. Statistics");
                input = br.readLine();
                if (input == null) break;
                input = input.toLowerCase();
                if (input.contains("1") || input.contains("print switches"))
                    printSwitch(inventoryManager.getSwitches());
                else if (input.contains("2") || input.contains("print hosts"))
                    printHost(inventoryManager.getHosts());
                else if (input.contains("3") || input.contains("add new flow"))
                    addFlow(br);
                else if (input.contains("4") || input.contains("install arp flood"))
                    installARP();
                else if (input.contains("5") || input.contains("delete one flow"))
                    printFlows(br);
                else if (input.contains("6") || input.contains("clear all flows")) {
                    flowManager.deleteAll();
                    meterManager.deleteAll();
                    clearAll();
                }
                else if (input.contains("7") || input.contains("delete one flow")) {
                    deleteFlow(br);
                }
                else if (input.contains("8") || input.contains("statistics")) {
                    statisticManager.printStatistics();
                }
                else {
                    System.err.println("Operation cannot supported.");
                }
            }

        }catch (Exception io){
            io.printStackTrace();
        }
    }

    private void clearAll() {
        if (flowManager != null) flowManager.deleteAll();
        if (meterManager != null) meterManager.deleteAll();

        String deletexml = "", addxml = "";
        try {
            File file = new File("sources/deleteflow.xml");
            File addFile = new File("sources/addflow.xml");
            FileReader fileReader = new FileReader(file);
            FileReader addFileReader = new FileReader(addFile);

            BufferedReader bufferedReader = new BufferedReader(fileReader);
            BufferedReader addBufferedReader = new BufferedReader(addFileReader);

            String tmp;
            while ((tmp = bufferedReader.readLine()) != null) {
                deletexml = deletexml + tmp;
            }
            bufferedReader.close();

            while ((tmp = addBufferedReader.readLine()) != null) {
                addxml = addxml + tmp;
            }
            addBufferedReader.close();

            ArrayList<Switch> switches = inventoryManager.getSwitches();
            for (Switch switchid : switches) {
                String realXML = deletexml.replace("switchID", "\"" + switchid.name + "\"");
                connector.postXMLToURL(BasicParam.salRemoveFlowURL, realXML);
                connector.putFlow(switchid.name, "0", "1", addxml);
            }

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void printHost(ArrayList<Host> hosts) {
        System.out.println("Hosts List:");
        for (Host host : hosts) {
            System.out.println(host.name + ": (ip: " + host.ip + "; mac: " + host.mac + "; linkedSwitchPort: " + host.linkedSwitchPort.container.name + "-" + host.linkedSwitchPort.port);
        }
    }

    private void printSwitch(ArrayList<Switch> switches) {
        System.out.println("Switches List:");
        for (Switch sw : switches) {
            System.out.println(sw.name);
        }
    }

    @Override
    public int getAvailableFlowID(String switchID, String tableID) {
        return flowManager.getAvailableFlowID(switchID, tableID);
    }

    @Override
    public int getAvailableMeterID(String switchID) {
        return meterManager.getAvailableMeterID(switchID);
    }

    @Override
    public Host getHost(String ip) {
        return inventoryManager.getHost(ip);
    }

    @Override
    public Switch getSwitch(String switchID) {
        return inventoryManager.getSwitch(switchID);
    }

    @Override
    public MeterInfo getMeter(String switchID, String meterID) {
        return meterManager.getMeter(switchID, meterID);
    }

    @Override
    public FlowInfo getFlow(String switchID, String tableID, String flowID) {
        return flowManager.getFlow(switchID, tableID, flowID);
    }
}
