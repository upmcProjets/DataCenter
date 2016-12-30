package fr.upmc.datacenterclient.adaptationcontroller.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenterclient.adaptationcontroller.interfaces.AdaptationControllerManagerI;

public class AdaptationControllerManagerOutboundPort
extends AbstractOutboundPort
implements AdaptationControllerManagerI {

	public AdaptationControllerManagerOutboundPort(Class<?> implementedInterface, ComponentI owner) throws Exception {
		super(implementedInterface, owner);
		// TODO Auto-generated constructor stub
	}

}
