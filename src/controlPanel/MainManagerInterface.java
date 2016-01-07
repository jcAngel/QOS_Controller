package controlPanel;

import basicUtil.FlowInfo;
import basicUtil.Host;
import basicUtil.MeterInfo;
import basicUtil.Switch;

/**
 * Created by jiachen on 29/12/15.
 */
public interface MainManagerInterface {
    int getAvailableFlowID(String switchID, String tableID);
    int getAvailableMeterID(String switchID);
    Host getHost(String ip);
    Switch getSwitch(String switchID);
    MeterInfo getMeter(String switchID, String meterID);
    FlowInfo getFlow(String switchID, String tableID, String flowID);
}
