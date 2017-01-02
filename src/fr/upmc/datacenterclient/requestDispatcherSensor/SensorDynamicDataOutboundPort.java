package fr.upmc.datacenterclient.requestDispatcher.sensor;

import fr.upmc.components.ComponentI;
import fr.upmc.components.interfaces.DataRequiredI.DataI;
import fr.upmc.datacenter.ports.AbstractControlledDataOutboundPort;

public class SensorDynamicDataOutboundPort 
extends AbstractControlledDataOutboundPort {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5130150600553941540L;
	protected String portURI ;


	/**
	 * @param owner
	 * @throws Exception
	 */
	public SensorDynamicDataOutboundPort( 
			ComponentI owner,
			String portURI) throws Exception {
		super(owner);
		this.portURI = portURI;
	}


	/**
	 * @param uri
	 * @param owner
	 * @throws Exception
	 */
	public SensorDynamicDataOutboundPort(
			String uri, 
			ComponentI owner,
			String sensorURI
			) throws Exception {
		super(uri, owner);
		this.portURI = sensorURI;
	}


	@Override
	public void receive(DataI d) throws Exception {
		((RequestDispatcherSensorConsumerI) this.owner).
		acceptRequestDispatcherDynamicData(portURI, (SensorDynamicDataI)d);

	}

}
