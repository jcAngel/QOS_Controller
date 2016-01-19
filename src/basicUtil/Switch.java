package basicUtil;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by jiachen on 28/12/15.
 */
public class Switch {
    public String name;
    public HashMap<Integer, SwitchPort> ports;
    private HashMap<SwitchPort, Pair<Switch, SwitchPort>> portLinkedSwitch;
    private HashMap<SwitchPort, Host> portLinkedHost;

    public Switch(String name) {
        ports = new HashMap<Integer, SwitchPort>();
        this.name = name;
        portLinkedSwitch = new HashMap<SwitchPort, Pair<Switch, SwitchPort>>();
        portLinkedHost = new HashMap<SwitchPort, Host>();
    }

    public SwitchPort addPort(int p) {
        if (ports.containsKey(p)) return ports.get(p);
        SwitchPort newPort = new SwitchPort(this, p);
        ports.put(p, newPort);
        return newPort;
    }

    public void addLinkedSwitch(SwitchPort port, Switch linked, SwitchPort linkedPort) {
        if (!ports.containsValue(port)) {
            System.err.println("Switch: " + name + " do not have port:" + port.port);
        }
        portLinkedSwitch.put(port, new Pair<Switch, SwitchPort>(linked, linkedPort));
    }

    public void addLinkedHost(SwitchPort port, Host host) {
        if (!ports.containsValue(port)) {
            System.err.println("Switch: " + name + " do not have port:" + port.port);
        }
        portLinkedHost.put(port, host);
    }

    public HashSet<SwitchPort> getPortsLinkedSwitch() {
        HashSet<SwitchPort> ans = new HashSet<SwitchPort>();
        for (SwitchPort port : portLinkedSwitch.keySet()) {
            ans.add(port);
        }
        return ans;
    }

    public HashSet<SwitchPort> getPortLinkedHost() {
        HashSet<SwitchPort> ans = new HashSet<SwitchPort>();
        for (SwitchPort port : portLinkedHost.keySet()) {
            ans.add(port);
        }
        return ans;
    }

    public HashSet<SwitchPort> getPorts() {
        HashSet<SwitchPort> ans = new HashSet<SwitchPort>();
        for (int x : ports.keySet()) {
            SwitchPort port = ports.get(x);
            ans.add(port);
        }
        return ans;
    }

    public Pair<Switch, SwitchPort> getLinkedSwitch(SwitchPort outPort) {
        return portLinkedSwitch.get(outPort);
    }

    public Host getLinkedHost(SwitchPort outPort) {
        return portLinkedHost.get(outPort);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Switch) {
            if (name.equals(((Switch) o).name)) return true;
        }
        return false;
    }
}
