package fr.upmc.datacenterclient.vm_actuator.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenterclient.vm_actuator.interfaces.VmActuatorI;

/**
 * Created by Hacene on 12/28/2016.
 */
public class VmActuatorOutboundPort extends AbstractOutboundPort implements VmActuatorI {
    public VmActuatorOutboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, VmActuatorI.class, owner);
    }

    public VmActuatorOutboundPort(ComponentI owner) throws Exception {
        super(VmActuatorI.class, owner);
    }

    @Override
    public String removeVM(String requestSubmissionOutboundPort) throws Exception {
        return ((VmActuatorI)this.connector).removeVM(requestSubmissionOutboundPort);
    }

    @Override
    public String addVM(int coreCount) throws Exception {
        return ((VmActuatorI)this.connector).addVM(coreCount);
    }
}
