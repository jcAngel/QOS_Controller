package controlPanel; /**
 * Created by jiachen on 04/12/15.
 */

import basicUtil.Connector;
import basicUtil.FlowConstructor;
import basicUtil.MeterConstructor;
import infoManager.InventoryManager;

public class Main {

    public static class FlowManager {
        Connector connector;

        FlowManager() {
            connector = new Connector();
        }

        public void getSwitches() {
            InventoryManager switchManager = new InventoryManager(connector);
        }

        public void putMeter() {
            MeterConstructor meter = new MeterConstructor();
            meter.setMeterID("3");
            meter.setMeterName("justatest");
            meter.setFlag("meter-kbps");
            meter.setMeterBandType(0, "ofpmbt-drop", 700000, 10000);
            System.out.println(meter.getXMLString());
            System.out.println(connector.putMeter("openflow:14721744650432675840", 3 + "", meter.getXMLString()));

            meter = new MeterConstructor();
            meter.setMeterID("4");
            meter.setMeterName("meter4");
            meter.setFlag("meter-kbps");
            meter.setMeterBandType(0, "ofpmbt-drop", 500000, 10000);
            System.out.println(meter.getXMLString());
            System.out.println(connector.putMeter("openflow:14721744650432675840", 4 + "", meter.getXMLString()));
        }

        public void putFlow() {
            FlowConstructor flow = new FlowConstructor(120, 0, 0, "15to20", "0", "3");
            flow.setEthType(2048);
            flow.setIpv4Src("10.10.10.15/32");
            flow.setIpv4Dst("10.10.10.20/32");
            flow.setMeter(0, "3");
            flow.setFlowName("hahaha");
            String[] str = {"output-action", "output-node-connector", "4"};
            flow.setAction(1, 0, str);
            System.out.println(flow.getXMLString());

            System.out.println(connector.putFlow("openflow:14721744650432675840", "0", "3", flow.getXMLString()));

            flow = new FlowConstructor(120, 0, 0, "10to20", "0", "4");
            flow.setEthType(2048);
            flow.setIpv4Src("10.10.10.10/32");
            flow.setIpv4Dst("10.10.10.20/32");
            flow.setMeter(0, "4");
            flow.setFlowName("10To20");
            flow.setAction(1, 0, str);
            System.out.println(flow.getXMLString());

            System.out.println(connector.putFlow("openflow:14721744650432675840", "0", "4", flow.getXMLString()));
        }

        public void putArp() {
            FlowConstructor flow = new FlowConstructor(10, 0, 0, "arpFlood", "0", "5");
            flow.setEthType(2054);
            String[] str = {"output-action", "output-node-connector", "4"};
            flow.setAction(0, 0, str);
            str[2] = "5";
            flow.setAction(0, 1, str);
            str[2] = "3";
            flow.setAction(0, 2, str);
            System.out.println(flow.getXMLString());
            System.out.println(connector.putFlow("openflow:14721744650432675840", "0", "5", flow.getXMLString()));
        }
    }

    public static void main(String[] args) {
        //FlowManager manager = new FlowManager();
        //manager.getSwitches();
        //manager.putMeter();
        //manager.putArp();
        //manager.putFlow();
        //SimpleManager manager = new SimpleManager();
        //infoManager.FlowManager flowManager = new infoManager.FlowManager();
        //System.out.println(Paths.get(".").toAbsolutePath().normalize().toString());
        new SimpleManager();
    }
}
