/**
 * 
 */
package fr.upmc.datacenterclient.requestDispatcher.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.datacenter.ports.AbstractControlledDataInboundPort;
import fr.upmc.datacenterclient.requestDispatcher.components.RequestDispatcher;

/**
 * @author chelbi
 *
 */
public class RequestDispatcherDynamicStateInboundPort 
extends AbstractControlledDataInboundPort {

	

	private static final long serialVersionUID = -8153370816597687994L;

	public RequestDispatcherDynamicStateInboundPort(
			ComponentI owner
			) throws Exception {
		super(owner);
		assert owner instanceof RequestDispatcher;
		
	}

	public RequestDispatcherDynamicStateInboundPort(
			String uri, 
			ComponentI owner
			) throws Exception {
		super(uri, owner);
		assert owner instanceof RequestDispatcher;
		
	}

	/** 
	 * @see fr.upmc.components.interfaces.DataOfferedI.PullI#get()
	 */
	@Override
	public DataOfferedI.DataI get() throws Exception {
		
		final RequestDispatcher rd = (RequestDispatcher) this.owner;
		return rd.handleRequestSync(new ComponentService<DataOfferedI.DataI>() {

			@Override
			public DataOfferedI.DataI call() throws Exception {
			
				return rd.getDynamicState() ;
			}
		});
	}

	
	

}
