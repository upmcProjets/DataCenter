package fr.upmc.datacenterclient.computerActuator.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenterclient.computerActuator.ComputerActuator;
import fr.upmc.datacenterclient.computerActuator.interfaces.ComputerActuatorManagerI;

public class ComputerActuatorManagerInboundPort
extends AbstractInboundPort
implements ComputerActuatorManagerI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ComputerActuatorManagerInboundPort(
			ComponentI owner
			)throws Exception {

		super(ComputerActuatorManagerI.class, owner);
		
		assert owner != null && owner instanceof ComputerActuator;
	}

	public ComputerActuatorManagerInboundPort(
			String uri,
			ComponentI owner
			)throws Exception {

		super(uri, ComputerActuatorManagerI.class, owner);
		assert owner != null && owner instanceof ComputerActuator && uri != null;
	}
	
	
	
	

	@Override
	public void updateFrequency(String processorURI, int numCore, int frequency) throws Exception {
		// TODO Auto-generated method stub
		((ComputerActuator)this.owner).updateFrequency(processorURI, numCore, frequency);
		
	}

	@Override
	public void addCore(String computerURI, String vmURI, int nbcore) throws Exception {
		// TODO Auto-generated method stub
		((ComputerActuator)this.owner).addCore(computerURI, vmURI, nbcore);
	}

	@Override
	public void deleteCore(String computerURI, String vmURI, int nbcore) throws Exception {
		// TODO Auto-generated method stub
		((ComputerActuator)this.owner).deleteCore(computerURI, vmURI, nbcore);
		
	}

}
