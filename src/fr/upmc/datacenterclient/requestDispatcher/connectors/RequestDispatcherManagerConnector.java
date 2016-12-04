/**
 * 
 */
package fr.upmc.datacenterclient.requestDispatcher.connectors;

import java.util.List;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenterclient.requestDispatcher.interfaces.RequestDispatcherManagerI;

/**
 * The class <code>RequestDispatcherManagerConnector</code> implements a
 * standard client/server connector for the management request dispatcher
 * management interface.
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

public class RequestDispatcherManagerConnector 
extends AbstractConnector 
implements RequestDispatcherManagerI {

	/**
	 * @see fr.upmc.datacenterclient.requestDispatcher.interfaces.RequestDispatcherManagerI#connectWithVMS(java.util.List)
	 */
	@Override
	public List<String> connectWithVMS(List<String> vms) throws Exception {
		return ((RequestDispatcherManagerI)this.offering).connectWithVMS(vms);
	}

	/**
	 * @see fr.upmc.datacenterclient.requestDispatcher.interfaces.RequestDispatcherManagerI#ajouterVM(java.lang.String)
	 */
	@Override
	public String ajouterVM(String requestSubmissionInboundPort) throws Exception {
		return ((RequestDispatcherManagerI)this.offering).ajouterVM(requestSubmissionInboundPort);
	}

	/**
	 * @see fr.upmc.datacenterclient.requestDispatcher.interfaces.RequestDispatcherManagerI#supprimerVM(java.lang.String)
	 */
	@Override
	public void supprimerVM(String requestSubmissionOutboundPort) throws Exception {
		((RequestDispatcherManagerI)this.offering).supprimerVM(requestSubmissionOutboundPort);

	}

}
