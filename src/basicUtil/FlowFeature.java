package basicUtil;

/**
 * Created by jiachen on 28/12/15.
 */
public class FlowFeature {
    public String srcIP, dstIP;
    public int port;

    public FlowFeature(String src, String dst) {
        srcIP = src;
        dstIP = dst;
    }
}
