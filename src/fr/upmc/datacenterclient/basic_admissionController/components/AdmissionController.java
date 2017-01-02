/**
 * 
 */
package fr.upmc.datacenterclient.basic_admissionController.components;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.datacenter.software.interfaces.RequestNotificationI;
import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.upmc.datacenterclient.adaptationcontroller.AdaptationController;
import fr.upmc.datacenterclient.basic_admissionController.interfaces.AdmissionControllerI;
import fr.upmc.datacenterclient.basic_admissionController.ports.AdmissionControllerInboundPort;
import fr.upmc.datacenterclient.requestDispatcher.components.RequestDispatcher;
import fr.upmc.datacenterclient.requestDispatcher.interfaces.RequestDispatcherManagerI;
import fr.upmc.datacenterclient.requestDispatcher.ports.RequestDispatcherManagerInboundPort;
import fr.upmc.datacenterclient.ressource_manager.RessourceManagerConnector;
import fr.upmc.datacenterclient.ressource_manager.interfaces.RessourceManagerI;
import fr.upmc.datacenterclient.ressource_manager.ports.RessourceManagerOutboundPort;
import fr.upmc.datacenterclient.utils.JavassistUtils;

import java.util.HashMap;


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
extends AbstractComponent {

	public static final int DEFAULT_CORE_COUNT = 2;
	private final String 								AdmissionControllerURI ;
	private static int 									repNum	= 0 ;

	/** uri des connectors du RequestDispatcher */
	public static final String	RepartiteurSubmissionInboundPortURI 			= "rpsibp" ;
	public static final String	RepartiteurSubmissionOutboundPortURI 			= "rpsobp" ;	
	public static final String	RepartiteurNotificationOutboundPortURI 			= "rpnobp" ;
	public static final String	RepartiteurNotificationInboundPortURI 			= "rpnibp" ;
	public static final String	RepartiteurRequestManagementInboundPortURI 		= "rprmip" ;
	public static final String	RepartiteurRequestManagementOutboundPortURI 	= "rprmop" ;
	public static final String  RequestDispatcherDynamicDataInboundPortURI		= "rdbdip" ;
	public static final String  SensorDynamicDataOutboundPortURI				= "ssddop" ;
	public static final String  ActuatorManagerOutboundPortURI					= "aamopu" ;

	/**  Les Port du controlleur  */
	private AdmissionControllerInboundPort				  acibp;
	private RessourceManagerOutboundPort                   rmop;
	private static int generatedConnectorCount = 0;


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
//	 * @param computerServiceOutboundPortURI 		l'uri du connectors outboundPort pour la connexion au computer
	 * @param admissionControllerInboundPortURI 	l'uri du connectors de management inboundPort  de l'DynamicAdmissionController
	 * @throws Exception                            Exception
	 */
	public AdmissionController(
			String controllerURI,
			String ressourceManagerInboundportUri,
			String admissionControllerInboundPortURI
			) throws Exception {
		super(1, 1);

		assert  controllerURI != null &&
				ressourceManagerInboundportUri != null &&
				admissionControllerInboundPortURI != null ;

		this.addOfferedInterface(AdmissionControllerI.class);
		this.addRequiredInterface(RessourceManagerI.class);
		this.addRequiredInterface(RequestDispatcherManagerI.class);
		this.AdmissionControllerURI = controllerURI ;
		rmop = new RessourceManagerOutboundPort(this);
		this.addPort(rmop);
		rmop.publishPort();
		rmop.doConnection(ressourceManagerInboundportUri, RessourceManagerConnector.class.getCanonicalName());

		assert rmop != null;

		/** connectors management du controlleur d'admission */
		this.acibp = new AdmissionControllerInboundPort(admissionControllerInboundPortURI, this);
		this.addPort(acibp);
		this.acibp.publishPort();

		assert this.rmop != null && this.acibp != null ;
	}

	/**
	 * @see fr.upmc.components.AbstractComponent#shutdown()
	 */
	@Override
	public void shutdown() throws ComponentShutdownException {
		try {
			this.rmop.doDisconnection();
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
	 * tout les composants et return le connectors de soumission du RequestDispacher pou permettre
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
	public String submitApplication(String rgNotificationInboundPort, String notificationMethodeName, int nbrVm)throws Exception {
		assert rgNotificationInboundPort != null && nbrVm > 0;
		if(!rmop.canHandleApplication(nbrVm, DEFAULT_CORE_COUNT))
			throw new Exception("no enough available resources to handle the application");

		/** création du RequestDispatcher */
		RequestDispatcher rqd = new RequestDispatcher("rep"+repNum,
				RepartiteurSubmissionInboundPortURI+repNum,
				RepartiteurNotificationInboundPortURI+repNum,
				RepartiteurSubmissionOutboundPortURI+repNum,
				RepartiteurNotificationOutboundPortURI+repNum,
				RepartiteurRequestManagementInboundPortURI+repNum,"dfsd",
				RequestDispatcherDynamicDataInboundPortURI+repNum);
		rqd.toggleLogging();
		rqd.toggleTracing();
		rqd.start();
		
	

		/* connexion avec le Request generator */
		RequestNotificationOutboundPort rdnobp = (RequestNotificationOutboundPort) rqd.
				findPortFromURI(RepartiteurNotificationOutboundPortURI+repNum);
		/*rdnobp.doConnection( // replacé par javassist
				rgNotificationInboundPort, RequestNotificationConnector.class.getCanonicalName());*/

		HashMap<String, String> methodesNameMap = new HashMap<>();
		methodesNameMap.put(notificationMethodeName, "notifyRequestTermination");
		Class<?> notificationConnector = JavassistUtils.makeConnectorClassJavassist(
				"fr.upmc.datacenterclient.basic_admissionController.GeneratedNotificationConnector" + generatedConnectorCount++,
				AbstractConnector.class,
				RequestNotificationI.class,
				RequestNotificationI.class,
				methodesNameMap
		);
		rdnobp.doConnection(rgNotificationInboundPort, notificationConnector.getCanonicalName());

		/** Creation de la machine virtuelle  */
		for(int i = 0; i < nbrVm; i++){
			rmop.createVM(RepartiteurRequestManagementInboundPortURI+repNum, DEFAULT_CORE_COUNT);
		}
		
		
		// un port pour cennecter l actuator
		RequestDispatcherManagerInboundPort rdmipA = new RequestDispatcherManagerInboundPort(rqd);
		rdmipA.publishPort();
		
		// uri du port ressource manager pour l actuator
		String ressourceManagerPortTOactuator = this.rmop.createServicePort();
		
		AdaptationController ac = new AdaptationController(
				true,
				"adapControllerURI",
				SensorDynamicDataOutboundPortURI,
				ActuatorManagerOutboundPortURI,
				ressourceManagerPortTOactuator,
				rdmipA.getPortURI(),
				RequestDispatcherDynamicDataInboundPortURI+repNum);
		ac.toggleLogging();
		ac.toggleTracing();
		ac.start();

		
		repNum = repNum+1;
		return RepartiteurSubmissionInboundPortURI + (repNum - 1) ;
	}
	
	
}
