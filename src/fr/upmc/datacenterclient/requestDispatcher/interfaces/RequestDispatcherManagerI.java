package fr.upmc.datacenterclient.requestDispatcher.interfaces;
import java.util.List;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

/**
 * La classe <code>RequestDispatcherManagerI</code> définit les methodes
 * qui permet de manager le <code>RequestDispatcher</code>
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
public interface RequestDispatcherManagerI 
extends OfferedI,RequiredI {
	
	/**
	 * connecte les machines virtuelles au Dispatcher
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre  vms != null and vms.lenght > 0
	 * post	return !=null and return.lenght= vms.lenght
	 * </pre>
	 * @param vms  	List<String> des URI SubmissionInboundPort des machine virtuelles
	 * @return		List<String> des URI NotificationInboundPort du RequestDispatcher pour les machines virtuelles
	 * @throws Exception
	 */
	
	public List<String> connectWithVMS(List<String> vms) throws Exception ;
	
	/**
	 * connecte une machine virtuelle au Dispatcher
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre  requestSubmissionOutboundPort != null
	 * post	return !=null
	 * </pre>
	 * @param requestSubmissionInboundPort  	l'URI de SubmissionInboundPort d'une machine virtuelle
	 * @return					l'URI de NotificationInboundPort du RequestDispatcher pour la machine virtuelle
	 * @throws Exception
	 */
	public String ajouterVM(String requestSubmissionInboundPort) throws Exception ;
	
	/**
	 * déconnecte une machine virtuelle du Dispatcher
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre  requestSubmissionOutboundPort != null
	 * post	true // pas de postcondition
	 * </pre>
	 * @param requestSubmissionOutboundPort  	l'URI de SubmissionOutboundPort du RequestDispatcher
	 * @throws Exception
	 */
	public void supprimerVM(String requestSubmissionOutboundPort) throws Exception ;
	
	

}