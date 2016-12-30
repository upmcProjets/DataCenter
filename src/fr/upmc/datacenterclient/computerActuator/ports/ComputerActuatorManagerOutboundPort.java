package fr.upmc.datacenterclient.computerActuator.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenterclient.computerActuator.interfaces.ComputerActuatorManagerI;

public class ComputerActuatorManagerOutboundPort 
extends AbstractOutboundPort 
implements ComputerActuatorManagerI {

	public ComputerActuatorManagerOutboundPort(ComponentI owner) throws Exception {
		super(ComputerActuatorManagerI.class, owner);
		assert owner !=null  ;
	}

		
	public ComputerActuatorManagerOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri,ComputerActuatorManagerI.class, owner);
		assert owner !=null && uri != null;
	}

	


	@Override
	public void updateFrequency(String processorURI, int numCore, int frequency) throws Exception {
		// TODO Auto-generated method stub
		((ComputerActuatorManagerI)this.connector).updateFrequency(processorURI, numCore, frequency);;
	}


	@Override
	public void addCore(String computerURI, String vmURI, int nbcore) throws Exception {
		// TODO Auto-generated method stub
		 ((ComputerActuatorManagerI)this.connector).addCore(computerURI, vmURI, nbcore);
	}


	@Override
	public void deleteCore(String computerURI, String vmURI, int nbcore) throws Exception {
		// TODO Auto-generated method stub
       ((ComputerActuatorManagerI)this.connector).deleteCore(computerURI, vmURI, nbcore);		
	}

}
