package fr.upmc.datacenterclient.computerActuator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.hardware.processors.Processor.ProcessorPortTypes;
import fr.upmc.datacenter.hardware.processors.connectors.ProcessorManagementConnector;
import fr.upmc.datacenter.hardware.processors.connectors.ProcessorServicesConnector;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorManagementOutboundPort;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorServicesOutboundPort;
import fr.upmc.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.upmc.datacenterclient.computerActuator.interfaces.ComputerActuatorManagerI;
import fr.upmc.datacenterclient.computerActuator.ports.ComputerActuatorManagerInboundPort;

public class ComputerActuator extends AbstractComponent {

	protected final String ComputerActuatorURI;

	protected ComputerActuatorManagerInboundPort ca_contrmip;
	public static final String ComputerServicesOutboundPortURI = "cs-obp";
	public static final String ComputerServicesInboundPortURI = "cs-ibp";

	public static final String ApplicationVMManagementInboundPortURI1 = "avm-ibp1";
	public static final String ApplicationVMManagementOutboundPortURI1 = "avm-obp1";

	protected ComputerServicesOutboundPort csPort;
	protected ApplicationVMManagementOutboundPort avmPort;
	//protected ProcessorManagementOutboundPort psPort;

	Map<String, ArrayList<String>> AppRessources = new HashMap<>();

	public ComputerActuator(String ComputerActuatorURI,
			// String requestNotificationOutboundPort,
			// String requestNotificationInboudPort,
			String ComputerActuatorManagerInboundPortURI// ,
	// Map<String,ArrayList<String>> AppRessources
	) throws Exception {
		super(1, 1);
		assert ComputerActuatorURI != null && ComputerActuatorManagerInboundPortURI != null;

		this.ComputerActuatorURI = ComputerActuatorURI;

		// this.addRequiredInterface(RequestNotificationI.class);
		// this.rnop = new
		// RequestNotificationOutboundPort(requestNotificationOutboundPort,
		// this);
		// this.addPort(rnop);
		// this.rnop.publishPort();
		//

		// this.addOfferedInterface(RequestNotificationI.class);
		// this.rnip = new
		// RequestNotificationInboundPort(requestNotificationInboudPort, this);
		// this.addPort(rnip);
		// this.rnip.publishPort();
		//

		// Adding computer actuator interfaces, creating and publishing the
		// related ports
		this.addOfferedInterface(ComputerActuatorManagerI.class);
		this.ca_contrmip = new ComputerActuatorManagerInboundPort(ComputerActuatorManagerInboundPortURI, this);
		this.addPort(this.ca_contrmip);
		this.ca_contrmip.publishPort();

		/*
		 * this.addOfferedInterface(ComputerActuatorManagerI.class);
		 * this.ca_contrmip = new
		 * ComputerActuatorManagerInboundPort(ComputerActuatorManagerInboundPort
		 * ,this); this.addPort(ca_contrmip); if(AbstractCVM.isDistributed){
		 * this.ca_contrmip.publishPort(); }else{
		 * this.ca_contrmip.localPublishPort(); }
		 */
		assert this.ca_contrmip != null;

	}

	/*
	 * public int getnb_disp_core(AllocatedCore[] cores) { int cpt = 0;
	 * 
	 * for (int j = 0; j < cores.length; j++) if (cores[j] != null) cpt++;
	 * 
	 * return cpt; }
	 * 
	 * public AllocatedCore[] get_core_disp(AllocatedCore [] cores,int nbcore) {
	 * 
	 * int cpt = 0; int i = 0; AllocatedCore[] ac = new AllocatedCore[nbcore];
	 * for (int j = 0; j < cores.length; j++) { if (cores[j] != null) { cpt++;
	 * ac[i] = cores[j]; cores[j] = null; i++; } if (cpt == nbcore) break; }
	 * return ac; }
	 * 
	 * 
	 * public void addCore(ApplicationVMManagementOutboundPort avmPort, int
	 * nbcore) throws Exception { int max = 0; AllocatedCore[] ac = new
	 * AllocatedCore[nbcore]; int i = 0; boolean created = false; for
	 * (AllocatedCore[] cores : allcors) { System.out.println("dispo core =" +
	 * getnb_disp_core(cores)); if (getnb_disp_core(cores) >= nbcore) { ac =
	 * get_core_disp(cores, nbcore); System.out.println(" we create " + nbcore +
	 * " cores for you"); avmPort.allocateCores(ac); created = true; break; }
	 * else
	 * 
	 * if (max < getnb_disp_core(cores)) { max = getnb_disp_core(cores); ac =
	 * get_core_disp(cores, nbcore); }
	 * 
	 * } if(created == false){ if (max==0)
	 * System.out.println(" we can't allocate more cores"); else{
	 * System.out.println(" we can't create this numbres of cores");
	 * System.out.println(" instead we create " + max + " cores for you");
	 * AllocatedCore[] ac2 = new AllocatedCore[max]; int k = 0; for
	 * (AllocatedCore core : ac) if (core != null) { ac2[k] = core; k++; }
	 * avmPort.allocateCores(ac2); } }
	 * 
	 * }
	 */

	public void addCore(String computerURI, String vmURI, int nbcore) throws Exception {

		System.out.println("Add cores");
		this.csPort = new ComputerServicesOutboundPort(new AbstractComponent() {
		});
		this.csPort.publishPort();
		// this.csPort.doConnection(ComputerServicesInboundPortURI,
		// ComputerServicesConnector.class.getCanonicalName());
		this.csPort.doConnection(computerURI, ComputerServicesConnector.class.getCanonicalName());

		System.out.println(this.csPort);
		// ComputerActuator.this.csPort = (ComputerServicesOutboundPort)
		// findPortFromURI(computerURI);

		this.avmPort = (ApplicationVMManagementOutboundPort) findPortFromURI(vmURI);

		this.avmPort = new ApplicationVMManagementOutboundPort(new AbstractComponent() {
		});
		this.avmPort.publishPort();
		this.avmPort.doConnection(vmURI, ApplicationVMManagementConnector.class.getCanonicalName());
		System.out.println(this.avmPort);
		AllocatedCore[] ac = this.csPort.allocateCores(nbcore);
		System.out.println(" nombre de cores =  " + ac.length);
		avmPort.allocateCores(ac);
	}

	public void deleteCore(String computerURI, String vmURI, int nbcore) throws Exception {

		System.out.println("Delete cores");

		this.csPort = new ComputerServicesOutboundPort(new AbstractComponent() {
		});
		this.csPort.publishPort();
		// this.csPort.doConnection(ComputerServicesInboundPortURI,
		// ComputerServicesConnector.class.getCanonicalName());
		this.csPort.doConnection(computerURI, ComputerServicesConnector.class.getCanonicalName());

		System.out.println(this.csPort);
		// ComputerActuator.this.csPort = (ComputerServicesOutboundPort)
		// findPortFromURI(computerURI);

		this.avmPort = (ApplicationVMManagementOutboundPort) findPortFromURI(vmURI);

		this.avmPort = new ApplicationVMManagementOutboundPort(new AbstractComponent() {
		});
		this.avmPort.publishPort();
		this.avmPort.doConnection(vmURI, ApplicationVMManagementConnector.class.getCanonicalName());
		System.out.println(this.avmPort);
		AllocatedCore[] ac = this.csPort.allocateCores(4 - nbcore);
		System.out.println(" nombre de cores =  " + ac.length);
		avmPort.allocateCores(ac);

	}

	public void updateFrequency(String computerURI, int numCore, int frequency) throws Exception {
		// TODO Auto-generated method stub

		System.out.println("Update frequency cores");
		
		this.csPort = new ComputerServicesOutboundPort(new AbstractComponent() {
		});
		this.csPort.publishPort();
		// this.csPort.doConnection(ComputerServicesInboundPortURI,
		// ComputerServicesConnector.class.getCanonicalName());
		this.csPort.doConnection(computerURI, ComputerServicesConnector.class.getCanonicalName());

		AllocatedCore[] ac = this.csPort.allocateCores(2);

		final String processorServicesInboundPortURI = ac[1].processorInboundPortURI.get(ProcessorPortTypes.SERVICES);
		final String processorManagementInboundPortURI = ac[1].processorInboundPortURI
				.get(ProcessorPortTypes.MANAGEMENT);

		// this.avmPort = (ApplicationVMManagementOutboundPort)
		// findPortFromURI(ApplicationVMManagementInboundPortURI1);

		this.avmPort = new ApplicationVMManagementOutboundPort(new AbstractComponent() {
		});
		this.avmPort.publishPort();
		this.avmPort.doConnection(ApplicationVMManagementInboundPortURI1,
				ApplicationVMManagementConnector.class.getCanonicalName());
		System.out.println(this.avmPort);

		System.out.println(" cores allocated " + ac.toString());
		avmPort.allocateCores(ac);

		ProcessorServicesOutboundPort psPort = new ProcessorServicesOutboundPort(new AbstractComponent() {
		});
		psPort.publishPort();
		psPort.doConnection(processorServicesInboundPortURI, ProcessorServicesConnector.class.getCanonicalName());

		ProcessorManagementOutboundPort pmPort = new ProcessorManagementOutboundPort(new AbstractComponent() {
		});
		pmPort.publishPort();
		pmPort.doConnection(processorManagementInboundPortURI, ProcessorManagementConnector.class.getCanonicalName());

		Thread.sleep(3000L);
		System.out.println("Start updating...");
		pmPort.setCoreFrequency(0, 3000);

		//psPort.doDisconnection();
		pmPort.doDisconnection();
		//psPort.unpublishPort();
		pmPort.unpublishPort();

	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		try {
			this.csPort.doDisconnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.avmPort.doDisconnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		super.shutdown();
	}

}
