package basicUtil;

import java.util.ArrayList;

/**
 * Created by jiachen on 29/12/15.
 */
public class NeedModifyList {
    private ArrayList<FlowInfo> modifyFlowList, deleteFlowList;
    private ArrayList<MeterInfo> meterList;

    public NeedModifyList() {
        modifyFlowList = new ArrayList<>();
        deleteFlowList = new ArrayList<>();
        meterList = new ArrayList<>();
    }

    public NeedModifyList(ArrayList<FlowInfo> flows, ArrayList<MeterInfo> meters) {
        modifyFlowList = flows;
        meterList = meters;
    }

    public void addModifyFlow(FlowInfo info) {
        modifyFlowList.add(info);
    }

    public void addDeleteFlow(FlowInfo info) { deleteFlowList.add(info); }

    public void addMeter(MeterInfo info) {
        meterList.add(info);
    }

    public int modifyFlowListSize() {
        return modifyFlowList.size();
    }

    public int deleteFlowListSize() {
        return deleteFlowList.size();
    }

    public int meterSize() {
        return meterList.size();
    }

    public FlowInfo getModifyFlow(int index) {
        return modifyFlowList.get(index);
    }

    public FlowInfo getDeleteFlow(int index) {
        return deleteFlowList.get(index);
    }

    public MeterInfo getMeter(int index) {
        return meterList.get(index);
    }

    public void print() {
        int index = 0;
        for (MeterInfo info : meterList) {
            System.out.println("Need Modify MeterEntry " + (++index) + ": {\n" +
                    "\tSwitch: " + info.switchID + "\n" +
                    "\tMeterID: " + info.meterID + "\n" +
                    "\tBandWidth:" + info.bandWidth + "\n}");
        }

        index = 0;
        for (FlowInfo info : modifyFlowList) {
            System.out.println("Need Modify FlowEntry " + (++index) + ":  {\n" +
                    "\tSwitch: " + info.switchID + "\n" +
                    "\tSourceIP: " + info.srcIP + "\n" +
                    "\tDestinationIP: " + info.dstIP + "\n" +
                    "\tTable: " + info.tableID + ",  flowID: " + info.flowID + "\n" +
                    "\tMeter: " + info.linkedMeter.meterID + "\n}");
        }

        index = 0;
        for (FlowInfo info: deleteFlowList) {
            System.out.println("Need Delete FlowEntry " + (++index) + ":  {\n" +
                                    "\tSwitch: " + info.switchID + "\n" +
                                    "\tSourceIP: " + info.srcIP + "\n" +
                                    "\tDestinationIP: " + info.dstIP + "\n" +
                                    "\tTable: " + info.tableID + ",  flowID: " + info.flowID + "\n" +
                                    "\tMeter: " + info.linkedMeter.meterID + "\n}");
        }
    }
}
