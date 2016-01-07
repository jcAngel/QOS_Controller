package basicUtil;

/**
 * Created by jiachen on 28/12/15.
 */
public class RouteNode {
    public Switch switchNode;
    public SwitchPort inPort, outPort;

    public RouteNode(Switch switchNode, SwitchPort inPort, SwitchPort outPort) {
        this.switchNode = switchNode;
        this.inPort = inPort;
        this.outPort = outPort;
    }
}
