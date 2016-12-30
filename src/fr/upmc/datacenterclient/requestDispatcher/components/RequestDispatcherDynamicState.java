/**
 * 
 */
package fr.upmc.datacenterclient.requestDispatcher.components;

import fr.upmc.datacenterclient.requestDispatcher.interfaces.RequestDispatcherDynamicStateI;

/**
 * @author chelbi
 *
 */
public class RequestDispatcherDynamicState 
implements RequestDispatcherDynamicStateI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String dispatcherURI ;
	private long timeRequest;
	

	/**
	 * @param dispatcherURI
	 * @param timeRequest
	 */
	public RequestDispatcherDynamicState(String dispatcherURI, long timeRequest) {
		super();
		this.dispatcherURI = dispatcherURI;
		this.timeRequest = timeRequest;
	}

	/**
	 *  @see fr.upmc.datacenterclient.requestDispatcher.interfaces.RequestDispatcherDynamicStateI#getDispatcherURI()
	 */
	@Override
	public String getDispatcherURI() {
		
		return this.dispatcherURI;
	}

	/**
	 *  @see fr.upmc.datacenterclient.requestDispatcher.interfaces.RequestDispatcherDynamicStateI#getTimeRequest()
	 */
	@Override
	public long getTimeRequest() {
		return this.timeRequest;
	}

}
