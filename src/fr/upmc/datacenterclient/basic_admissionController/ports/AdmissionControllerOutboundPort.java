/**
 * 
 */
package fr.upmc.datacenterclient.basic_admissionController.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenterclient.basic_admissionController.interfaces.AdmissionControllerI;

/**
 * La class <code>RequestDispatcherManagerOutboundPort</code> extend
 *  <code>AbstractOutboundPort</code>et implements <code>RequestDispatcherManagerI</code>
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

public class AdmissionControllerOutboundPort 
extends AbstractOutboundPort 
implements AdmissionControllerI {

	public AdmissionControllerOutboundPort( ComponentI owner) 
			throws Exception {

		super(AdmissionControllerI.class, owner);	
		assert owner != null ;
	}


	public AdmissionControllerOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, AdmissionControllerI.class, owner);
		assert owner != null  &&  uri != null;
	}

	/**
	 * @see fr.upmc.datacenterclient.basic_admissionController.interfaces.AdmissionControllerI#submitApplication(java.lang.String, java.lang.String, int)
	 */
	@Override
	public String submitApplication(String rgNotificationInboundPort, String notificationMethodeName, int nbVm) throws Exception {
		return ((AdmissionControllerI)this.connector).submitApplication(rgNotificationInboundPort, notificationMethodeName, nbVm);
	}
}
