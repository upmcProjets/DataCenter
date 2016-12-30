package fr.upmc.datacenterclient.vm_actuator.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenterclient.vm_actuator.interfaces.VmActuatorI;

/**
 * Created by Hacene on 12/28/2016.
 */
public class VmActuatorConnector extends AbstractConnector implements VmActuatorI {
    @Override
    public String removeVM(String requestSubmissionOutboundPort) throws Exception {
        return ((VmActuatorI)this.offering).removeVM(requestSubmissionOutboundPort);
    }

    @Override
    public String addVM(int coreCount) throws Exception {
        return ((VmActuatorI)this.offering).addVM(coreCount);
    }
}
