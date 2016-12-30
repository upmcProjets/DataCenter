package fr.upmc.datacenterclient.vm_actuator.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenterclient.vm_actuator.VmActuator;
import fr.upmc.datacenterclient.vm_actuator.interfaces.VmActuatorI;

/**
 * Created by Hacene on 12/28/2016.
 */
public class VmActuatorInboundPort extends AbstractInboundPort implements VmActuatorI {
    public VmActuatorInboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, VmActuatorI.class, owner);
    }

    public VmActuatorInboundPort(ComponentI owner) throws Exception {
        super(VmActuatorI.class, owner);
    }

    @Override
    public String removeVM(final String requestSubmissionOutboundPort) throws Exception {
        final VmActuator actiator = (VmActuator) this.owner;
        return this.owner.handleRequestSync(new ComponentI.ComponentService<String>() {
            @Override
            public String call() throws Exception {
                return actiator.removeVM(requestSubmissionOutboundPort);
            }
        });
    }

    @Override
    public String addVM(final int coreCount) throws Exception {
        final VmActuator actiator = (VmActuator) this.owner;
        return this.owner.handleRequestSync(new ComponentI.ComponentService<String>() {
            @Override
            public String call() throws Exception {
                return actiator.addVM(coreCount);
            }
        });
    }
}
