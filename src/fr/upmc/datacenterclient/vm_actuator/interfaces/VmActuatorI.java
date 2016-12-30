package fr.upmc.datacenterclient.vm_actuator.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

/**
 * Created by Hacene on 12/28/2016.
 */
public interface VmActuatorI extends OfferedI, RequiredI {
    String removeVM(String requestSubmissionOutboundPort) throws Exception;
    String addVM(int coreCount) throws Exception;
}
