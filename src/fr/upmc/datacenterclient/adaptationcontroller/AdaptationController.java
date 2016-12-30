package fr.upmc.datacenterclient.adaptationcontroller;

import fr.upmc.datacenterclient.requestDispatcher.sensor.SensorDynamicDataI;
import fr.upmc.datacenterclient.requestDispatcher.sensor.SensorDynamicDataOutboundPort;
import fr.upmc.datacenterclient.vm_actuator.interfaces.VmActuatorI;
import fr.upmc.datacenterclient.vm_actuator.ports.VmActuatorOutboundPort;
import fr.upmc.components.AbstractComponent;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.components.exceptions.ComponentStartException;
import fr.upmc.datacenter.interfaces.ControlledDataRequiredI;
import fr.upmc.datacenterclient.computerActuator.interfaces.ComputerActuatorManagerI;
import fr.upmc.datacenterclient.computerActuator.ports.ComputerActuatorManagerOutboundPort;
import fr.upmc.datacenterclient.requestDispatcher.sensor.RequestDispatcherSensorConsumerI;

public class AdaptationController 
extends AbstractComponent
implements RequestDispatcherSensorConsumerI {

	protected boolean									active ;
	protected String 						adapControllerURI;
	protected SensorDynamicDataOutboundPort   sddop ;
	protected ComputerActuatorManagerOutboundPort camop;
	protected VmActuatorOutboundPort         vmaop ;
	
	
	public AdaptationController(boolean active,
			String adapControllerURI, 
			String sensorDynamicDataOutboundPort ,
			String computerActuatorManagerOutboundPort,
			String vmActuatorOutboundPort ) throws Exception{
		super(1,0);
		this.active = active;
		this.adapControllerURI = adapControllerURI;
		
		this.addRequiredInterface(ControlledDataRequiredI.ControlledPullI.class) ;
		
		this.sddop = new SensorDynamicDataOutboundPort(sensorDynamicDataOutboundPort, this, adapControllerURI);
		this.addPort(sddop);
		this.sddop.publishPort();
		System.out.println(sensorDynamicDataOutboundPort+" ###  "+this.sddop);
		this.addRequiredInterface(ComputerActuatorManagerI.class);
		this.camop = new ComputerActuatorManagerOutboundPort(computerActuatorManagerOutboundPort, this);
		this.addPort(camop);
		this.camop.publishPort();
		
		this.addRequiredInterface(VmActuatorI.class);
		this.vmaop = new VmActuatorOutboundPort(vmActuatorOutboundPort, this);
		this.addPort(vmaop);
		this.vmaop.publishPort();
		
		assert this.sddop != null ;
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
	}




	@Override
	public void acceptRequestDispatcherDynamicData(String monitorURI,
			SensorDynamicDataI currentDynamicData) throws Exception {
		System.out.println("####"+currentDynamicData.getSensorUri()+ " : "+currentDynamicData.getMeanTime());
		
	}

}
