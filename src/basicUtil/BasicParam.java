package basicUtil;

/**
 * Created by jiachen on 21/01/16.
 */
public class BasicParam {
    public static int linkBandWidth = 200000;
    public static String controllerIP = "http://127.0.0.1:8181/";
    public static String configURL = controllerIP + "restconf/config/opendaylight-inventory:nodes/";
    public static String operationalURL = controllerIP + "restconf/operational/opendaylight-inventory:nodes/";
    public static String topoURL = controllerIP + "restconf/operational/network-topology:network-topology/";
    public static String salRemoveFlowURL = controllerIP + "restconf/operations/sal-flow:remove-flow";
    public static String user = "admin";
    public static String pwd = "admin";
    public static String flowStatisticURL = operationalURL + "node/{switchID}/flow-node-inventory:table/{tableID}/flow/{flowID}/opendaylight-flow-statistics:flow-statistics";
    public static String meterStatisticURL = operationalURL + "node/{switchID}/flow-node-inventory:meter/{meterID}/opendaylight-meter-statistics:meter-statistics/";
}
