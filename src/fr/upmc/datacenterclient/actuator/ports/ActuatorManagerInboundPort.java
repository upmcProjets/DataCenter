/**
 * 
 */
package fr.upmc.datacenterclient.actuator.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenterclient.actuator.interfaces.ActuatorI;

/**
 * @author chelbi
 *
 */
public class ActuatorManagerInboundPort 
extends AbstractInboundPort implements ActuatorI {

	
	private static final long serialVersionUID = -5045862902786812949L;

	public ActuatorManagerInboundPort(
			ComponentI owner
			)throws Exception {

		super(ActuatorI.class, owner);
		
		assert owner != null ;// && owner instanceof ActuatorManager;
	}

	public ActuatorManagerInboundPort(
			String uri,
			ComponentI owner
			)throws Exception {

		super(uri, ActuatorI.class, owner);
		assert owner != null ;//&& owner instanceof ComputerActuator && uri != null;
	}
	
	/**
	 *  @see fr.upmc.datacenterclient.actuator.interfaces.ActuatorI#addCore(java.lang.String, java.lang.String, int)
	 */
	@Override
	public void addCore(String computerURI, String vmURI, int nbcore) throws Exception {
		

	}

	/**
	 *  @see fr.upmc.datacenterclient.actuator.interfaces.ActuatorI#deleteCore(java.lang.String, java.lang.String, int)
	 */
	@Override
	public void deleteCore(String computerURI, String vmURI, int nbcore) throws Exception {
		// TODO Auto-generated method stub

	}

	/**
	 *  @see fr.upmc.datacenterclient.actuator.interfaces.ActuatorI#updateFrequency(java.lang.String, int, int)
	 */
	@Override
	public void updateFrequency(String processorURI, int numCore, int frequency) throws Exception {
		// TODO Auto-generated method stub

	}

	/**
	 *  @see fr.upmc.datacenterclient.actuator.interfaces.ActuatorI#removeVM(java.lang.String)
	 */
	@Override
	public String removeVM(String requestSubmissionOutboundPort) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 *  @see fr.upmc.datacenterclient.actuator.interfaces.ActuatorI#addVM(int)
	 */
	@Override
	public String addVM(int coreCount) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
