package fr.upmc.datacenterclient.computerActuator.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenterclient.computerActuator.interfaces.ComputerActuatorManagerI;

public class ComputerActuatorManagerConnector
extends AbstractConnector 
implements ComputerActuatorManagerI{

	

	

	@Override
	public void updateFrequency(String processorURI, int numCore, int frequency) throws Exception {
		// TODO Auto-generated method stub
		((ComputerActuatorManagerI)this.offering).updateFrequency(processorURI, numCore, frequency);
	}

	@Override
	public void addCore(String computerURI, String vmURI, int nbcore) throws Exception {
		// TODO Auto-generated method stub
		((ComputerActuatorManagerI) this.offering).addCore(computerURI, vmURI, nbcore);;
		
	}

	@Override
	public void deleteCore(String computerURI, String vmURI, int nbcore) throws Exception {
		// TODO Auto-generated method stub
		((ComputerActuatorManagerI)this.offering).deleteCore(computerURI, vmURI, nbcore);
	}

}
