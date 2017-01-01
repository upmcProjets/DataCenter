package fr.upmc.datacenterclient.adaptationcontroller;

import fr.upmc.datacenterclient.requestDispatcher.sensor.SensorDynamicDataI;
import fr.upmc.datacenterclient.requestDispatcher.sensor.SensorDynamicDataOutboundPort;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.components.exceptions.ComponentStartException;
import fr.upmc.datacenter.interfaces.ControlledDataRequiredI;
import fr.upmc.datacenterclient.actuator.interfaces.ActuatorI;
import fr.upmc.datacenterclient.actuator.ports.ActuatorManagerOutboundPort;
import fr.upmc.datacenterclient.requestDispatcher.sensor.RequestDispatcherSensorConsumerI;

public class AdaptationController 
extends AbstractComponent
implements RequestDispatcherSensorConsumerI {

	protected boolean									active ;
	protected String 									adapControllerURI;
	protected SensorDynamicDataOutboundPort   			sddop ;
	protected ActuatorManagerOutboundPort 		camop;
	//protected VmActuatorOutboundPort         			vmaop ;


	public AdaptationController(
			boolean active,
			String adapControllerURI, 
			String sensorDynamicDataOutboundPort ,
			String computerActuatorManagerOutboundPort,
			String vmActuatorOutboundPort 
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
				computerActuatorManagerOutboundPort, this);
		this.addPort(camop);
		this.camop.publishPort();

		
		assert this.sddop != null  && this.camop != null ;
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
