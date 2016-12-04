
package fr.upmc.datacenterclient.basic_admissionController;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.connectors.DataConnector;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.cvm.AbstractDistributedCVM;
import fr.upmc.datacenter.connectors.ControlledDataConnector;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.upmc.datacenter.hardware.computers.ports.ComputerDynamicStateDataOutboundPort;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.hardware.computers.ports.ComputerStaticStateDataOutboundPort;
import fr.upmc.datacenter.hardware.processors.Processor;
import fr.upmc.datacenter.hardware.tests.ComputerMonitor;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;
import fr.upmc.datacenterclient.basic_admissionController.components.AdmissionController;
import fr.upmc.datacenterclient.basic_admissionController.connectors.AdmissionControllerConnector;
import fr.upmc.datacenterclient.basic_admissionController.ports.AdmissionControllerOutboundPort;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;
import fr.upmc.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.upmc.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;

/**
 * 
 * @authors zahir CHELBI, hacene KEDJAR, lyes CHELFI
 *
 */
public class TestDistributedAdmisionController 
extends AbstractDistributedCVM {

	// URI of the CVM instances as defined in the config.xml file
	protected static String		CONTROLLER_JVM_URI = "controller" ;
	protected static String		GENERATOR_JVM_URI  = "generator" ;


	public static final String	ComputerServicesInboundPortURI = "cs-ibp" ;
	public static final String	ComputerServicesOutboundPortURI = "cs-obp" ;
	public static final String	ComputerStaticStateDataInboundPortURI = "css-dip" ;
	public static final String	ComputerStaticStateDataOutboundPortURI = "css-dop" ;
	public static final String	ComputerDynamicStateDataInboundPortURI = "cds-dip" ;
	public static final String	ComputerDynamicStateDataOutboundPortURI = "cds-dop" ;

	public static final String  ControllerServiceOutboundPortURI   = "csop";
	public static final String  controllerInboundPortURI           = "ctrcippp" ;
	public static final String  controllerOutboundPortURI          = "ctrcotp";

	public static final String	RequestSubmissionInboundPortURI    = "rsibp" ;
	public static final String	RequestSubmissionOutboundPortURI   = "rsobp" ;
	public static final String	RequestNotificationInboundPortURI  = "rnibp" ;
	public static final String	RequestNotificationOutboundPortURI = "rnobp" ;
	public static final String	RequestGeneratorManagementInboundPortURI = "rgmip" ;
	public static final String	RequestGeneratorManagementOutboundPortURI = "rgmop" ;

	protected String  repartiteurSubmissionInboundP ;

	protected ComputerServicesOutboundPort				csPort ;
	protected ComputerStaticStateDataOutboundPort		cssPort ;												
	protected ComputerDynamicStateDataOutboundPort		cdsPort ;
	protected AdmissionControllerOutboundPort			cop ;

	protected RequestGeneratorManagementOutboundPort 	rgmop;
	protected RequestSubmissionOutboundPort 			rsobp;

	protected RequestGeneratorManagementOutboundPort 	rgmop1;
	protected RequestSubmissionOutboundPort 			rsobp1;
	protected RequestGeneratorManagementOutboundPort 	rgmop2;
	protected RequestSubmissionOutboundPort 			rsobp2;
	private RequestGenerator rg;
	private ComputerMonitor cm;
	private AdmissionController controller;


	public TestDistributedAdmisionController(String[] args) throws Exception {
		super(args);
	}

	/**
	 * @see fr.upmc.components.cvm.AbstractDistributedCVM#initialise()
	 */
	@Override
	public void initialise() throws Exception {
		super.initialise() ;
		// any other application-specific initialisation must be put here

		// logging configuration putting log files in the user current directory
		AbstractComponent.configureLogging(
				System.getProperty("user.dir"),		// directory for log files
				"log",								// log files name extension
				4000,								// initial buffer size for logs
				'|') ;								// character separator between
		// time stamps and log messages
		// debugging mode configuration; comment and uncomment the line to see
		// the difference
		AbstractCVM.toggleDebugMode() ;
	}

	/**
	 * @see fr.upmc.components.cvm.AbstractDistributedCVM#instantiateAndPublish()
	 */
	@Override
	public void instantiateAndPublish() throws Exception {
		if(thisJVMURI.equals(CONTROLLER_JVM_URI)){

			Processor.DEBUG = true ;

			String computerURI = "computer0" ;
			int numberOfProcessors = 8 ;
			int numberOfCores = 2 ;
			Set<Integer> admissibleFrequencies = new HashSet<Integer>() ;
			admissibleFrequencies.add(1500) ;	// Cores can run at 1,5 GHz
			admissibleFrequencies.add(3000) ;	// and at 3 GHz
			Map<Integer,Integer> processingPower = new HashMap<Integer,Integer>() ;
			processingPower.put(1500, 1500000) ;	// 1,5 GHz executes 1,5 Mips
			processingPower.put(3000, 3000000) ;	// 3 GHz executes 3 Mips

			Computer c = new Computer(
					computerURI,
					admissibleFrequencies,
					processingPower,  
					1500,		// Test scenario 1, frequency = 1,5 GHz
					// 3000,	// Test scenario 2, frequency = 3 GHz
					1500,		// max frequency gap within a processor
					numberOfProcessors,
					numberOfCores,
					ComputerServicesInboundPortURI,
					ComputerStaticStateDataInboundPortURI,
					ComputerDynamicStateDataInboundPortURI) ;
			this.addDeployedComponent(c) ;

			cm = new ComputerMonitor(computerURI,
					true,
					ComputerStaticStateDataOutboundPortURI,
					ComputerDynamicStateDataOutboundPortURI) ;
			this.addDeployedComponent(cm) ;

			/**   Admission Controller  */
			controller = new AdmissionController(
					"controller",
					ControllerServiceOutboundPortURI,
					controllerInboundPortURI);
			this.addDeployedComponent(controller);
			controller.toggleLogging();
			controller.toggleTracing();

		}else if(thisJVMURI.equals(GENERATOR_JVM_URI)){

			rg =new RequestGenerator(
					"rg",  500.0  , 6000000000L,	
					RequestGeneratorManagementInboundPortURI,
					RequestSubmissionOutboundPortURI,
					RequestNotificationInboundPortURI) ;
			this.addDeployedComponent(rg) ;
			rg.toggleTracing() ;
			rg.toggleLogging() ;

		}else{
			System.out.println("Unknown JVM URI... " + thisJVMURI) ;
		}
		super.instantiateAndPublish();
	}

	/**
	 * @see fr.upmc.components.cvm.AbstractDistributedCVM#interconnect()
	 */
	@Override
	public void interconnect() throws Exception {

		if(thisJVMURI.equals(CONTROLLER_JVM_URI)){
			this.csPort = new ComputerServicesOutboundPort(
					ComputerServicesOutboundPortURI,
					new AbstractComponent() {}) ;
			this.csPort.publishPort() ;
			this.csPort.doConnection(
					ComputerServicesInboundPortURI,
					ComputerServicesConnector.class.getCanonicalName()) ;

			this.cssPort =
					(ComputerStaticStateDataOutboundPort)
					cm.findPortFromURI(ComputerStaticStateDataOutboundPortURI) ;
			this.cssPort.doConnection(
					ComputerStaticStateDataInboundPortURI,
					DataConnector.class.getCanonicalName()) ;

			this.cdsPort =
					(ComputerDynamicStateDataOutboundPort)
					cm.findPortFromURI(ComputerDynamicStateDataOutboundPortURI) ;
			this.cdsPort.
			doConnection(
					ComputerDynamicStateDataInboundPortURI,
					ControlledDataConnector.class.getCanonicalName()) ;

			ComputerServicesOutboundPort csop =(ComputerServicesOutboundPort)
					controller.findPortFromURI(ControllerServiceOutboundPortURI);
			csop.doConnection(
					ComputerServicesInboundPortURI, ComputerServicesConnector.class.getCanonicalName());



		}else if(thisJVMURI.equals(GENERATOR_JVM_URI)){
			this.rgmop = new RequestGeneratorManagementOutboundPort(
					RequestGeneratorManagementOutboundPortURI,new AbstractComponent() {}) ;
			this.rgmop.publishPort() ;
			this.rgmop.doConnection(
					RequestGeneratorManagementInboundPortURI,
					RequestGeneratorManagementConnector.class.getCanonicalName()) ;
			this.rsobp =(RequestSubmissionOutboundPort) rg.findPortFromURI(RequestSubmissionOutboundPortURI) ;

			cop =new AdmissionControllerOutboundPort(controllerOutboundPortURI , new AbstractComponent() {});
			cop.publishPort();
			cop.doConnection(controllerInboundPortURI, AdmissionControllerConnector.class.getCanonicalName());


		}else{
			System.out.println("Unknown JVM URI... " + thisJVMURI) ;
		}
		super.interconnect();
	}

	/**
	 * @see fr.upmc.components.cvm.AbstractDistributedCVM#start()
	 */
	@Override
	public void start() throws Exception {
		// TODO Auto-generated method stub
		super.start();
	}

	@Override
	public void shutdown() throws Exception {

		super.shutdown();
	}

	public void			testScenario() throws Exception
	{
		if(thisJVMURI.equals(CONTROLLER_JVM_URI)){


		}else if(thisJVMURI.equals(GENERATOR_JVM_URI)){

			//connexion du RequestGenerator au controlleur d'admission
			repartiteurSubmissionInboundP = cop.submitApplication(RequestNotificationInboundPortURI,2);
			rsobp.doConnection(repartiteurSubmissionInboundP,RequestSubmissionConnector.class.getCanonicalName()) ;

//			repartiteurSubmissionInboundP = cop.submitApplication(RequestNotificationInboundPortURI+1,2);
//			rsobp1.doConnection(repartiteurSubmissionInboundP,RequestSubmissionConnector.class.getCanonicalName()) ;
//
//			repartiteurSubmissionInboundP = cop.submitApplication(RequestNotificationInboundPortURI+2,2);
//			rsobp2.doConnection(repartiteurSubmissionInboundP,RequestSubmissionConnector.class.getCanonicalName()) ;

			// start the request generation in the request generator.
			this.rgmop.startGeneration() ;
			// wait 20 seconds
			Thread.sleep(2000L) ;
			// then stop the generation.
			this.rgmop.stopGeneration() ;

		}else{
			System.out.println("Unknown JVM URI... " + thisJVMURI) ;
		}
	}

	public static void main(String[] args){

		try {
			final TestDistributedAdmisionController testAdmissionController= new TestDistributedAdmisionController(args) ;
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
