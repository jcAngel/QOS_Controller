package basicUtil;

import java.util.ArrayList;

/**
 * Created by jiachen on 28/12/15.
 */
public class Route {
    ArrayList<RouteNode> routeList;

    public Route() {
        routeList = new ArrayList<RouteNode>();
    }

    public void addNode(RouteNode routeNode) {
        routeList.add(routeNode);
    }

    public RouteNode get(int index) {
        return routeList.get(index);
    }

    public ArrayList<RouteNode> getAll() {
        return routeList;
    }

    public int size() {
        return routeList.size();
    }

    public int length() {
        return routeList.size();
    }
}
