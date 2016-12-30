package fr.upmc.datacenterclient.adaptationcontroller.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenterclient.adaptationcontroller.interfaces.AdaptationControllerManagerI;

public class AdaptationControllerManagerInboundPort
extends AbstractInboundPort
implements AdaptationControllerManagerI {

	public AdaptationControllerManagerInboundPort(Class<?> implementedInterface, ComponentI owner) throws Exception {
		super(implementedInterface, owner);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
