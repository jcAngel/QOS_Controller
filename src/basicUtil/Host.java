package basicUtil;

/**
 * Created by jiachen on 28/12/15.
 */
public class Host {
    public String name, ip, mac;
    public SwitchPort linkedSwitchPort;

    public Host(String name, String ip, SwitchPort switchPort) {
        this.name = name;
        this.ip = ip;
        this.linkedSwitchPort = switchPort;
    }

    public void setMAC(String MAC) {
        this.mac = MAC;
    }
}
