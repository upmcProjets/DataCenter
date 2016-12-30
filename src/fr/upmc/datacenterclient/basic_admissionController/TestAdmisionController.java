
package fr.upmc.datacenterclient.basic_admissionController;


import java.util.*;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.components.connectors.DataConnector;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.connectors.ControlledDataConnector;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.upmc.datacenter.hardware.computers.ports.ComputerDynamicStateDataOutboundPort;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesInboundPort;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.hardware.computers.ports.ComputerStaticStateDataOutboundPort;
import fr.upmc.datacenter.hardware.processors.Processor;
import fr.upmc.datacenter.hardware.tests.ComputerMonitor;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionI;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;
import fr.upmc.datacenterclient.basic_admissionController.components.AdmissionController;
import fr.upmc.datacenterclient.basic_admissionController.connectors.AdmissionControllerConnector;
import fr.upmc.datacenterclient.basic_admissionController.ports.AdmissionControllerOutboundPort;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;
import fr.upmc.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.upmc.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;
import fr.upmc.datacenterclient.ressource_manager.RessourceManager;
import fr.upmc.datacenterclient.ressource_manager.connectors.RessourceManagerConnector;
import fr.upmc.datacenterclient.ressource_manager.ports.RessourceManagerOutboundPort;
import fr.upmc.datacenterclient.utils.JavassistUtils;
//import sun.security.krb5.internal.TGSRep;

/**
 * 
 * @authors zahir CHELBI, hacene KEDJAR, lyes CHELFI
 *
 */
public class TestAdmisionController extends AbstractCVM {

	public static List<String>	         ComputerServicesInboundPortURIs = new ArrayList<>();
	public static List<String>	        ComputerServicesOutboundPortURIs = new ArrayList<>();
	public static List<String>	  ComputerStaticStateDataInboundPortURIs = new ArrayList<>();
	public static List<String>	 ComputerStaticStateDataOutboundPortURIs = new ArrayList<>();
	public static List<String>	 ComputerDynamicStateDataInboundPortURIs = new ArrayList<>();
	public static List<String>	ComputerDynamicStateDataOutboundPortURIs = new ArrayList<>();
	public static List<String>								ComputerURIs = new ArrayList<>();

	public static List<ComputerServicesOutboundPort> 			   csops = new ArrayList<>();

	private AdmissionControllerOutboundPort							 cop;
	private RessourceManagerOutboundPort							rmop;

	private RequestGeneratorManagementOutboundPort 				   rgmop;
	private RequestSubmissionOutboundPort 						   rsobp;
	private RequestGeneratorManagementOutboundPort 				  rgmop1;
	private RequestSubmissionOutboundPort 			              rsobp1;
	private RequestGeneratorManagementOutboundPort 	              rgmop2;
	private RequestSubmissionOutboundPort 			              rsobp2;

	private String admissionControllerIPUri = "acip_1", ressourceManagerIPUri = "rmip_1";
	private String admissionControllerOPUri = "acop_1", ressourceManagerOPUri = "rmop_1";

	private String RequestGeneratorManagementInboundPortURI = "rgmip";
	private String RequestSubmissionOutboundPortURI         = "rsopu";
	private String RequestNotificationInboundPortURI		= "rnipu";
	private String RequestGeneratorManagementOutboundPortURI= "rgmop";



	public TestAdmisionController() throws Exception {
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
//			ComputerServicesOutboundPort csop = new ComputerServicesOutboundPort(
//					ComputerServicesOutboundPortURIs.get(i),
//					new AbstractComponent() {});
//			csops.add(csop);
//			csop.publishPort();
//			csop.doConnection(ComputerServicesInboundPortURIs.get(i), ComputerServicesConnector.class.getCanonicalName());

//			 --------------------------------------------------------------------
//			 Create the computer monitor component and connect its to ports
//			 with the computer component.
//			 --------------------------------------------------------------------

//			ComputerMonitor cm =
//					new ComputerMonitor(ComputerURIs.get(i),
//							true,
//							ComputerStaticStateDataOutboundPortURIs.get(i),
//							ComputerDynamicStateDataOutboundPortURIs.get(i)) ;
//			this.addDeployedComponent(cm) ;
//			ComputerStaticStateDataOutboundPort cssPort =
//					(ComputerStaticStateDataOutboundPort)
//							cm.findPortFromURI(ComputerStaticStateDataOutboundPortURIs.get(i));
//
//			cssPort.doConnection(
//					ComputerStaticStateDataInboundPortURIs.get(i),
//					DataConnector.class.getCanonicalName()) ;
//
//			ComputerDynamicStateDataOutboundPort cdsPort =
//					(ComputerDynamicStateDataOutboundPort)
//							cm.findPortFromURI(ComputerDynamicStateDataOutboundPortURIs.get(i)) ;
//			cdsPort.
//					doConnection(
//							ComputerDynamicStateDataInboundPortURIs.get(i),
//							ControlledDataConnector.class.getCanonicalName()) ;
		}
		/** ##################  Ressource Manager     ###########################*/
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

		/** ##################  Admission Controller  ###########################*/
		AdmissionController controller = new AdmissionController(
				"controller",
				ressourceManagerIPUri,
				admissionControllerIPUri);
		this.addDeployedComponent(controller);
		controller.toggleLogging();
		controller.toggleTracing();

		cop = new AdmissionControllerOutboundPort(admissionControllerOPUri, new AbstractComponent() {});
		cop.publishPort();
		cop.doConnection(admissionControllerIPUri, AdmissionControllerConnector.class.getCanonicalName());


		/** ##################  Request Generator    ########################### */
		RequestGenerator rg =
				new RequestGenerator(
						"rg",  500.0  , 6000000000L,
						RequestGeneratorManagementInboundPortURI,
						RequestSubmissionOutboundPortURI,
						RequestNotificationInboundPortURI) ;
		this.addDeployedComponent(rg) ;
		rg.toggleTracing() ;
		rg.toggleLogging() ;

		this.rgmop = new RequestGeneratorManagementOutboundPort(
				RequestGeneratorManagementOutboundPortURI,new AbstractComponent() {}) ;
		this.rgmop.publishPort() ;
		this.rgmop.doConnection(
				RequestGeneratorManagementInboundPortURI,
				RequestGeneratorManagementConnector.class.getCanonicalName()) ;
		this.rsobp =(RequestSubmissionOutboundPort) rg.findPortFromURI(RequestSubmissionOutboundPortURI) ;


		/** ##################  Request Generator    ########################### */
		RequestGenerator rg1 =
				new RequestGenerator(
						"rg1",  500.0  , 6000000000L,
						RequestGeneratorManagementInboundPortURI+1,
						RequestSubmissionOutboundPortURI+1,
						RequestNotificationInboundPortURI+1) ;
		this.addDeployedComponent(rg1) ;
		rg1.toggleTracing() ;
		rg1.toggleLogging() ;

		this.rgmop1 = new RequestGeneratorManagementOutboundPort
				(RequestGeneratorManagementOutboundPortURI+1,new AbstractComponent() {}) ;
		this.rgmop1.publishPort() ;
		this.rgmop1.doConnection(
				RequestGeneratorManagementInboundPortURI+1,
				RequestGeneratorManagementConnector.class.getCanonicalName()) ;

		this.rsobp1 =(RequestSubmissionOutboundPort)
				rg1.findPortFromURI(RequestSubmissionOutboundPortURI+1) ;

		/** ##################  Request Generator    ########################### */
		RequestGenerator rg2 =
				new RequestGenerator(
						"rg1",  500.0  , 6000000000L,
						RequestGeneratorManagementInboundPortURI+2,
						RequestSubmissionOutboundPortURI+2,
						RequestNotificationInboundPortURI+2) ;
		this.addDeployedComponent(rg2) ;
		rg2.toggleTracing() ;
		rg2.toggleLogging() ;

		this.rgmop2 = new RequestGeneratorManagementOutboundPort
				(RequestGeneratorManagementOutboundPortURI+2,new AbstractComponent() {}) ;
		this.rgmop2.publishPort() ;
		this.rgmop2.doConnection(
				RequestGeneratorManagementInboundPortURI+2,
				RequestGeneratorManagementConnector.class.getCanonicalName()) ;

		this.rsobp2 =(RequestSubmissionOutboundPort)
				rg2.findPortFromURI(RequestSubmissionOutboundPortURI+2) ;
		
		super.deploy();
	}

	@Override
	public void shutdown() throws Exception {

		super.shutdown();
	}

	public void			testScenario() throws Exception
	{
		Thread.sleep(1500);
		String repartiteurSubmissionInboundP;
		try {
			repartiteurSubmissionInboundP = cop.submitApplication(RequestNotificationInboundPortURI, "notifyRequestTermination",2);
			HashMap<String, String> methodesNameMap1 = new HashMap<>();
			methodesNameMap1.put("submitRequest", "submitRequest");
			methodesNameMap1.put("submitRequestAndNotify", "submitRequestAndNotify");
			Class<?> notificationConnector1 = JavassistUtils.makeConnectorClassJavassist(
					"fr.upmc.datacenterclient.basic_admissionController.GeneratedSubmissionConnector1",
					AbstractConnector.class,
					RequestSubmissionI.class,
					RequestSubmissionI.class,
					methodesNameMap1
			);
			rsobp.doConnection(repartiteurSubmissionInboundP, notificationConnector1.getCanonicalName());
			Thread.sleep(1500);
		}catch (Exception e){
			e.printStackTrace();
			System.out.println(e.getMessage());
		}

//		//connexion du RequestGenerator au controlleur d'admission
//		try {
//			repartiteurSubmissionInboundP = cop.submitApplication(RequestNotificationInboundPortURI+1, "notifyRequestTermination",2);
//			HashMap<String, String> methodesNameMap2 = new HashMap<>();
//			methodesNameMap2.put("submitRequest", "submitRequest");
//			methodesNameMap2.put("submitRequestAndNotify", "submitRequestAndNotify");
//			Class<?> notificationConnector2 = JavassistUtils.makeConnectorClassJavassist(
//                    "fr.upmc.datacenterclient.basic_admissionController.GeneratedSubmissionConnector2",
//                    AbstractConnector.class,
//                    RequestSubmissionI.class,
//                    RequestSubmissionI.class,
//                    methodesNameMap2
//            );
//			rsobp.doConnection(repartiteurSubmissionInboundP, notificationConnector2.getCanonicalName());
//			Thread.sleep(1500);
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.out.println(e.getMessage());
//		}
//		try {
//			repartiteurSubmissionInboundP = cop.submitApplication(RequestNotificationInboundPortURI+2, "notifyRequestTermination",2);
//			HashMap<String, String> methodesNameMap3 = new HashMap<>();
//			methodesNameMap3.put("submitRequest", "submitRequest");
//			methodesNameMap3.put("submitRequestAndNotify", "submitRequestAndNotify");
//			Class<?> notificationConnector3 = JavassistUtils.makeConnectorClassJavassist(
//                    "fr.upmc.datacenterclient.basic_admissionController.GeneratedSubmissionConnector3",
//                    AbstractConnector.class,
//                    RequestSubmissionI.class,
//                    RequestSubmissionI.class,
//                    methodesNameMap3
//            );
//			rsobp.doConnection(repartiteurSubmissionInboundP, notificationConnector3.getCanonicalName());
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.out.println(e.getMessage());
//		}

		// start the request generation in the request generator.
		this.rgmop.startGeneration() ;
		this.rgmop1.startGeneration();
		this.rgmop2.startGeneration();
		// wait 20 seconds
		Thread.sleep(2000L) ;
		// then stop the generation.
		this.rgmop.stopGeneration() ;
		this.rgmop1.stopGeneration();
		this.rgmop2.stopGeneration();
	}

	public static void main(String[] args){

		try {
			final TestAdmisionController testAdmissionController= new TestAdmisionController() ;
			// Deploy the components
			testAdmissionController.deploy();
			System.out.println("starting...") ;
			// Start them.
			testAdmissionController.start();

			// Execute the chosen request generation test scenario in a
			// separate thread.
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						testAdmissionController.testScenario();
					} catch (Exception e) {
						throw new RuntimeException(e) ;
					}
				}
			}).start() ;
			// Sleep to let the test scenario execute to completion.
			Thread.sleep(90000L) ;
			// Shut down the application.
			System.out.println("shutting down...") ;
			testAdmissionController.shutdown() ;
			System.out.println("ending...") ;
			// Exit from Java.
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

}
