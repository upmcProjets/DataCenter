/**
 * 
 */
package fr.upmc.datacenterclient.actuator.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenterclient.actuator.interfaces.ActuatorI;

/**
 * @author chelbi
 *
 */
public class ActuatorConnector extends AbstractConnector implements ActuatorI {

	/**
	 *  @see fr.upmc.datacenterclient.actuator.interfaces.ActuatorI#addCore(java.lang.String, java.lang.String, int)
	 */
	@Override
	public void addCore(String computerURI, String vmURI, int nbcore) throws Exception {
		((ActuatorI)this.offering).addCore(computerURI, vmURI, nbcore);
	}

	/**
	 *  @see fr.upmc.datacenterclient.actuator.interfaces.ActuatorI#deleteCore(java.lang.String, java.lang.String, int)
	 */
	@Override
	public void deleteCore(String computerURI, String vmURI, int nbcore) throws Exception {
		((ActuatorI)this.offering).deleteCore(computerURI, vmURI, nbcore);

	}

	/**
	 *  @see fr.upmc.datacenterclient.actuator.interfaces.ActuatorI#updateFrequency(java.lang.String, int, int)
	 */
	@Override
	public void updateFrequency(String processorURI, int numCore, int frequency) throws Exception {
		((ActuatorI)this.offering).updateFrequency(processorURI, numCore, frequency);

	}

	/**
	 *  @see fr.upmc.datacenterclient.actuator.interfaces.ActuatorI#removeVM(java.lang.String)
	 */
	@Override
	public String removeVM(String requestSubmissionOutboundPort) throws Exception {	
		return ((ActuatorI)this.offering).removeVM(requestSubmissionOutboundPort);
	}

	/**
	 *  @see fr.upmc.datacenterclient.actuator.interfaces.ActuatorI#addVM(int)
	 */
	@Override
	public String addVM(int coreCount) throws Exception {
		
		return ((ActuatorI)this.offering).addVM(coreCount);
	}

}
