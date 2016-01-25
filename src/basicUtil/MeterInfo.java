package basicUtil;

/**
 * Created by jiachen on 21/12/15.
 */
public class MeterInfo {
    public String name;
    public String switchID;
    public String meterID;
    public int bandWidth;

    public MeterInfo(String name, String sid, String mid, int bandWidth) {
        this.name = name;
        switchID = sid;
        meterID = mid;
        this.bandWidth = bandWidth;
    }

    @Override
    public String toString() {
        return "{switchID: " + switchID + ", meterID: " + meterID + "}";
    }

    @Override
    public boolean equals(Object meter) {
        if (meter instanceof MeterInfo) {
            if (!((MeterInfo) meter).switchID.equals(this.switchID)) return false;
            else if (!((MeterInfo) meter).meterID.equals(this.meterID)) return false;
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        String ans = switchID + "/meter:" + meterID;
        return ans.hashCode();
    }
}
