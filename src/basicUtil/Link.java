package basicUtil;

/**
 * Created by jiachen on 29/12/15.
 */

/*
 * This class represent the link, from one switch outPort to another switch's inPort.
 */
public class Link {
    public SwitchPort outPort, inPort;

    public Link(SwitchPort outPort, SwitchPort inPort) {
        this.outPort = outPort;
        this.inPort = inPort;
    }
}
