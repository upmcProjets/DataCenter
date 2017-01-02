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
import fr.upmc.datacenterclient.ressource_manager.RessourceManagerConnector;
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
	protected ActuatorManagerInboundPort amip;
	//vm
	private RessourceManagerOutboundPort rmop;
	private RequestDispatcherManagerOutboundPort rdmop;

	Map<String, ArrayList<String>> AppRessources = new HashMap<>();

	public Actuator(String actuatorURI,
			String actuatorManagerInboundPortURI,
			String ressourceManagerOutboundPortUri,
			String requestDispatcherManagerOutboundPortUri
			) throws Exception {
		super(1, 1);
		this.actuatorURI= actuatorURI;

		this.addOfferedInterface(ActuatorI.class);
		this.amip = new ActuatorManagerInboundPort(
						actuatorManagerInboundPortURI, this);
		this.addPort(this.amip);
		this.amip.publishPort();

		//vm

		addRequiredInterface(RequestDispatcherManagerI.class);
		rmop = new RessourceManagerOutboundPort(ressourceManagerOutboundPortUri, this);
		rmop.publishPort();
		this.addPort(rmop);
		//

		
		addRequiredInterface(RessourceManagerI.class);
		rdmop = new RequestDispatcherManagerOutboundPort(requestDispatcherManagerOutboundPortUri,this);
		rdmop.publishPort();
		this.addPort(rdmop);
		


	}


	
	// Computer method
	
	public void addCore(String vmURI, int nbcore) throws Exception {

		System.out.println("Add cores");	
		this.rmop.updateVMCoresNumber(vmURI, nbcore);
	}

	public void deleteCore(String vmURI, int nbcore) throws Exception {

		System.out.println("Delete cores");
		this.rmop.updateVMCoresNumber(vmURI, nbcore);

	}

	public void updateFrequency(String processorManagementInboundPortURI, int numCore, int frequency) throws Exception {
		// TODO Auto-generated method stub

		System.out.println("Update frequency cores");
	
		ProcessorManagementOutboundPort pmPort = 
				new ProcessorManagementOutboundPort(new AbstractComponent() {
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
			e.printStackTrace();
		}
		try {
			this.avmPort.doDisconnection();
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.shutdown();
	}

}
