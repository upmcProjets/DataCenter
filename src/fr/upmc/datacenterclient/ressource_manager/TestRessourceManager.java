package fr.upmc.datacenterclient.ressource_manager;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionI;
import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;
import fr.upmc.datacenterclient.basic_admissionController.components.AdmissionController;
import fr.upmc.datacenterclient.basic_admissionController.connectors.AdmissionControllerConnector;
import fr.upmc.datacenterclient.basic_admissionController.ports.AdmissionControllerOutboundPort;
import fr.upmc.datacenterclient.requestDispatcher.components.RequestDispatcher;
import fr.upmc.datacenterclient.requestDispatcher.ports.RequestDispatcherManagerOutboundPort;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;
import fr.upmc.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.upmc.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;
import fr.upmc.datacenterclient.ressource_manager.ports.RessourceManagerOutboundPort;
import fr.upmc.datacenterclient.utils.JavassistUtils;

import java.util.*;

/**
 * 
 * @authors zahir CHELBI, hacene KEDJAR, lyes CHELFI
 *
 */
@SuppressWarnings("Duplicates")
public class TestRessourceManager extends AbstractCVM {

	private static final int APP_COUNT = 3;
	public static List<String>		           ComputerServicesInboundPortURIs = new ArrayList<>();
	public static List<String>		          ComputerServicesOutboundPortURIs = new ArrayList<>();
	public static List<String>		    ComputerStaticStateDataInboundPortURIs = new ArrayList<>();
	public static List<String>		   ComputerStaticStateDataOutboundPortURIs = new ArrayList<>();
	public static List<String>		   ComputerDynamicStateDataInboundPortURIs = new ArrayList<>();
	public static List<String>		  ComputerDynamicStateDataOutboundPortURIs = new ArrayList<>();
	public static List<String>	  	 							  ComputerURIs = new ArrayList<>();
	public static List<String>     RequestGeneratorNotificationInboundPortURIs = new ArrayList<>();
	public static List<String>         RequestDispatcherManagerInboundPortURIs = new ArrayList<>();
	public static List<String>      RequestDispatcherSubmissionInboundPortURIs = new ArrayList<>();

	private List<RequestGeneratorManagementOutboundPort> rgmops = new ArrayList<>();
	private List<RequestSubmissionOutboundPort> rsops = new ArrayList<>();
	private List<RequestDispatcherManagerOutboundPort> rdmops = new ArrayList<>();
	private List<RequestNotificationOutboundPort> rnops = new ArrayList<>();

	private RessourceManagerOutboundPort rmop;
	private String  ressourceManagerIPUri = "rmip_1";
	private String  ressourceManagerOPUri = "rmop_1";

	public TestRessourceManager() throws Exception {
		super();
	}

	@Override
	public void deploy() throws Exception {
//		DEBUG = true;
//		Processor.DEBUG = true ;
		// --------------------------------------------------------------------
		// Create and deploy a computer component with its 2 processors and
		// each with 2 cores.
		// --------------------------------------------------------------------
		int numberOfProcessors = 8 ;
		int numberOfCores = 2 ;
		Set<Integer> admissibleFrequencies = new HashSet<Integer>() ;
		admissibleFrequencies.add(1500) ;	// Cores can run at 1,5 GHz
		admissibleFrequencies.add(3000) ;	// and at 3 GHz
		Map<Integer,Integer> processingPower = new HashMap<Integer,Integer>() ;
		processingPower.put(1500, 1500000) ;	// 1,5 GHz executes 1,5 Mips
		processingPower.put(3000, 3000000) ;	// 3 GHz executes 3 Mips

		for (int i = 0; i < 5; i++){
			ComputerServicesInboundPortURIs.add("csip_" + i);
			ComputerServicesOutboundPortURIs.add("csop_" + i);
			ComputerStaticStateDataInboundPortURIs.add("cssdip_" + i);
			ComputerStaticStateDataOutboundPortURIs.add("cssdop_" + i);
			ComputerDynamicStateDataInboundPortURIs.add("cdsdip" + i);
			ComputerDynamicStateDataOutboundPortURIs.add("cdsdop" + i);
			ComputerURIs.add("computer_" + i);
			Computer c = new Computer(
					ComputerURIs.get(i),
					admissibleFrequencies,
					processingPower,
					1500,		// Test scenario 1, frequency = 1,5 GHz
					// 3000,	// Test scenario 2, frequency = 3 GHz
					1500,		// max frequency gap within a processor
					numberOfProcessors,
					numberOfCores,
					ComputerServicesInboundPortURIs.get(i),
					ComputerStaticStateDataInboundPortURIs.get(i),
					ComputerDynamicStateDataInboundPortURIs.get(i));
			this.addDeployedComponent(c);
		}
		/* ##################  Ressource Manager     ###########################*/
		RessourceManager manager = new RessourceManager(
										   ComputerURIs,
						ComputerServicesInboundPortURIs,
				ComputerDynamicStateDataInboundPortURIs,
								   ressourceManagerIPUri
		);
		this.addDeployedComponent(manager);
		manager.toggleTracing();
		manager.toggleLogging();
		rmop = new RessourceManagerOutboundPort(ressourceManagerOPUri, new AbstractComponent() {});
		rmop.publishPort();
		rmop.doConnection(ressourceManagerIPUri, RessourceManagerConnector.class.getCanonicalName());

		/* ##################  Request Dispatchers   ###########################*/
		for(int i = 0; i < APP_COUNT; i++){
			RequestDispatcherManagerInboundPortURIs.add("rdmip_" + i);
			RequestDispatcherSubmissionInboundPortURIs.add("rdsip_" + i);
			RequestDispatcher rd = new RequestDispatcher(
					"rep_" + i,
					RequestDispatcherSubmissionInboundPortURIs.get(i),
					"rnip_" + i,
					"rsop_" + i,
					"rnop_" + i,
					RequestDispatcherManagerInboundPortURIs.get(i),
					"dfsd_" + i,
					"dsfsd_" + i);
			rnops.add((RequestNotificationOutboundPort) rd.findPortFromURI("rnop_" + i));
			this.addDeployedComponent(rd);
			rd.toggleLogging();
			rd.toggleTracing();
		}

		/* ##################  Request Generator    ########################### */
		for(int i = 0; i < APP_COUNT; i++){
			RequestGeneratorNotificationInboundPortURIs.add("rgnip_" + i);
			RequestGenerator rg = new RequestGenerator(
							"rg_" + i,  500.0  , 6000000000L,
							"rgmip_" + i,
							"rsop_" + i,
							RequestGeneratorNotificationInboundPortURIs.get(i));
			this.addDeployedComponent(rg) ;
			rg.toggleTracing() ;
			rg.toggleLogging() ;

			rgmops.add(new RequestGeneratorManagementOutboundPort(
					"rgmop_" + i,new AbstractComponent() {}));
			this.rgmops.get(i).publishPort() ;
			this.rgmops.get(i).doConnection(
					"rgmip_" + i,
					RequestGeneratorManagementConnector.class.getCanonicalName()) ;
			this.rsops.add((RequestSubmissionOutboundPort) rg.findPortFromURI("rsop_" + i));
		}

		/* #################### connect RG with RD ################# */
		for (int i = 0; i < APP_COUNT; i++){
			rsops.get(i).doConnection(RequestDispatcherSubmissionInboundPortURIs.get(i), RequestSubmissionConnector.class.getCanonicalName());
			rnops.get(i).doConnection(RequestGeneratorNotificationInboundPortURIs.get(i), RequestNotificationConnector.class.getCanonicalName());
		}

		super.deploy();
	}

	@Override
	public void shutdown() throws Exception {

		super.shutdown();
	}

	public void			testScenario() throws Exception
	{

		Thread.sleep(1500);
		for (int i = 0; i < APP_COUNT; i++){
			rmop.createVM(RequestDispatcherManagerInboundPortURIs.get(i), 1 + i);
			rmop.createVM(RequestDispatcherManagerInboundPortURIs.get(i), 1 + i);
			rmop.createVM(RequestDispatcherManagerInboundPortURIs.get(i), 1 + i);
			Thread.sleep(1500);
		}
		Thread.sleep(1500);

		for (RequestGeneratorManagementOutboundPort rgmop : rgmops){
			rgmop.startGeneration();
			rgmop.startGeneration();
			rgmop.startGeneration();

			Thread.sleep(2000L);

			rgmop.stopGeneration() ;
			rgmop.stopGeneration();
			rgmop.stopGeneration();
		}

	}

	public static void main(String[] args){

		try {
			final TestRessourceManager testRessourceManager= new TestRessourceManager() ;
			// Deploy the components
			testRessourceManager.deploy();
			System.out.println("starting...") ;
			// Start them.
			testRessourceManager.start();

			// Execute the chosen request generation test scenario in a
			// separate thread.
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						testRessourceManager.testScenario();
					} catch (Exception e) {
						throw new RuntimeException(e) ;
					}
				}
			}).start() ;
			// Sleep to let the test scenario execute to completion.
			Thread.sleep(90000L) ;
			// Shut down the application.
			System.out.println("shutting down...") ;
			testRessourceManager.shutdown() ;
			System.out.println("ending...") ;
			// Exit from Java.
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
