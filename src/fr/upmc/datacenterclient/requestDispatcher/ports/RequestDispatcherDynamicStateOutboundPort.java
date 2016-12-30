/**
 * 
 */
package fr.upmc.datacenterclient.requestDispatcher.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.interfaces.DataRequiredI.DataI;
import fr.upmc.datacenter.ports.AbstractControlledDataOutboundPort;
import fr.upmc.datacenterclient.requestDispatcher.interfaces.RequestDispatcherDataConsumerI;
import fr.upmc.datacenterclient.requestDispatcher.interfaces.RequestDispatcherDynamicStateI;

/**
 * @author chelbi
 *
 */
public class RequestDispatcherDynamicStateOutboundPort 
extends AbstractControlledDataOutboundPort {

	
	
	private static final long serialVersionUID = -4191750992011668043L;
	protected String			dispatcherURI ;

	public RequestDispatcherDynamicStateOutboundPort(
			ComponentI owner,
			String	dispatcherURI
			) throws Exception {
		super(owner);
		this.dispatcherURI = dispatcherURI;
		
		
	}

	public RequestDispatcherDynamicStateOutboundPort(
			String uri, 
			ComponentI owner,
			String	dispatcherURI
			) throws Exception {
		super(uri, owner);
		this.dispatcherURI = dispatcherURI;
		
	}

	/** 
	 * @see fr.upmc.components.interfaces.DataRequiredI.PushI#receive(fr.upmc.components.interfaces.DataRequiredI.DataI)
	 */
	@Override
	public void receive(DataI d) throws Exception {
		((RequestDispatcherDataConsumerI) this.owner).acceptRequestDispatcherDynamicData(dispatcherURI, (RequestDispatcherDynamicStateI) d) ;
	}

}
