package optimizationAlgorithm;

import basicUtil.FlowFeature;
import basicUtil.NeedModifyList;
import basicUtil.Route;

import java.util.ArrayList;

/**
 * Created by jiachen on 21/12/15.
 */
public interface OptimizationInterface {
    ArrayList<Route> initialOptimization();
    NeedModifyList addNewFlow(FlowFeature feature);
}
