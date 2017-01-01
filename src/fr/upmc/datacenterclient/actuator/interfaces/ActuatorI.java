package fr.upmc.datacenterclient.actuator.interfaces;

import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.components.interfaces.DataRequiredI;

/**
 * @author chelbi
 *
 */
public interface ActuatorI 
extends DataOfferedI, 
		DataRequiredI {
	
	// Computer Management
	
	public void addCore(String computerURI,String vmURI, int nbcore) throws Exception;

	public void deleteCore(String computerURI,String vmURI, int nbcore) throws Exception;

	public void updateFrequency(String processorURI,int numCore, int frequency) throws Exception;

	// applicationVm Management
	
	public String removeVM(String requestSubmissionOutboundPort) throws Exception;
	
    public String addVM(int coreCount) throws Exception;

}
