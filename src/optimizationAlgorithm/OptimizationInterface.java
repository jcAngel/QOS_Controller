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

    /**
     * Add a new flow depend on the feature.
     * @param feature feature represent a flow.
     * @return NeedModifyList Which FlowEntry and MeterEntry should be modified or deleted.
     */
    NeedModifyList addNewFlow(FlowFeature feature);

    /**
     * Delete a existing flow depend on the feature.
     * @param feature feature represent a flow.
     * @return NeedModifyList Which FlowEntry and MeterEntry should be modified or deleted.
     */
    NeedModifyList deleteFlow(FlowFeature feature);

    /**
     * Remove all flows.
     */
    void clearAll();
}
