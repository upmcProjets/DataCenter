package fr.upmc.datacenterclient.basic_admissionController.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

/**
 * La classe <code>AdmissionControllerI</code> définit les methodes
 * qui permet de manager le composant <code>DynamicAdmissionController</code>
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

public interface AdmissionControllerI 
extends OfferedI, RequiredI 
{
	
	// return l URI du connectors de connection
	
	/**
	 * connecte le connectors de notification du<code>RequestGenerator</code> au
	 *  composant <code>RequestDispatcher</code> et retourne l'uri du connectors de soumission
	 *  de ce dernier . 
	 * 
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre  rgNotificationInboundPort != null and nbVm > 0
	 * post	return !=null
	 * </pre>
	 * @param rgNotificationInboundPort  	L'URI de NotificationInboundPort du RequestGenerator
	 * @param nbVm							nombre de machine virtuelle à allouer pour l'application
	 * @return								L'URI de submissionInboundPort du RequestDispatcher
	 * @throws Exception
	 */
	String submitApplication(String rgNotificationInboundPort, String notificationMethodeName, int nbVm)
			throws Exception ;
}