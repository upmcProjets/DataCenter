/**
 * 
 */
package fr.upmc.datacenterclient.requestDispatcher;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.cvm.AbstractDistributedCVM;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;
import fr.upmc.datacenterclient.requestDispatcher.components.RequestDispatcher;
import fr.upmc.datacenterclient.requestDispatcher.connectors.RequestDispatcherManagerConnector;
import fr.upmc.datacenterclient.requestDispatcher.ports.RequestDispatcherManagerOutboundPort;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;
import fr.upmc.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.upmc.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;

/**
 * @authors zahir CHELBI, hacene KEDJAR, lyes CHELFI
 *
 */
public class TestDistributedDispatcher 
extends AbstractDistributedCVM {

	// URI of the CVM instances as defined in the config.xml file
	protected static String		DISPATCHER_JVM_URI = "dispatcher" ;
	protected static String		GENERATOR_JVM_URI  = "generator" ;


	public static final String	ComputerServicesInboundPortURI 				= "cs-ibp" ;
	public static final String	ComputerServicesOutboundPortURI 			= "cs-obp" ;
	public static final String	ComputerStaticStateDataInboundPortURI 		= "css-dip" ;
	public static final String	ComputerDynamicStateDataInboundPortURI 		= "cds-dip" ;

	/**		 uri des ports du dispatcher 		*/
	public static final String	RepartiteurSubmissionInboundPortURI 		= "rpsibp" ;
	public static final String	RepartiteurSubmissionOutboundPortURI 		= "rpsobp" ;
	public static final String  RepartiteurNotificationInboundPortURI		= "rpnip";
	public static final String	RepartiteurNotificationOutboundPortURI 		= "rpnobp" ;
	public static final String	RepartiteurRequestManagementInboundPortURI 	= "rprmip" ;
	public static final String	RepartiteurRequestManagementOutboundPortURI = "rprmop" ;

	/**		 uri des ports de la vm 0 		*/
	public static final String	RequestSubmissionInboundPortURI 			= "rsibp1" ;
	public static final String	RequestNotificationOutboundPortURI 			= "rnobp1" ;
	public static final String	ApplicationVMManagementInboundPortURI 		= "avm-ibp1" ;
	public static final String	ApplicationVMManagementOutboundPortURI 		= "avm-obp1" ;

	/**		 uri des ports de la vm 0 		*/
	public static final String	RequestSubmissionOutboundPortURI 			= "rsobp" ;
	public static final String	RequestNotificationInboundPortURI 			= "rnibp" ;
	public static final String	RequestGeneratorManagementInboundPortURI 	= "rgmip" ;
	public static final String	RequestGeneratorManagementOutboundPortURI 	= "rgmop" ;


	protected ApplicationVMManagementOutboundPort		avmPort1 ;
	protected ComputerServicesOutboundPort				csPort ;
	protected RequestDispatcherManagerOutboundPort 		rprmop ;
	protected RequestSubmissionOutboundPort				rsobp ;
	protected RequestNotificationOutboundPort			nobp ;
	protected RequestGeneratorManagementOutboundPort	rgmop ;

	protected ApplicationVM								mv;	
	protected RequestDispatcher 						rep;
	protected RequestGenerator							rg;


	public TestDistributedDispatcher(String[] args) throws Exception {
		super(args);

	}

	@Override
	public void			initialise() throws Exception
	{
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

	@Override
	public void			instantiateAndPublish() throws Exception
	{
		if(thisJVMURI.equals(DISPATCHER_JVM_URI)){

			String computerURI = "computer0" ;
			int numberOfProcessors = 4 ;
			int numberOfCores = 4 ;
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
			this.deployedComponents.add(c);

			// instanciation de la vm 1
			this.mv =new ApplicationVM("vm",	
					ApplicationVMManagementInboundPortURI,
					RequestSubmissionInboundPortURI,
					RequestNotificationOutboundPortURI) ;
			this.deployedComponents.add(mv);
			mv.toggleTracing() ;
			mv.toggleLogging() ;


			// instanciation du RequestDispatcher
			this.rep = 
					new RequestDispatcher("rep",
							RepartiteurSubmissionInboundPortURI,
							RepartiteurNotificationInboundPortURI,
							RepartiteurSubmissionOutboundPortURI,
							RepartiteurNotificationOutboundPortURI,
							"gffdgd",
							RepartiteurRequestManagementInboundPortURI,"rdddip");
			this.deployedComponents.add(rep);		
			rep.toggleLogging();
			rep.toggleTracing();


		}else if(thisJVMURI.equals(GENERATOR_JVM_URI)){

			// Creating the request generator component.
			this.rg =
					new RequestGenerator(
							"rg",  500.0  , 6000000000L,	
							RequestGeneratorManagementInboundPortURI,
							RequestSubmissionOutboundPortURI,
							RequestNotificationInboundPortURI) ;
			this.deployedComponents.add(rg) ;
			rg.toggleTracing() ;
			rg.toggleLogging() ;

		}else{
			System.out.println("Unknown JVM URI... " + thisJVMURI) ;
		}
		super.instantiateAndPublish();
	}

	@Override
	public void			interconnect() throws Exception
	{
		if(thisJVMURI.equals(DISPATCHER_JVM_URI)){

			// Create a mock-up computer services port to later allocate its cores
			// to the application virtual machine.
			this.csPort = new ComputerServicesOutboundPort(
					ComputerServicesOutboundPortURI,
					new AbstractComponent() {}) ;
			this.csPort.publishPort() ;
			this.csPort.doConnection(
					ComputerServicesInboundPortURI,
					ComputerServicesConnector.class.getCanonicalName()) ;

			//apvm
			this.avmPort1 = new ApplicationVMManagementOutboundPort(
					ApplicationVMManagementOutboundPortURI,
					new AbstractComponent() {}) ;
			this.avmPort1.publishPort() ;
			this.avmPort1.
			doConnection(
					ApplicationVMManagementInboundPortURI,
					ApplicationVMManagementConnector.class.getCanonicalName()) ;

			//dispatcher

			this.nobp =(RequestNotificationOutboundPort) rep.
					findPortFromURI(RepartiteurNotificationOutboundPortURI) ;
			nobp.doConnection(
					RequestNotificationInboundPortURI,
					RequestNotificationConnector.class.getCanonicalName()) ;


		}else if(thisJVMURI.equals(GENERATOR_JVM_URI)){
			this.rsobp =(RequestSubmissionOutboundPort) rg.
					findPortFromURI(RequestSubmissionOutboundPortURI) ;
			rsobp.doConnection(
					RepartiteurSubmissionInboundPortURI,
					RequestSubmissionConnector.class.getCanonicalName()) ;

			this.rgmop = new RequestGeneratorManagementOutboundPort(
					RequestGeneratorManagementOutboundPortURI,
					new AbstractComponent() {}) ;
			this.rgmop.publishPort() ;
			this.rgmop.doConnection(
					RequestGeneratorManagementInboundPortURI,
					RequestGeneratorManagementConnector.class.getCanonicalName()) ;
			this.rprmop= new RequestDispatcherManagerOutboundPort(
					RepartiteurRequestManagementOutboundPortURI,
					new AbstractComponent() {});
			this.rprmop.publishPort();
			this.rprmop.doConnection(
					RepartiteurRequestManagementInboundPortURI,
					RequestDispatcherManagerConnector.class.getCanonicalName());


		}else{
			System.out.println("Unknown JVM URI... " + thisJVMURI) ;
		}
		super.interconnect();
	}

	public void	testScenario() throws Exception
	{
		// start the request generation in the request generator.
		this.rgmop.startGeneration() ;
		// wait 20 seconds
		Thread.sleep(2000L) ;
		// then stop the generation.
		this.rgmop.stopGeneration() ;
	}

	@Override
	public void start() throws Exception {
		if(thisJVMURI.equals(DISPATCHER_JVM_URI)){
			AllocatedCore[] ac = this.csPort.allocateCores(4) ;
			this.avmPort1.allocateCores(ac);
			String inBoundPortDispatcher1 = rprmop.ajouterVM(RequestSubmissionInboundPortURI);
			RequestNotificationOutboundPort rpnop1 =(RequestNotificationOutboundPort) 
					mv.findPortFromURI(RequestNotificationOutboundPortURI) ;
			rpnop1.doConnection(
					inBoundPortDispatcher1,
					RequestNotificationConnector.class.getCanonicalName()) ;

		} else if (thisJVMURI.equals(GENERATOR_JVM_URI)) {

		}else {
			System.out.println("Unknown JVM URI... " + thisJVMURI) ;
		}
		super.start();
	}

	@Override
	public void			shutdown() throws Exception
	{
		if (thisJVMURI.equals(DISPATCHER_JVM_URI)) {
			this.avmPort1.doDisconnection();
			this.csPort.doDisconnection() ;
			this.nobp.doDisconnection() ;

		} else if (thisJVMURI.equals(GENERATOR_JVM_URI)) {
			this.rsobp.doDisconnection() ;
			this.rgmop.doDisconnection() ;
			this.rprmop.doDisconnection();

		} else {

			System.out.println("Unknown JVM URI... " + thisJVMURI) ;

		}

		super.shutdown();
	}

	public static void	main(String[] args)
	{
		try {
			final TestDistributedDispatcher da = new TestDistributedDispatcher(args) ;
			da.deploy() ;
			System.out.println("starting...") ;
			da.start() ;
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						da.testScenario() ;
					} catch (Exception e) {
						throw new RuntimeException(e) ;
					}
				}
			}).start() ;
			Thread.sleep(15000L) ;
			System.out.println("shutting down...") ;
			da.shutdown() ;
			System.out.println("ending...") ;
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
