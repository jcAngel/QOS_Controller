package basicUtil;

/**
 * Created by jiachen on 28/12/15.
 */
public class FlowFeature {
    public String srcIP, dstIP;
    public int port;
    public double weight;

    public FlowFeature(String src, String dst, double w) {
        srcIP = src;
        dstIP = dst;
        weight = w;
    }

    public FlowFeature(String src, String dst) {
        srcIP = src;
        dstIP = dst;
        weight = 1.0;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof FlowFeature) {
            if (((FlowFeature) o).srcIP.equals(srcIP) && ((FlowFeature) o).dstIP.equals(dstIP))
                return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        String ans = srcIP + dstIP;
        return ans.hashCode();
    }
}
