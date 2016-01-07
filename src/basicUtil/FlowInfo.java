package basicUtil;

import java.util.ArrayList;

/**
 * Created by jiachen on 21/12/15.
 */
public class FlowInfo {
    public String switchID, name;
    public String flowID;
    public String tableID;
    public String srcIP, dstIP;
    public ArrayList<String> outPorts;
    public int priority, ethType;
    public MeterInfo linkedMeter;

    public FlowInfo(String sid, String tid, String fid, String src, String dst, int ethType, ArrayList<String> out) {
        switchID = sid;
        flowID = fid;
        tableID = tid;
        linkedMeter = null;

        priority = 100;
        srcIP = src;
        dstIP = dst;
        outPorts = out;
        name = src + "To" + dst;
        this.ethType = ethType;
    }

    public void setLinkedMeter(MeterInfo meter) {
        linkedMeter = meter;
    }

    public void unsetLinkedMeter(MeterInfo meter) {
        if (meter.equals(linkedMeter))
            linkedMeter = null;
    }

    @Override
    public String toString() {
        return "{ switchID: " + switchID + ", tableID: " + tableID + ", flowID: " + flowID + "}";
    }

    @Override
    public boolean equals(Object flow) {
        if (flow instanceof FlowInfo) {
            if (!((FlowInfo) flow).switchID.equals(this.switchID)) return false;
            else if (!((FlowInfo) flow).tableID.equals(this.tableID)) return false;
            else if (!((FlowInfo) flow).flowID.equals(this.flowID)) return false;
            return true;
        }
        else return false;
    }
}
