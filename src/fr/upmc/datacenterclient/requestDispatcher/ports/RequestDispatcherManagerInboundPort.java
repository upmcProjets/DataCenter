/**
 * 
 */
package fr.upmc.datacenterclient.requestDispatcher.ports;

import java.util.List;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenterclient.requestDispatcher.components.RequestDispatcher;
import fr.upmc.datacenterclient.requestDispatcher.interfaces.RequestDispatcherManagerI;

/**
 * La class <code>RequestDispatcherManagerInboundPort</code> extend <code>AbstractInboundPort</code>
 * et implements <code>RequestDispatcherManagerI</code>
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
public class RequestDispatcherManagerInboundPort 
extends AbstractInboundPort 
implements RequestDispatcherManagerI 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5685810782696564998L;

	public RequestDispatcherManagerInboundPort(
			ComponentI owner
			)throws Exception {

		super(RequestDispatcherManagerI.class, owner);
		
		assert owner != null && owner instanceof RequestDispatcher;
	}

	public RequestDispatcherManagerInboundPort(
			String uri,
			ComponentI owner
			)throws Exception {

		super(uri, RequestDispatcherManagerI.class, owner);
		assert owner != null && owner instanceof RequestDispatcher && uri != null;
	}

	/**
	 * @see fr.upmc.datacenterclient.requestDispatcher.interfaces.RequestDispatcherManagerI#connectWithVMS(java.util.List)
	 */
	@Override
	public List<String> connectWithVMS(List<String> vms) throws Exception {

		assert vms != null && vms.size() > 0 ;
		
		final RequestDispatcher rp = (RequestDispatcher) this.owner ;
		final List<String> vmss = vms ;
		return this.owner.handleRequestSync(
				new ComponentService<List<String>>() {

					@Override
					public List<String> call() throws Exception {
						return rp.connectWithVMS(vmss);						
					}	
				});

	}

	/**
	 * @see fr.upmc.datacenterclient.requestDispatcher.interfaces.RequestDispatcherManagerI#ajouterVM(java.lang.String)
	 */
	@Override
	public String ajouterVM(
			String requestSubmissionInboundPort
			) throws Exception {
		assert requestSubmissionInboundPort != null ;
		final RequestDispatcher rp = (RequestDispatcher) this.owner ;
		final String requestSubmission =requestSubmissionInboundPort;

		return this.owner.handleRequestSync(
				new ComponentService<String>() {

					@Override
					public String call() throws Exception {
						return rp.ajouterVM(requestSubmission);
					}
				});
	}

	/**
	 * @see fr.upmc.datacenterclient.requestDispatcher.interfaces.RequestDispatcherManagerI#supprimerVM(java.lang.String)
	 */
	@Override
	public void supprimerVM(
			String requestSubmissionOutboundPort
			) throws Exception {
		
		assert requestSubmissionOutboundPort != null ;
		final RequestDispatcher rp = (RequestDispatcher) this.owner ;
		final String requestSubmission =requestSubmissionOutboundPort;

		this.owner.handleRequestAsync(
				new ComponentService<Void>() {

					@Override
					public Void call() throws Exception {
						rp.supprimerVM(requestSubmission);
						return null ;
					}
				});		


	}

}
