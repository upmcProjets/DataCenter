package fr.upmc.datacenterclient.computerActuator.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

public interface ComputerActuatorManagerI
extends OfferedI,RequiredI
{
	
		
	public void addCore(String computerURI,String vmURI, int nbcore) throws Exception;

	public void deleteCore(String computerURI,String vmURI, int nbcore) throws Exception;

	public void updateFrequency(String processorURI,int numCore, int frequency) throws Exception;

	
}
