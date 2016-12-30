package fr.upmc.datacenterclient.basic_admissionController.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenterclient.basic_admissionController.components.AdmissionController;
import fr.upmc.datacenterclient.basic_admissionController.interfaces.AdmissionControllerI;

/**
 * The class <code>AdmissionControllerConnector</code> implements a
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
public class AdmissionControllerConnector 
extends AbstractConnector implements 
AdmissionControllerI {

	
	
	/**
	 * @see fr.upmc.datacenterclient.basic_admissionController.interfaces.AdmissionControllerI#submitApplication(java.lang.String, java.lang.String, int)
	 */
	@Override
	public String submitApplication(String rgNotificationInboundPort, String notificationMethodeName, int nbVm) throws Exception {
		return ((AdmissionControllerI)this.offering).submitApplication(rgNotificationInboundPort, notificationMethodeName, nbVm);
	}
}
