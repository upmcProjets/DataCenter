/**
 * 
 */
package fr.upmc.datacenterclient.actuator.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenterclient.actuator.interfaces.ActuatorI;

/**
 * @author chelbi
 *
 */
public class ActuatorManagerOutboundPort 
extends AbstractOutboundPort 
implements ActuatorI {
	
	public ActuatorManagerOutboundPort(ComponentI owner) throws Exception {
		super(ActuatorI.class, owner);
		assert owner !=null  ;
	}

		
	public ActuatorManagerOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri,ActuatorI.class, owner);
		assert owner !=null && uri != null;
	}

	/**
	 *  @see fr.upmc.datacenterclient.actuator.interfaces.ActuatorI#addCore(java.lang.String, java.lang.String, int)
	 */
	@Override
	public void addCore(String computerURI, String vmURI, int nbcore) throws Exception {
		((ActuatorI)this.connector).addCore(computerURI, vmURI, nbcore);

	}

	/**
	 *  @see fr.upmc.datacenterclient.actuator.interfaces.ActuatorI#deleteCore(java.lang.String, java.lang.String, int)
	 */
	@Override
	public void deleteCore(String computerURI, String vmURI, int nbcore) throws Exception {
		((ActuatorI)this.connector).deleteCore(computerURI, vmURI, nbcore);

	}

	/**
	 *  @see fr.upmc.datacenterclient.actuator.interfaces.ActuatorI#updateFrequency(java.lang.String, int, int)
	 */
	@Override
	public void updateFrequency(String processorURI, int numCore, int frequency) throws Exception {
		((ActuatorI)this.connector).updateFrequency(processorURI, numCore, frequency);

	}

	/**
	 *  @see fr.upmc.datacenterclient.actuator.interfaces.ActuatorI#removeVM(java.lang.String)
	 */
	@Override
	public String removeVM(String requestSubmissionOutboundPort) throws Exception {
		
		return ((ActuatorI)this.connector).removeVM(requestSubmissionOutboundPort);
	}

	/**
	 *  @see fr.upmc.datacenterclient.actuator.interfaces.ActuatorI#addVM(int)
	 */
	@Override
	public String addVM(int coreCount) throws Exception {
		
		return ((ActuatorI)this.connector).addVM(coreCount);
	}

}
