/**
 * 
 */
package fr.upmc.datacenterclient.requestDispatcher.ports;

import java.util.List;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenterclient.requestDispatcher.interfaces.RequestDispatcherManagerI;

/**
 * La class <code>RequestDispatcherManagerOutboundPort</code> extend <code>AbstractOutboundPort</code>
 * et implements <code>RequestDispatcherManagerI</code>
 * 
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * @authors zahir CHELBI, hacene KEDJAR, lyes CHELFI
 */
public class RequestDispatcherManagerOutboundPort 
extends AbstractOutboundPort 

implements RequestDispatcherManagerI {

	public RequestDispatcherManagerOutboundPort(ComponentI owner) throws Exception {
		super(RequestDispatcherManagerI.class, owner);
		assert owner !=null  ;
	}

	public RequestDispatcherManagerOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri,RequestDispatcherManagerI.class, owner);
		assert owner !=null && uri != null;
	}

	
	
	/**
	 * @see fr.upmc.datacenterclient.requestDispatcher.interfaces.RequestDispatcherManagerI#connectWithVMS(java.util.List)
	 */
	@Override
	public List<String> connectWithVMS(List<String> vms) throws Exception {
		return ((RequestDispatcherManagerI)this.connector).connectWithVMS(vms);
	}

	/**
	 * @see fr.upmc.datacenterclient.requestDispatcher.interfaces.RequestDispatcherManagerI#ajouterVM(java.lang.String)
	 */
	@Override
	public String ajouterVM(String requestSubmissionInboundPort) throws Exception {
		return ((RequestDispatcherManagerI)this.connector).ajouterVM(requestSubmissionInboundPort);
	}

	/**
	 * @see fr.upmc.datacenterclient.requestDispatcher.interfaces.RequestDispatcherManagerI#supprimerVM(java.lang.String)
	 */
	@Override
	public void supprimerVM(String requestSubmissionOutboundPort) throws Exception {
		 ((RequestDispatcherManagerI)this.connector).supprimerVM(requestSubmissionOutboundPort);

	}

}
