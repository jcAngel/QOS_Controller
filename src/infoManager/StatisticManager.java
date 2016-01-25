package infoManager;

import basicUtil.*;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jiachen on 22/01/16.
 */
public class StatisticManager {
    //Pair<Double, Double>, first Double represent byte count, sencond Double represent time count.
    private HashMap<FlowInfo, Pair<Double, Double>> flowCount;
    private HashMap<MeterInfo, Pair<Double, Double>> meterCount;
    Connector connector;
    FlowManager flowManager;
    MeterManager meterManager;

    public StatisticManager() {
        flowCount = new HashMap<>();
        meterCount = new HashMap<>();
        connector = new Connector();
        flowManager = new FlowManager(connector);
        meterManager = new MeterManager(connector);
        getUpdate();
    }

    public StatisticManager(Connector connector, FlowManager flowManager, MeterManager meterManager) {
        flowCount = new HashMap<>();
        meterCount = new HashMap<>();
        this.connector = connector;
        this.flowManager = flowManager;
        this.meterManager = meterManager;
        getUpdate();
    }

    public void getUpdate() {
        flowCount.clear();

        ArrayList<FlowInfo> flowList = flowManager.getFlowList();
        for (FlowInfo info : flowList) {
            String url = BasicParam.flowStatisticURL;
            url = url.replace("{switchID}", info.switchID);
            url = url.replace("{tableID}", info.tableID);
            url = url.replace("{flowID}", info.flowID);
            String str = connector.getFromURL(url);
            try {
                JSONObject doc = new JSONObject(str);
                doc = doc.getJSONObject("opendaylight-flow-statistics:flow-statistics");
                Double byteCount = (double) doc.getLong("byte-count");
                doc = doc.getJSONObject("duration");
                int sec = doc.getInt("second");
                int nanosec = doc.getInt("nanosecond");
                Double duration = sec + ((double) nanosec) / 1000000000;
                flowCount.put(info, new Pair<Double, Double>(byteCount, duration));
                //System.out.println(byteCount + " time: " + duration);
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }

        ArrayList<MeterInfo> meterList = meterManager.getMeterList();
        for (MeterInfo info : meterList) {
            String url = BasicParam.meterStatisticURL;
            url = url.replace("{switchID}", info.switchID);
            url = url.replace("{meterID}", info.meterID);
            String str = connector.getFromURL(url);
            try {
                JSONObject doc = new JSONObject(str);
                doc = doc.getJSONObject("opendaylight-meter-statistics:meter-statistics");
                Double byteCount = (double) doc.getLong("byte-in-count");
                doc = doc.getJSONObject("duration");
                int sec = doc.getInt("second");
                int nanosec = doc.getInt("nanosecond");
                Double duration = sec + ((double) nanosec) / 1000000000;
                meterCount.put(info, new Pair<Double, Double>(byteCount, duration));
                //System.out.println(byteCount + " time: " + duration);
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
    }

    public HashMap<FlowInfo, Pair<Double, Double>> getFlowStatistic() {
        getUpdate();
        return flowCount;
    }

    public HashMap<MeterInfo, Pair<Double, Double>> getMeterStatistic() {
        getUpdate();
        return meterCount;
    }

    public void printStatistics() {
        getUpdate();
        int index = 0;
        for (FlowInfo info : flowCount.keySet()) {
            Pair<Double, Double> stats = flowCount.get(info);
            System.out.println("Flow " + (++index) + "'s statistics:{");
            System.out.println("\tSwitchID: " + info.switchID);
            System.out.println("\ttableID: " + info.tableID + ", flowID: " + info.flowID);
            if (info.srcIP != null && info.srcIP.length() != 0)
                System.out.println("\tsrcIP: " + info.srcIP);
            if (info.dstIP != null && info.dstIP.length() != 0)
                System.out.println("\tdstIP: " + info.dstIP);
            System.out.println("\tDuration: " + String.format("%.3f", stats.second()) + "s; byte Count: " + stats.first().intValue() + " bytes");
            Double tmp = ((stats.first() * 8) / (stats.second() * 1000));
            System.out.println("\tAvg. Bandwidth: " + String.format("%.3f", tmp) + " Kbps");
            System.out.println("}\n");
        }
    }
}
