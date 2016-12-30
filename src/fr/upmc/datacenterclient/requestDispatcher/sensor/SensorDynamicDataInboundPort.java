/**
 * 
 */
package fr.upmc.datacenterclient.requestDispatcher.sensor;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.datacenter.ports.AbstractControlledDataInboundPort;
import fr.upmc.datacenterclient.requestDispatcher.components.RequestDispatcher;

/**
 * @author chelbi
 *
 */
public class SensorDynamicDataInboundPort 
extends AbstractControlledDataInboundPort {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param owner
	 * @throws Exception
	 */
	public SensorDynamicDataInboundPort(ComponentI owner) throws Exception {
		super(owner);
		
	}

	/**
	 * @param uri
	 * @param owner
	 * @throws Exception
	 */
	public SensorDynamicDataInboundPort(
			String uri, 
			ComponentI owner) throws Exception {
		super(uri, owner);
	}

	/**
	 *  @see fr.upmc.components.interfaces.DataOfferedI.PullI#get()
	 */
	@Override
	public DataOfferedI.DataI get() throws Exception {
		final RequestDispatcherMonitor rdm = (RequestDispatcherMonitor) this.owner;
		return rdm.handleRequestSync(new ComponentService<DataOfferedI.DataI>() {

			@Override
			public DataOfferedI.DataI call() throws Exception {
			
				//return rd.getDynamicState() ;
				return rdm.getDynamicData();
			}
		});
	}

}
