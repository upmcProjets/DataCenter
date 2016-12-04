/**
 * 
 */
package fr.upmc.datacenterclient.basic_admissionController.components;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerServicesI;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;
import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.upmc.datacenterclient.basic_admissionController.interfaces.AdmissionControllerI;
import fr.upmc.datacenterclient.basic_admissionController.ports.AdmissionControllerInboundPort;
import fr.upmc.datacenterclient.requestDispatcher.components.RequestDispatcher;
import fr.upmc.datacenterclient.requestDispatcher.connectors.RequestDispatcherManagerConnector;
import fr.upmc.datacenterclient.requestDispatcher.ports.RequestDispatcherManagerOutboundPort;


/**
 * La classe <code>DynamicAdmissionController</code> est le composant qui permet de
 *  connecter les generateur de requettes au controlleur d'admission
 *
 * <p><strong>Description</strong></p>
 *
 * Le <code>RequestGenerator</code> qui arrive demande l'execution sur un nombre de
 * machines virtuelles , l'<code>DynamicAdmissionController</code> cree un <code>RequestDispatcher</code>
 * et alloue les machines virtuelles,puis il se connecte au <code>Computer</code> puis allouer les cores
 * aux machine virtuelles  
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 *  
 * @author zahir CHELBI, hacene KEDJAR, lyes CHELFI
 */
public class AdmissionController 
extends AbstractComponent 
implements ComputerServicesI {

	protected final String 								AdmissionControllerURI ;
	public static int 									vmNum 	= 0 ;
	public static int 									repNum	= 0 ;

	/** uri des ports de la vm */
	public static final String	PrefixRequestSubmissionInboundPortURI 			= "rsibp" ;
	public static final String	PrefixRequestNotificationOutboundPortURI 		= "rnobp" ;
	public static final String	PrefixApplicationVMManagementInboundPortURI	 	= "avm-ibp"; 
	public static final String	PrefixApplicationVMManagementOutboundPortURI	= "avm-obp" ;

	/** uri des port du RequestDispatcher */
	public static final String	RepartiteurSubmissionInboundPortURI 			= "rpsibp" ;
	public static final String	RepartiteurSubmissionOutboundPortURI 			= "rpsobp" ;	
	public static final String	RepartiteurNotificationOutboundPortURI 			= "rpnobp" ;
	public static final String	RepartiteurNotificationInboundPortURI 			= "rpnibp" ;
	public static final String	RepartiteurRequestManagementInboundPortURI 		= "rprmip" ;
	public static final String	RepartiteurRequestManagementOutboundPortURI 	= "rprmop" ;

	/**  Les Port du controlleur  */
	protected ComputerServicesOutboundPort 				accsobp ;
	protected AdmissionControllerInboundPort			acibp ;

	/**
	 * cree le composant <code>DynamicAdmissionController</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	controllerURI != null and
 			computerServiceOutboundPortURI    != null and
			admissionControllerInboundPortURI != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param controllerURI						    l'uri du composant DynamicAdmissionController
	 * @param computerServiceOutboundPortURI 		l'uri du port outboundPort pour la connexion au computer
	 * @param admissionControllerInboundPortURI 	l'uri du port de management inboundPort  de l'DynamicAdmissionController
	 * @throws Exception                            Exception
	 */
	public AdmissionController(
			String controllerURI,
			String computerServiceOutboundPortURI,
			String admissionControllerInboundPortURI
			) throws Exception {
		super(1, 1);

		assert  controllerURI != null && 
				computerServiceOutboundPortURI != null && 
				admissionControllerInboundPortURI != null ;

		this.AdmissionControllerURI = controllerURI ;

		/** port de connexion avec le computer */
		this.addRequiredInterface(ComputerServicesI.class);
		this.accsobp = new ComputerServicesOutboundPort(computerServiceOutboundPortURI, this);
		this.addPort(accsobp);
		this.accsobp.publishPort();

		/** port management du controlleur d'admission */
		this.addOfferedInterface(AdmissionControllerI.class);
		this.acibp = new AdmissionControllerInboundPort(admissionControllerInboundPortURI, this);
		this.addPort(acibp);
		this.acibp.publishPort();

		assert this.accsobp != null && this.acibp != null ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.computers.interfaces.ComputerServicesI#allocateCore()
	 */
	@Override
	public AllocatedCore allocateCore() throws Exception {
		return accsobp.allocateCore();	
	}

	/**
	 * @see fr.upmc.datacenter.hardware.computers.interfaces.ComputerServicesI#allocateCores(int)
	 */
	@Override
	public AllocatedCore[] allocateCores(int numberRequested) throws Exception {
		return  accsobp.allocateCores(numberRequested);	
	}

	/**
	 * @see fr.upmc.components.AbstractComponent#shutdown()
	 */
	@Override
	public void shutdown() throws ComponentShutdownException {
		try {
			this.accsobp.doDisconnection();		
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.shutdown();
	}


	// -------------------------------------------------------------------------
	// Component internal services
	// -------------------------------------------------------------------------
	/**
	 * la methode submitApplication est une methode interne au composant qui reçois en
	 * paramètre un URI de notification du RequestGenerator et le nombre de machines virtuelles,
	 * et il cree un RequestDispatcher et les machines virtuelles nécessaires puis il connecte 
	 * tout les composants et return le port de soumission du RequestDispacher pou permettre 
	 * au RequestGenerateur de connecter son pour de soumission
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre  rgNotificationInboundPort != null and nbrVm > 0
	 * post	return !=null
	 * </pre>
	 * @param rgNotificationInboundPort  	L'URI de NotificationInboundPort du RequestGenerator
	 * @param nbrVm							nombre de machine virtuelle à allouer pour l'application
	 * @return								L'URI de submissionInboundPort du RequestDispatcher
	 * @throws Exception					Exception
	 */	
	public String submitApplication(String rgNotificationInboundPort, int nbrVm)throws Exception {
		assert rgNotificationInboundPort != null && nbrVm > 0;

		/** création du RequestDispatcher */
		RequestDispatcher rqd = new RequestDispatcher("rep"+repNum,
				RepartiteurSubmissionInboundPortURI+repNum,
				RepartiteurNotificationInboundPortURI+repNum,
				RepartiteurSubmissionOutboundPortURI+repNum,
				RepartiteurNotificationOutboundPortURI+repNum,
				RepartiteurRequestManagementInboundPortURI+repNum);
		rqd.toggleLogging();
		rqd.toggleTracing();
		rqd.start();

		/** Création du port manager du RequestDispatcher */
		RequestDispatcherManagerOutboundPort rdmobp = new RequestDispatcherManagerOutboundPort(
				RepartiteurRequestManagementOutboundPortURI+repNum,new AbstractComponent() {});
		rdmobp.publishPort();
		rdmobp.doConnection(
				RepartiteurRequestManagementInboundPortURI+repNum, 
				RequestDispatcherManagerConnector.class.getCanonicalName());

		/** connexion avec le Request generator */
		RequestNotificationOutboundPort rdnobp = (RequestNotificationOutboundPort) rqd.
				findPortFromURI(RepartiteurNotificationOutboundPortURI+repNum);
		rdnobp.doConnection(
				rgNotificationInboundPort, RequestNotificationConnector.class.getCanonicalName());

		/** Creation de la machine virtuelle  */
		for(int i=0;i<nbrVm;i++){

			ApplicationVM vm =new ApplicationVM("vm"+i+vmNum,
					PrefixApplicationVMManagementInboundPortURI+i+repNum,
					PrefixRequestSubmissionInboundPortURI+i+repNum,
					PrefixRequestNotificationOutboundPortURI+i+repNum) ;
			vm.toggleTracing() ;
			vm.toggleLogging() ;
			vm.start();
			//creation du port Manager de la vm
			ApplicationVMManagementOutboundPort avmPort = new ApplicationVMManagementOutboundPort(
					PrefixApplicationVMManagementOutboundPortURI+i+repNum,
					new AbstractComponent() {}) ;
			avmPort.localPublishPort(); ;

			avmPort.doConnection(PrefixApplicationVMManagementInboundPortURI+i+repNum,
					ApplicationVMManagementConnector.class.getCanonicalName()) ;
			avmPort.allocateCores(allocateCores(2));
			// connexion avec les vm		
			String inboundPortDispatcher = rdmobp.ajouterVM(
					PrefixRequestSubmissionInboundPortURI+i+repNum);
			RequestNotificationOutboundPort rpnop =(RequestNotificationOutboundPort) vm.
					findPortFromURI(PrefixRequestNotificationOutboundPortURI+i+repNum) ;
			rpnop.doConnection(inboundPortDispatcher,RequestNotificationConnector.class.getCanonicalName()) ;
		}
		repNum = repNum+1 ;
		vmNum = vmNum+nbrVm ;

		return RepartiteurSubmissionInboundPortURI+(repNum-1) ;

	}

}
