package basicUtil;

import java.util.ArrayList;

/**
 * Created by jiachen on 29/12/15.
 */
public class NeedModifyList {
    public ArrayList<FlowInfo> flowList;
    public ArrayList<MeterInfo> meterList;

    public NeedModifyList() {
        flowList = new ArrayList<>();
        meterList = new ArrayList<>();
    }

    public NeedModifyList(ArrayList<FlowInfo> flows, ArrayList<MeterInfo> meters) {
        flowList = flows;
        meterList = meters;
    }

    public void addFlow(FlowInfo info) {
        flowList.add(info);
    }

    public void addMeter(MeterInfo info) {
        meterList.add(info);
    }

    public int flowSize() {
        return flowList.size();
    }

    public int meterSize() {
        return meterList.size();
    }

    public FlowInfo getFlow(int index) {
        return flowList.get(index);
    }

    public MeterInfo getMeter(int index) {
        return meterList.get(index);
    }

    public void print() {
        int index = 0;
        for (FlowInfo info : flowList) {
            System.out.println("Flow " + (++index) + ":  {\n" +
                                    "\tSwitch: " + info.switchID + "\n" +
                                    "\tSourceIP: " + info.srcIP + "\n" +
                                    "\tDestinationIP: " + info.dstIP + "\n" +
                                    "\tTable: " + info.tableID + ",  flowID: " + info.flowID + "\n" +
                                    "\tMeter: " + info.linkedMeter.meterID + "\n}");
        }

        index = 0;
        for (MeterInfo info : meterList) {
            System.out.println("Meter " + (++index) + ": {\n" +
                                    "\tSwitch: " + info.switchID + "\n" +
                                    "\tMeterID: " + info.meterID + "\n" +
                                    "\tBandWidth:" + info.bandWidth + "\n}");
        }
    }
}
