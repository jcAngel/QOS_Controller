package basicUtil; /**
 * Created by jiachen on 05/12/15.
 */

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Connector {
    private String configURL = BasicParam.configURL;
    private String operationalURL = BasicParam.operationalURL;
    private String nodePostfix = "node/";
    private String tablePostfix = "table/";
    private String flowPostfix = "flow/";
    private String meterPostfix = "meter/";

    private String user = BasicParam.user;
    private String pwd = BasicParam.pwd;

    public String putFlow(String switchid, String tableid, String flowid, String xml) {
        String s = configURL + nodePostfix + switchid + "/" + tablePostfix + tableid + "/" + flowPostfix + flowid;
        return putXMLToURL(s, xml);
    }

    public String getFlowInfoInConfig(String switchid, String tableid, String flowid) {
        return getFromURL(configURL + nodePostfix + switchid + "/" + tablePostfix + tableid + "/" + flowPostfix + flowid);
    }

    public String getFlowTableInConfig(String switchid, String tableid) {
        return getFromURL(configURL + nodePostfix + switchid + "/" + tablePostfix + tableid);
    }

    public String getAllInConfig() {
        return getFromURL(configURL);
    }

    public String getFlowInfoInOperational(String switchid, String tableid, String flowid) {
        return getFromURL(operationalURL + nodePostfix + switchid + "/" + tablePostfix + tableid + "/" + flowPostfix + flowid);
    }

    public String getFlowTableInOperational(String switchid, String tableid) {
        return getFromURL(operationalURL + nodePostfix + switchid + "/" + tablePostfix + tableid);
    }

    public String getAllInOperational() {
        return getFromURL(operationalURL);
    }

    public String deleteFlowTable(String switchid, String tableid) {
        return deleteOnURL(configURL + nodePostfix + switchid + "/" + tablePostfix + tableid);
    }

    public String deleteFlow(String switchid, String tableid, String flowid) {
        return deleteOnURL(configURL + nodePostfix + switchid + "/" + tablePostfix + tableid + "/" + flowPostfix + flowid);
    }

    public String putMeter(String switchid, String meterid, String xml) {
        return putXMLToURL(configURL + nodePostfix + switchid + "/" + meterPostfix + meterid, xml);
    }

    public String getMeterInfoInConfig(String switchid, String meterid) {
        return getFromURL(configURL + nodePostfix + switchid + "/" + meterPostfix + meterid);
    }

    public String getMeterInfoInOperational(String switchid, String meterid) {
        return getFromURL(operationalURL + nodePostfix + switchid + "/" + meterPostfix + meterid);
    }

    public String deleteMeter(String switchid, String meterid) {
        return deleteOnURL(configURL + nodePostfix + switchid + "/" + meterPostfix + meterid);
    }

    private String deleteOnURL(String url) {
        URL baseURL;
        try {
            baseURL = new URL(url);
        } catch (Exception e) {
            //e.printStackTrace();
            return "Connection Error At getAll.";
        }
        HttpURLConnection httpURLConnection = null;
        String ans = "";
        try {
            httpURLConnection = (HttpURLConnection) baseURL.openConnection();
            httpURLConnection.setRequestProperty("Content-Type", "text/xml");

            String userpass = user + ":" + pwd;
            String basicAuth = "Basic " + new String(new Base64().encode(userpass.getBytes()));
            httpURLConnection.setRequestProperty("Authorization", basicAuth);

            httpURLConnection.setRequestMethod("DELETE");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(httpURLConnection.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null)
                ans = ans + inputLine;
            in.close();

        } catch (Exception exception) {
            exception.printStackTrace();
        }  finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return ans;
    }

    public String putXMLToURL(String url, String xml) {
        URL baseURL;
        try {
            baseURL = new URL(url);
            //System.out.println(baseURL.toString());
        } catch (Exception e) {
            //e.printStackTrace();
            return "Connection Error At putFlow.";
        }
        HttpURLConnection httpURLConnection = null;
        DataOutputStream dataOutputStream = null;
        String ans = "";
        try {
            httpURLConnection = (HttpURLConnection) baseURL.openConnection();
            httpURLConnection.setRequestProperty("Content-Type", "text/xml");

            String userpass = user + ":" + pwd;
            String basicAuth = "Basic " + new String(new Base64().encode(userpass.getBytes()));
            httpURLConnection.setRequestProperty("Authorization", basicAuth);

            httpURLConnection.setRequestMethod("PUT");
            httpURLConnection.setDoOutput(true);
            dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            dataOutputStream.write(xml.getBytes());
            dataOutputStream.flush();
            dataOutputStream.close();

            ans = "Responce Code: " + httpURLConnection.getResponseCode() + "\n" + httpURLConnection.getResponseMessage();

            httpURLConnection.disconnect();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return ans;
    }

    public String postXMLToURL(String url, String xml) {
        URL baseURL;
        try {
            baseURL = new URL(url);
            //System.out.println(baseURL.toString());
        } catch (Exception e) {
            //e.printStackTrace();
            return "Connection Error At putFlow.";
        }
        HttpURLConnection httpURLConnection = null;
        DataOutputStream dataOutputStream = null;
        String ans = "";
        try {
            httpURLConnection = (HttpURLConnection) baseURL.openConnection();
            httpURLConnection.setRequestProperty("Content-Type", "text/xml");

            String userpass = user + ":" + pwd;
            String basicAuth = "Basic " + new String(new Base64().encode(userpass.getBytes()));
            httpURLConnection.setRequestProperty("Authorization", basicAuth);

            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            dataOutputStream.write(xml.getBytes());
            dataOutputStream.flush();
            dataOutputStream.close();

            ans = "Responce Code: " + httpURLConnection.getResponseCode() + "\n" + httpURLConnection.getResponseMessage();

            httpURLConnection.disconnect();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return ans;
    }

    public String getFromURL(String url) {
        URL baseURL;
        try {
            baseURL = new URL(url);
        } catch (Exception e) {
            e.printStackTrace();
            return "Connection Error At getAll.";
        }
        HttpURLConnection httpURLConnection = null;
        String ans = "";
        try {
            httpURLConnection = (HttpURLConnection) baseURL.openConnection();
            httpURLConnection.setRequestProperty("Content-Type", "text/xml");

            String userpass = user + ":" + pwd;
            String basicAuth = "Basic " + new String(new Base64().encode(userpass.getBytes()));
            httpURLConnection.setRequestProperty("Authorization", basicAuth);

            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(httpURLConnection.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null)
                ans = ans + inputLine;
            in.close();

        } catch (Exception exception) {
            exception.printStackTrace();
        }  finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return ans;
    }
}
