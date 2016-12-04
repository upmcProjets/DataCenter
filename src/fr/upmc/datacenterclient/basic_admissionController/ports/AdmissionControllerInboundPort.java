/**
 * 
 */
package fr.upmc.datacenterclient.basic_admissionController.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenterclient.basic_admissionController.components.AdmissionController;
import fr.upmc.datacenterclient.basic_admissionController.interfaces.AdmissionControllerI;

/**
 * La class <code>AdmissionControllerInboundPort</code> extend <code>AbstractInboundPort</code>
 * et implements <code>AdmissionControllerI</code>
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
public class AdmissionControllerInboundPort 
extends AbstractInboundPort 
implements AdmissionControllerI {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3740254375521688239L;

	public AdmissionControllerInboundPort(ComponentI owner)
			throws Exception {
		super(AdmissionControllerI.class, owner);

		assert owner != null && owner instanceof AdmissionController;

	}

	public AdmissionControllerInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, AdmissionControllerI.class, owner);
		
		assert owner != null && owner instanceof AdmissionController && uri != null;

	}
	
	/**
	 * @see fr.upmc.datacenterclient.basic_admissionController.interfaces.AdmissionControllerI#submitApplication(java.lang.String, int)
	 */
	@Override
	public String submitApplication(String rgNotificationInboundPort, int nbVm) throws Exception {
		assert rgNotificationInboundPort != null ;	
		final String rgsop = rgNotificationInboundPort ;
		final int nbrVm = nbVm ;
		
		final AdmissionController ac = (AdmissionController) this.owner ;

		return this.owner.handleRequestSync(
				new ComponentI.ComponentService<String>() {
					@Override
					public String call() throws Exception {	
						return ac.submitApplication(rgsop,nbrVm);
					}
				});
	}

}
