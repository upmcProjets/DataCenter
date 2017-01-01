/**
 * 
 */
package fr.upmc.datacenterclient.actuator;

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
import fr.upmc.datacenterclient.actuator.interfaces.ActuatorI;
import fr.upmc.datacenterclient.actuator.ports.ActuatorManagerInboundPort;
import fr.upmc.datacenterclient.requestDispatcher.connectors.RequestDispatcherManagerConnector;
import fr.upmc.datacenterclient.requestDispatcher.interfaces.RequestDispatcherManagerI;
import fr.upmc.datacenterclient.requestDispatcher.ports.RequestDispatcherManagerOutboundPort;
import fr.upmc.datacenterclient.ressource_manager.connectors.RessourceManagerConnector;
import fr.upmc.datacenterclient.ressource_manager.interfaces.RessourceManagerI;
import fr.upmc.datacenterclient.ressource_manager.ports.RessourceManagerOutboundPort;

/**
 * @author chelbi
 *
 */
public class Actuator 
extends AbstractComponent {

	protected final String actuatorURI;


	public static final String ComputerServicesOutboundPortURI = "cs-obp";
	public static final String ComputerServicesInboundPortURI = "cs-ibp";

	public static final String ApplicationVMManagementInboundPortURI1 = "avm-ibp1";
	public static final String ApplicationVMManagementOutboundPortURI1 = "avm-obp1";

	protected ComputerServicesOutboundPort csPort;
	protected ApplicationVMManagementOutboundPort avmPort;
	protected ActuatorManagerInboundPort ca_contrmip;
	//vm
	private RessourceManagerOutboundPort rmop;
	private RequestDispatcherManagerOutboundPort rdmop;

	Map<String, ArrayList<String>> AppRessources = new HashMap<>();

	public Actuator(String actuatorURI,
			String ComputerActuatorManagerInboundPortURI,
			String ressourceManagerInboundPortUri,
			String requestDispatcherManagerInboundPortUri
			) throws Exception {
		super(1, 1);
		this.actuatorURI= actuatorURI;

		this.addOfferedInterface(ActuatorI.class);
		this.ca_contrmip = new ActuatorManagerInboundPort(
						ComputerActuatorManagerInboundPortURI, this);
		this.addPort(this.ca_contrmip);
		this.ca_contrmip.publishPort();

		//vm

		addRequiredInterface(RessourceManagerI.class);
		addRequiredInterface(RequestDispatcherManagerI.class);

		rmop = new RessourceManagerOutboundPort(this);
		rmop.publishPort();
		this.addPort(rmop);
		rmop.doConnection(ressourceManagerInboundPortUri, 
				RessourceManagerConnector.class.getCanonicalName());

		rdmop = new RequestDispatcherManagerOutboundPort(this);
		rdmop.publishPort();
		this.addPort(rdmop);
		rdmop.doConnection(requestDispatcherManagerInboundPortUri, 
				RequestDispatcherManagerConnector.class.getCanonicalName());


		assert this.ca_contrmip != null;
	}


	
	// Computer method
	
	public void addCore(String computerURI, String vmURI, int nbcore) throws Exception {

		System.out.println("Add cores");
		
		this.csPort = new ComputerServicesOutboundPort(new AbstractComponent() {});
		this.csPort.publishPort();
		this.csPort.doConnection(computerURI, ComputerServicesConnector.class.getCanonicalName());
	
		System.out.println(this.csPort);
		
		this.avmPort = (ApplicationVMManagementOutboundPort) findPortFromURI(vmURI);
	
		this.avmPort = new ApplicationVMManagementOutboundPort(new AbstractComponent() {});
		this.avmPort.publishPort();
		this.avmPort.doConnection(vmURI, ApplicationVMManagementConnector.class.getCanonicalName());
		System.out.println(this.avmPort);
		AllocatedCore[] ac = this.csPort.allocateCores(nbcore);
		System.out.println(" nombre de cores =  " + ac.length);
		avmPort.allocateCores(ac);
	}

	public void deleteCore(String computerURI, String vmURI, int nbcore) throws Exception {

		System.out.println("Delete cores");

		this.csPort = new ComputerServicesOutboundPort(new AbstractComponent() {});
		this.csPort.publishPort();
		this.csPort.doConnection(computerURI, ComputerServicesConnector.class.getCanonicalName());

		System.out.println(this.csPort);
		// ComputerActuator.this.csPort = (ComputerServicesOutboundPort)
		// findPortFromURI(computerURI);

		this.avmPort = (ApplicationVMManagementOutboundPort) findPortFromURI(vmURI);

		this.avmPort = new ApplicationVMManagementOutboundPort(new AbstractComponent() {});
		this.avmPort.publishPort();
		this.avmPort.doConnection(vmURI, ApplicationVMManagementConnector.class.getCanonicalName());
		System.out.println(this.avmPort);
		AllocatedCore[] ac = this.csPort.allocateCores(4 - nbcore);
		System.out.println(" nombre de cores =  " + ac.length);
		avmPort.allocateCores(ac);

	}

	public void updateFrequency(String computerURI, int numCore, int frequency) throws Exception {
		
		System.out.println("Update frequency cores");
		
		this.csPort = new ComputerServicesOutboundPort(new AbstractComponent() {});
		this.csPort.publishPort();
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

	//vm methode
	

    public String removeVM(String requestSubmissionOutboundPort) throws Exception{
        rdmop.supprimerVM(requestSubmissionOutboundPort);
        return "";
    }

    public String addVM(int coreCount) throws Exception{
        if(!rmop.canCreateVM(coreCount))
            throw new Exception("no enough available resources to create vm");
        return rmop.createVM(rdmop.getServerPortURI(), coreCount);
    }
	
	
	
	////////
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
