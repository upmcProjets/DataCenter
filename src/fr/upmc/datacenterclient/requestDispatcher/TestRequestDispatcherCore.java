/**
 * 
 */
package fr.upmc.datacenterclient.requestDispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.hardware.processors.Processor;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorDynamicStateI;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorStateDataConsumerI;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorStaticStateI;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorDynamicStateDataOutboundPort;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorManagementOutboundPort;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorStaticStateDataOutboundPort;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;
import fr.upmc.datacenterclient.computerActuator.ComputerActuator;
import fr.upmc.datacenterclient.computerActuator.connectors.ComputerActuatorManagerConnector;
import fr.upmc.datacenterclient.computerActuator.ports.ComputerActuatorManagerOutboundPort;
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
public class TestRequestDispatcherCore extends AbstractCVM implements	ProcessorStateDataConsumerI {
	static ArrayList<AllocatedCore[]> allcors = new ArrayList<>();

	public static final String ComputerServicesInboundPortURI = "cs-ibp";
	public static final String ComputerServicesOutboundPortURI = "cs-obp";
	public static final String ComputerStaticStateDataInboundPortURI = "css-dip";
	public static final String ComputerDynamicStateDataInboundPortURI = "cds-dip";

	/** uri des ports du dispatcher */
	public static final String RepartiteurSubmissionInboundPortURI = "rpsibp";
	public static final String RepartiteurSubmissionOutboundPortURI = "rpsobp";
	public static final String RepartiteurNotificationInboundPortURI = "rpnip";
	public static final String RepartiteurNotificationOutboundPortURI = "rpnobp";
	public static final String RepartiteurRequestManagementInboundPortURI = "rprmip";
	public static final String RepartiteurRequestManagementOutboundPortURI = "rprmop";

	/** uri ComputerActuator */
	public static final String ComputerActuatorManagerInboundPortURI  = "camop";
	public static final String ComputerActuatorManagerOutboundPortURI  = "camip";
	
	/** uri des ports de la vm 0 */
	public static final String RequestSubmissionOutboundPortURI = "rsobp";
	public static final String RequestNotificationInboundPortURI = "rnibp";
	public static final String RequestGeneratorManagementInboundPortURI = "rgmip";
	public static final String RequestGeneratorManagementOutboundPortURI = "rgmop";

	/** uri des ports de la vm 1 */
	public static final String RequestSubmissionInboundPortURI1 = "rsibp1";
	public static final String RequestNotificationOutboundPortURI1 = "rnobp1";
	public static final String ApplicationVMManagementInboundPortURI1 = "avm-ibp1";
	public static final String ApplicationVMManagementOutboundPortURI1 = "avm-obp1";

	/** uri des ports de la vm 2 */
	public static final String RequestSubmissionInboundPortURI2 = "rsibp2";
	public static final String RequestNotificationOutboundPortURI2 = "rnobp2";
	public static final String ApplicationVMManagementInboundPortURI2 = "avm-ibp2";
	public static final String ApplicationVMManagementOutboundPortURI2 = "avm-obp2";

	/** uri des ports de la vm 3 */
	public static final String RequestSubmissionInboundPortURI3 = "rsibp3";
	public static final String RequestNotificationOutboundPortURI3 = "rnobp3";
	public static final String ApplicationVMManagementInboundPortURI3 = "avm-ibp3";
	public static final String ApplicationVMManagementOutboundPortURI3 = "avm-obp3";

	/** Port de management des VMs */
	protected ApplicationVMManagementOutboundPort avmPort1;
	protected ApplicationVMManagementOutboundPort avmPort2;
	protected ApplicationVMManagementOutboundPort avmPort3;

	/** Port connected to the computer component to access its services. */
	protected ComputerServicesOutboundPort csPort;
	
	/** Port connected to the processor component to access its services. */
	protected ProcessorStaticStateDataOutboundPort pssPort1;
	protected ProcessorManagementOutboundPort pmPort;
	protected ProcessorDynamicStateDataOutboundPort cdsPort ;
	
	protected RequestDispatcherManagerOutboundPort rprmop;
	
	protected ComputerActuatorManagerOutboundPort camop;

	public static String computerURI;
	/**
	 * Port of the request generator component sending requests to the AVM
	 * component.
	 */
	protected RequestSubmissionOutboundPort rsobp;
	/**
	 * Port of the request generator component used to receive end of execution
	 * notifications from the AVM component.
	 */
	protected RequestNotificationOutboundPort nobp;
	/**
	 * Port connected to the request generator component to manage its execution
	 * (starting and stopping the request generation).
	 */
	protected RequestGeneratorManagementOutboundPort rgmop;

	protected ApplicationVM mv1;
	protected ApplicationVM mv2;
	protected ApplicationVM mv3;

	public TestRequestDispatcherCore() throws Exception {
		super();
	}

	@Override
	public void deploy() throws Exception {
		Processor.DEBUG = true;

		// --------------------------------------------------------------------
		// Create and deploy a computer component with its 2 processors and
		// each with 2 cores.
		// --------------------------------------------------------------------
		computerURI = "computer0";
		int numberOfProcessors =2;
		int numberOfCores = 4;
		Set<Integer> admissibleFrequencies = new HashSet<Integer>();
		admissibleFrequencies.add(1500); // Cores can run at 1,5 GHz
		admissibleFrequencies.add(3000); // and at 3 GHz
		Map<Integer, Integer> processingPower = new HashMap<Integer, Integer>();
		processingPower.put(1500, 1500000); // 1,5 GHz executes 1,5 Mips
		processingPower.put(3000, 3000000); // 3 GHz executes 3 Mips

		Computer c = new Computer(computerURI, admissibleFrequencies, processingPower, 1500, // Test
																								// scenario
																			// 1,
																								// frequency
																								// =
																								// 1,5
																								// GHz
				// 3000, // Test scenario 2, frequency = 3 GHz
				1500, // max frequency gap within a processor
				numberOfProcessors, numberOfCores, ComputerServicesInboundPortURI,
				ComputerStaticStateDataInboundPortURI, ComputerDynamicStateDataInboundPortURI);
		this.addDeployedComponent(c);

		// Create a mock-up computer services port to later allocate its cores
		// to the application virtual machine.
		/*this.csPort = new ComputerServicesOutboundPort(ComputerServicesOutboundPortURI, new AbstractComponent() {
		});
		this.csPort.publishPort();
		this.csPort.doConnection(ComputerServicesInboundPortURI, ComputerServicesConnector.class.getCanonicalName());
*/
		// instanciation de la vm 1
		mv1 = new ApplicationVM("vm1", ApplicationVMManagementInboundPortURI1, RequestSubmissionInboundPortURI1,
				RequestNotificationOutboundPortURI1);
		this.addDeployedComponent(mv1);

//		this.avmPort1 = new ApplicationVMManagementOutboundPort(ApplicationVMManagementOutboundPortURI1,
//				new AbstractComponent() {
//				});
//		this.avmPort1.publishPort();
//		this.avmPort1.doConnection(ApplicationVMManagementInboundPortURI1,
//				ApplicationVMManagementConnector.class.getCanonicalName());
		mv1.toggleTracing();
		mv1.toggleLogging();

		// instanciation de la vm 2
		mv2 = new ApplicationVM("vm2", ApplicationVMManagementInboundPortURI2, RequestSubmissionInboundPortURI2,
				RequestNotificationOutboundPortURI2);
		this.addDeployedComponent(mv2);
		this.avmPort2 = new ApplicationVMManagementOutboundPort(ApplicationVMManagementOutboundPortURI2,
				new AbstractComponent() {
				});
		this.avmPort2.publishPort();
		this.avmPort2.doConnection(ApplicationVMManagementInboundPortURI2,
				ApplicationVMManagementConnector.class.getCanonicalName());
		mv2.toggleTracing();
		mv2.toggleLogging();

		// instanciation de la vm 3
		mv3 = new ApplicationVM("vm3", ApplicationVMManagementInboundPortURI3, RequestSubmissionInboundPortURI3,
				RequestNotificationOutboundPortURI3);
		this.addDeployedComponent(mv3);
		this.avmPort3 = new ApplicationVMManagementOutboundPort(ApplicationVMManagementOutboundPortURI3,
				new AbstractComponent() {
				});
		this.avmPort3.publishPort();
		this.avmPort3.doConnection(ApplicationVMManagementInboundPortURI3,
				ApplicationVMManagementConnector.class.getCanonicalName());
		mv3.toggleTracing();
		mv3.toggleLogging();

		// instanciation du RequestDispatcher
		RequestDispatcher rep = new RequestDispatcher("rep", RepartiteurSubmissionInboundPortURI,
				RepartiteurNotificationInboundPortURI, RepartiteurSubmissionOutboundPortURI,
				RepartiteurNotificationOutboundPortURI, RepartiteurRequestManagementInboundPortURI,"fdgr","dffd");
		this.addDeployedComponent(rep);
		rep.toggleLogging();
		rep.toggleTracing();
		
		
		this.rprmop = new RequestDispatcherManagerOutboundPort(RepartiteurRequestManagementOutboundPortURI,
				new AbstractComponent() {
				});
		this.rprmop.publishPort();
		this.rprmop.doConnection(RepartiteurRequestManagementInboundPortURI,
				RequestDispatcherManagerConnector.class.getCanonicalName());

		
		// Creating the Computer Actuator
		ComputerActuator ca = new ComputerActuator("ca",ComputerActuatorManagerInboundPortURI);
		this.addDeployedComponent(ca);
		ca.toggleTracing();
		ca.toggleLogging();
		
		this.camop = new ComputerActuatorManagerOutboundPort(ComputerActuatorManagerOutboundPortURI,
				new AbstractComponent() {
				});
		this.camop.publishPort();
		this.camop.doConnection(ComputerActuatorManagerInboundPortURI,
				ComputerActuatorManagerConnector.class.getCanonicalName());
		

		
		// Creating the request generator component.
		RequestGenerator rg = new RequestGenerator("rg", 500.0, 6000000000L, RequestGeneratorManagementInboundPortURI,
				RequestSubmissionOutboundPortURI, RequestNotificationInboundPortURI);
		this.addDeployedComponent(rg);
		rg.toggleTracing();
		rg.toggleLogging();
		this.rsobp = (RequestSubmissionOutboundPort) rg.findPortFromURI(RequestSubmissionOutboundPortURI);
		rsobp.doConnection(RepartiteurSubmissionInboundPortURI, RequestSubmissionConnector.class.getCanonicalName());

		this.nobp = (RequestNotificationOutboundPort) rep.findPortFromURI(RepartiteurNotificationOutboundPortURI);
		nobp.doConnection(RequestNotificationInboundPortURI, RequestNotificationConnector.class.getCanonicalName());

		this.rgmop = new RequestGeneratorManagementOutboundPort(RequestGeneratorManagementOutboundPortURI,
				new AbstractComponent() {
				});
		this.rgmop.publishPort();
		this.rgmop.doConnection(RequestGeneratorManagementInboundPortURI,
				RequestGeneratorManagementConnector.class.getCanonicalName());
	
		super.deploy();
	}
/*
	public int getnb_disp_core(AllocatedCore[] cores) {
		int cpt = 0;

		for (int j = 0; j < cores.length; j++)
			if (cores[j] != null)
				cpt++;

		return cpt;
	}
	
	public AllocatedCore[] get_core_disp(AllocatedCore [] cores,int nbcore)
	{
		
	    int cpt = 0;
	    int i = 0;
		AllocatedCore[] ac = new  AllocatedCore[nbcore];
		for (int j = 0; j < cores.length; j++) {
			if (cores[j] != null) {
				cpt++;
				ac[i] = cores[j];
				cores[j] = null;
				i++;
			}
			if (cpt == nbcore)
				break;
		}
			return ac;
	}
		

	public void addCore(ApplicationVMManagementOutboundPort avmPort, int nbcore) throws Exception {
		int max = 0;
		AllocatedCore[] ac = new AllocatedCore[nbcore];
		int i = 0;
		boolean created = false;
		for (AllocatedCore[] cores : allcors) {
			System.out.println("dispo core =" + getnb_disp_core(cores));
			if (getnb_disp_core(cores) >= nbcore) {
			    ac = get_core_disp(cores, nbcore);
				System.out.println(" we create " + nbcore + " cores for you");
				avmPort.allocateCores(ac);
				created = true;
				break;
			}
			else
				
			if (max < getnb_disp_core(cores))
			{
			    max = getnb_disp_core(cores);
			    ac = get_core_disp(cores, nbcore);
			}
			
		}
		if(created == false){
			if (max==0)
				System.out.println(" we can't allocate more cores");
			else{
		    System.out.println(" we can't create this numbres of cores");
			System.out.println(" instead we create " + max + " cores for you");
			AllocatedCore[] ac2 = new AllocatedCore[max];
			int k = 0;
			for (AllocatedCore core : ac)
				if (core != null) {
					ac2[k] = core;
					k++;
				}
			avmPort.allocateCores(ac2);
			}
			}

	}

	public void deleteCore(ApplicationVMManagementOutboundPort avmPort, int nbcore) throws Exception {

		AllocatedCore[] ac = this.csPort.allocateCores(4 - nbcore);
		avmPort.allocateCores(ac);

	}
	
	public void update_frequency(AllocatedCore[] ac)
	{
		
		
	}*/

	@Override
	public void start() throws Exception {
		super.start();

		//AllocatedCore[] ac1 = this.csPort.allocateCores(4);
		//AllocatedCore[] ac = 
		//this.camop.updateFrequency(ComputerServicesInboundPortURI, 0, 3000); 
	   this.camop.addCore(ComputerServicesInboundPortURI, ApplicationVMManagementInboundPortURI1, 4);
	   this.camop.deleteCore(ComputerServicesInboundPortURI,ApplicationVMManagementInboundPortURI2, 1);
	 System.out.println("it started!");
				//this.camop.addCore("computer0", "vm2", 1);
		// AllocatedCore[] ac2 = this.csPort.allocateCores(1) ;
		// AllocatedCore[] ac3 = this.csPort.allocateCores(1) ;
        //System.out.println("current frequency"+ psPort1.ge());
		//allcors.add(ac1);
		// allcors.add(ac2);
		// allcors.add(ac3);
		//addCore(avmPort1, 1);
		//addCore(avmPort2, 1);
		//addCore(avmPort3, 1);
		// this.avmPort1.allocateCores(ac1);
		// this.avmPort2.allocateCores(ac2);
		// this.avmPort3.allocateCores(ac3);

		String inBoundPortDispatcher1 = rprmop.ajouterVM(RequestSubmissionInboundPortURI1);
		RequestNotificationOutboundPort rpnop1 = (RequestNotificationOutboundPort) mv1
				.findPortFromURI(RequestNotificationOutboundPortURI1);
		rpnop1.doConnection(inBoundPortDispatcher1, RequestNotificationConnector.class.getCanonicalName());

		String inBoundPortDispatcher2 = rprmop.ajouterVM(RequestSubmissionInboundPortURI2);
		RequestNotificationOutboundPort rpnop2 = (RequestNotificationOutboundPort) mv2
				.findPortFromURI(RequestNotificationOutboundPortURI2);
		rpnop2.doConnection(inBoundPortDispatcher2, RequestNotificationConnector.class.getCanonicalName());

		String inBoundPortDispatcher3 = rprmop.ajouterVM(RequestSubmissionInboundPortURI3);
		RequestNotificationOutboundPort rpnop3 = (RequestNotificationOutboundPort) mv3
				.findPortFromURI(RequestNotificationOutboundPortURI3);
		rpnop3.doConnection(inBoundPortDispatcher3, RequestNotificationConnector.class.getCanonicalName());
	}

	@Override
	public void shutdown() throws Exception {

		this.avmPort1.doDisconnection();
		this.avmPort2.doDisconnection();
		this.avmPort3.doDisconnection();

		this.csPort.doDisconnection();
		this.rsobp.doDisconnection();
		this.nobp.doDisconnection();
		this.rgmop.doDisconnection();
		this.rprmop.doDisconnection();

		super.shutdown();
	}

	public void testScenario() throws Exception {
		// start the request generation in the request generator.
		this.rgmop.startGeneration();
		Thread.sleep(5000L);
		System.out.println("étape 2");
		//this.camop.deleteCore(ComputerServicesInboundPortURI, ApplicationVMManagementInboundPortURI1,1);
		//this.camop.updateFrequency(ComputerServicesInboundPortURI, 0, 3000); 
		
		//this.camop.addCore(ComputerServicesInboundPortURI,"vm1", 2);
		//this.addCore(avmPort1, 4);
		//this.addCore(avmPort2, 2);
		// this.addCore(avmPort2, 8);
		// this.addCore(avmPort3, 2);
		// wait 20 seconds
		Thread.sleep(2000L);
		// then stop the generation.
		this.rgmop.stopGeneration();
	}

	public static void main(String[] args) {
		// Uncomment next line to execute components in debug mode.
		// AbstractCVM.toggleDebugMode() ;
		try {
			final TestRequestDispatcherCore trg = new TestRequestDispatcherCore();
			// Deploy the components
			trg.deploy();
			System.out.println("starting...");
			// Start them.
			trg.start();
			// Execute the chosen request generation test scenario in a
			// separate thread.
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						trg.testScenario();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}).start();
			// Sleep to let the test scenario execute to completion.
			Thread.sleep(90000L);
			// Shut down the application.
			System.out.println("shutting down...");
			trg.shutdown();
			System.out.println("ending...");
			// Exit from Java.
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void acceptProcessorStaticData(String processorURI, ProcessorStaticStateI staticState) throws Exception 
	{
		// TODO Auto-generated method stub
		
			StringBuffer sb = new StringBuffer() ;
			sb.append("Accepting static data from " + processorURI + "\n") ;
			sb.append("  timestamp                      : " +
					staticState.getTimeStamp() + "\n") ;
			sb.append("  timestamper id                 : " +
					staticState.getTimeStamperId() + "\n") ;
			sb.append("  number of Cores           : " +
					staticState.getNumberOfCores() + "\n") ;
			sb.append("  default frequency : " +
					staticState.getDefaultFrequency() + "\n") ;
//			for (int p = 0 ; p < staticState.getAdmissibleFrequencies().size(); p++) {
//				if (p == 0) {
//					sb.append("  processor URIs                 : ") ;
//				} else {
//					sb.append("                                 : ") ;
//				}
//			sb.append(p + "  " + staticState.getAdmissibleFrequencies()p) + "\n") ;
//			}
//			sb.append("  processor port URIs            : " + "\n") ;
//			sb.append(Computer.printProcessorsInboundPortURI(
//						10, ss.getNumberOfProcessors(),
//						ss.getProcessorURIs(), ss.getProcessorPortMap())) ;
			//this.logMessage(sb.toString()) ;
		
		System.out.println(sb.toString());
		
	}

	@Override
	public void acceptProcessorDynamicData(String processorURI, ProcessorDynamicStateI currentDynamicState)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
