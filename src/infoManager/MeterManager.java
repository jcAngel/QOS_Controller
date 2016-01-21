package infoManager;

import basicUtil.Connector;
import basicUtil.MeterConstructor;
import basicUtil.MeterInfo;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by jiachen on 22/12/15.
 */
public class MeterManager {
    HashSet<MeterInfo> meterList;
    Connector connector;
    final int startMeterID = 0;

    HashMap<String, Integer> availableMeterID;

    public MeterManager() {
        meterList = new HashSet<>();
        availableMeterID = new HashMap<>();
        connector = new Connector();
    }

    public MeterManager(Connector connector) {
        meterList = new HashSet<>();
        availableMeterID = new HashMap<>();
        this.connector = connector;
    }

    public int getAvailableMeterID(String switchID) {
        int x = startMeterID;
        if (availableMeterID.containsKey(switchID)) x = availableMeterID.get(switchID);
        else availableMeterID.put(switchID, x);
        return x + 1;
    }

    public void updateAvailableMeterID(String switchID, String meterID) {
        int meter = Integer.parseInt(meterID);
        int now = startMeterID;
        if (availableMeterID.containsKey(switchID)) now = availableMeterID.get(switchID);
        else availableMeterID.put(switchID, now);
        if (meter > now) {
            availableMeterID.remove(switchID);
            availableMeterID.put(switchID, meter);
        }
    }

    public String addMeter(MeterInfo info) {
        if (meterList.contains(info)) meterList.remove(info);
        meterList.add(info);
        updateAvailableMeterID(info.switchID, info.meterID);

        MeterConstructor meter = new MeterConstructor();
        meter.setMeterID(info.meterID);
        meter.setMeterName(info.name);
        meter.setFlag("meter-kbps");
        meter.setMeterBandType(0, "ofpmbt-drop", info.bandWidth, 10000);
        //System.out.println(meter.getXMLString());
        return connector.putMeter(info.switchID, info.meterID, meter.getXMLString());
    }

    public String deleteMeter(MeterInfo info) {
        meterList.remove(info);
        return connector.deleteMeter(info.switchID, info.meterID);
    }

    public void getUpdate() {
        String str = connector.getAllInConfig();
        meterList.clear();
        try {
            JSONObject doc = new JSONObject(str);
            doc = doc.getJSONObject("nodes");
            JSONArray nodes = doc.getJSONArray("node");
            for (int i = 0; i < nodes.length(); i++) {
                JSONObject node = nodes.getJSONObject(i);
                String switchID = node.getString("id");
                JSONArray meterTables = node.getJSONArray("flow-node-inventory:meter");
                for (int j = 0; j < meterTables.length(); j++) {
                    JSONObject meter = meterTables.getJSONObject(j);
                    String meterID = meter.getInt("meter-id") + "";
                    int bandWidth = meter.getJSONObject("meter-band-headers")
                                         .getJSONArray("meter-band-header")
                                         .getJSONObject(0)
                                         .getInt("drop-rate");
                    MeterInfo info = new MeterInfo(switchID + meterID, switchID, meterID, bandWidth);
                    meterList.add(info);
                }
            }
        } catch (Exception e) {
        }

    }

    public MeterInfo getMeter(String switchID, String meterID) {
        for (MeterInfo meter : meterList) {
            if (meter.switchID.equals(switchID) && meter.meterID.equals(meterID)) return meter;
        }
        return null;
    }

    public void deleteAll() {
        getUpdate();
        for (MeterInfo meter : meterList) {
            connector.deleteMeter(meter.switchID, meter.meterID);
        }
        availableMeterID.clear();
    }

    public ArrayList<MeterInfo> getMeterList() {
        getUpdate();
        ArrayList<MeterInfo> ans = new ArrayList<>();
        for (MeterInfo info : meterList)
            ans.add(info);
        return ans;
    }
}
