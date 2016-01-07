package basicUtil;

/**
 * Created by jiachen on 28/12/15.
 */
public class SwitchPort {
    public Switch container;
    public int port;

    public SwitchPort(Switch container, int port) {
        this.container = container;
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SwitchPort) {
            if (container.equals(((SwitchPort) o).container) && port == ((SwitchPort) o).port) return true;
        }
        return false;
    }
}
