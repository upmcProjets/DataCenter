package fr.upmc.datacenterclient.adaptationcontroller;

import fr.upmc.datacenterclient.requestDispatcher.sensor.SensorDynamicDataI;
import fr.upmc.datacenterclient.requestDispatcher.sensor.SensorDynamicDataOutboundPort;
import fr.upmc.datacenterclient.ressource_manager.RessourceManagerConnector;
import fr.upmc.datacenterclient.ressource_manager.ports.RessourceManagerOutboundPort;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.connectors.DataConnector;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.components.exceptions.ComponentStartException;
import fr.upmc.datacenter.interfaces.ControlledDataRequiredI;
import fr.upmc.datacenterclient.actuator.Actuator;
import fr.upmc.datacenterclient.actuator.connectors.ActuatorConnector;
import fr.upmc.datacenterclient.actuator.interfaces.ActuatorI;
import fr.upmc.datacenterclient.actuator.ports.ActuatorManagerOutboundPort;
import fr.upmc.datacenterclient.requestDispatcher.connectors.RequestDispatcherManagerConnector;
import fr.upmc.datacenterclient.requestDispatcher.ports.RequestDispatcherDynamicStateOutboundPort;
import fr.upmc.datacenterclient.requestDispatcher.ports.RequestDispatcherManagerOutboundPort;
import fr.upmc.datacenterclient.requestDispatcher.sensor.RequestDispatcherSensor;
import fr.upmc.datacenterclient.requestDispatcher.sensor.RequestDispatcherSensorConsumerI;

public class AdaptationController 
extends AbstractComponent
implements RequestDispatcherSensorConsumerI {
	
	protected static final String ActuatorManagerInboundPortURI = "amip";
	protected static final String SensorDynamicDataInboundPort  = "sens" ;
	protected static final String SensorDynamicDataOutboundPort = "sddop" ;
	protected static final String ressourceManagerOutboundPort	= "rmip";
	protected static final String requestDispatcherOutboundPort	= "rdip";
	

	protected boolean									active ;
	protected String 									adapControllerURI;
	protected SensorDynamicDataOutboundPort   			sddop ;
	protected ActuatorManagerOutboundPort 				camop;
	protected RequestDispatcherDynamicStateOutboundPort rddsop ;
	protected RessourceManagerOutboundPort              rmop;
	protected RequestDispatcherManagerOutboundPort				rdop ;
	//protected VmActuatorOutboundPort         			vmaop ;


	public AdaptationController(
			boolean active,
			String adapControllerURI, 
			String sensorDynamicDataOutboundPort ,
			String ActuatorManagerOutboundPort,
			String ressourceManagerInboundPort,
			String requestDispatcherInboundPort,
			String RequestDispatcherDynamicDataInboundPort
			) throws Exception{
		super(1,0);

		this.active = active;
		this.adapControllerURI = adapControllerURI;

		this.addRequiredInterface(ControlledDataRequiredI.ControlledPullI.class) ;
		this.sddop = new SensorDynamicDataOutboundPort(
				sensorDynamicDataOutboundPort, this, adapControllerURI);
		this.addPort(sddop);
		this.sddop.publishPort();

		this.addRequiredInterface(ActuatorI.class);
		this.camop = new ActuatorManagerOutboundPort(
				ActuatorManagerOutboundPort, this);
		this.addPort(camop);
		this.camop.publishPort();
		
		createComponents(ressourceManagerInboundPort,
				requestDispatcherInboundPort,
				RequestDispatcherDynamicDataInboundPort);

		
		assert this.sddop != null  && this.camop != null ;
	}


	private void createComponents(
			String ressourceManagerInboundPort,
			String requestDispatcherInboundPort,
			String requestDispatcherDynamicDataInboundPort
			) throws Exception{
	
		// creation de l Actuator
		Actuator ca = new Actuator(
				"ca",
				ActuatorManagerInboundPortURI,
				ressourceManagerOutboundPort,
				requestDispatcherOutboundPort);	
		ca.toggleTracing();
		ca.toggleLogging();
		ca.start();
		
		// Connexion AdaptationController to Actuator
		this.camop.doConnection(
				ActuatorManagerInboundPortURI,
				ActuatorConnector.class.getCanonicalName());
		
		//Connexion Actuator to Ressource Manager
		
		// faite dans le constructeur de  Actuator
		this.rmop = (RessourceManagerOutboundPort) ca.
				findPortFromURI(ressourceManagerOutboundPort);
		this.rmop.doConnection(
				ressourceManagerInboundPort, RessourceManagerConnector.class.getCanonicalName());
		
		
		
		//Connexion Actuator to RequestDispatcher
		// faite dans le constructeur de  Actuator
		this.rdop =(RequestDispatcherManagerOutboundPort)ca.
				findPortFromURI(requestDispatcherOutboundPort);
		this.rdop.doConnection(
				requestDispatcherInboundPort,
				RequestDispatcherManagerConnector.class.getCanonicalName());
		
		
		
		// Creation du Sensor
		RequestDispatcherSensor rdm = new RequestDispatcherSensor(
				"rdmm", true,
				SensorDynamicDataInboundPort,
				SensorDynamicDataOutboundPort);
		rdm.toggleLogging();
		rdm.toggleTracing();
		rdm.start();
		
		//Connexion Sensor to RequestDispatcher
		this.rddsop = (RequestDispatcherDynamicStateOutboundPort) rdm.
				findPortFromURI(SensorDynamicDataOutboundPort);
		
		this.rddsop.doConnection(requestDispatcherDynamicDataInboundPort,
				DataConnector.class.getCanonicalName());
		
		//connexion AdaptationController to Sensor
		this.sddop.doConnection(SensorDynamicDataInboundPort, DataConnector.class.getCanonicalName());
		
		
	}


	/**
	 *  @see fr.upmc.components.AbstractComponent#start()
	 */
	@Override
	public void start() throws ComponentStartException {
		super.start();
	}

	/**
	 *  @see fr.upmc.components.AbstractComponent#shutdown()
	 */
	@Override
	public void shutdown() throws ComponentShutdownException {
		super.shutdown();
		try {

			this.sddop.doDisconnection();
			//this.camop.doDisconnection();
			//this.vmaop.doDisconnection();

		} catch (Exception e) {
			throw new ComponentShutdownException();
		}
	}


	@Override
	public void acceptRequestDispatcherDynamicData(String monitorURI,
			SensorDynamicDataI currentDynamicData) throws Exception {
		System.out.println("AC recieve temps moyen : "+
				currentDynamicData.getMeanTime()+ " from : "+
				currentDynamicData.getSensorUri());
		
		

	}

}
