/**
 * 
 */
package fr.upmc.datacenterclient.requestDispatcher.sensor;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.ComponentI;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.components.exceptions.ComponentStartException;
import fr.upmc.datacenter.interfaces.ControlledDataOfferedI;
import fr.upmc.datacenter.interfaces.ControlledDataRequiredI;
import fr.upmc.datacenter.interfaces.PushModeControllerI;
import fr.upmc.datacenterclient.requestDispatcher.interfaces.RequestDispatcherDataConsumerI;
import fr.upmc.datacenterclient.requestDispatcher.interfaces.RequestDispatcherDynamicStateI;
import fr.upmc.datacenterclient.requestDispatcher.ports.RequestDispatcherDynamicStateOutboundPort;

/**
 * @author chelbi
 *
 */
public class RequestDispatcherMonitor 
extends AbstractComponent 
implements RequestDispatcherDataConsumerI,
PushModeControllerI{

	protected boolean										active ;
	protected RequestDispatcherDynamicStateOutboundPort    	rddsop ;
	protected SensorDynamicDataInboundPort					sddip ;

	protected String 										sensorURI ;
	protected long											meanTime ;
	protected ScheduledFuture<?>							pushingFuture;

	// -------------------------------------------------------------------------
	// Component Constructor
	// -------------------------------------------------------------------------

	public RequestDispatcherMonitor(
			String dispatcherURI,
			boolean active,
			String sensorDynamicDataInboundPort,
			String requestdispatcherDynamicStateDataOutboundPortURI
			)throws Exception {
		super(1,0);
		this.active = active ;
		this.sensorURI = dispatcherURI;
		this.pushingFuture = null ;
		this.meanTime = 0;

		this.addRequiredInterface(ControlledDataRequiredI.ControlledPullI.class) ;
		this.rddsop = new RequestDispatcherDynamicStateOutboundPort(requestdispatcherDynamicStateDataOutboundPortURI, this, dispatcherURI);
		this.addPort(rddsop);
		this.rddsop.publishPort();
		System.out.println("sensor"+rddsop.getPortURI());

		this.addOfferedInterface(ControlledDataOfferedI.ControlledPullI.class) ;
		this.sddip = new SensorDynamicDataInboundPort(sensorDynamicDataInboundPort, this);
		this.addPort(sddip);
		this.sddip.publishPort();

		assert this.rddsop != null  ;
	}


	@Override
	public void start() throws ComponentStartException {

		super.start();
		try {
			//this.rddsop.startLimitedPushing(1, 1);
			this.logMessage("call to start");
		} catch (Exception e) {
			throw new ComponentStartException(
					"Unable to start the pushing of dynamic data from"
							+ " the comoter component.", e) ;
		}
	}

	/**
	 *  @see fr.upmc.components.AbstractComponent#shutdown()
	 */
	@Override
	public void shutdown() throws ComponentShutdownException {

		super.shutdown();
	}



	/**
	 *  @see fr.upmc.datacenterclient.requestDispatcher.interfaces.RequestDispatcherDataConsumerI#acceptRequestDispatcherDynamicData(java.lang.String, fr.upmc.datacenterclient.requestDispatcher.interfaces.RequestDispatcherDynamicStateI)
	 */
	@Override
	public void acceptRequestDispatcherDynamicData(String computerURI,
			RequestDispatcherDynamicStateI currentDynamicState) throws Exception {

		this.meanTime=currentDynamicState.getTimeRequest();
		System.out.println("Receive : "+currentDynamicState.getDispatcherURI()+" : "+currentDynamicState.getTimeRequest());
		this.sendDynamicData();
	}





	//methodes pour l'actuator

	public long getMeanTime(){
		return this.meanTime ;
	}

	public String getSensorURI(){
		return this.sensorURI;
	}

	public SensorDynamicDataI getDynamicData(){
		return new SensorDynamicData(this.getSensorURI(), this.getMeanTime());
	}

	public void sendDynamicData() throws Exception{

		if(this.sddip.connected()){
			SensorDynamicDataI sdd = this.getDynamicData();
			this.sddip.send(sdd);

		}

	}

	@Override
	public void startUnlimitedPushing(int interval) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void startLimitedPushing(int interval, int n) throws Exception {
		assert	n > 0 ;

		this.logMessage(this.sensorURI + " startLimitedPushing with interval "
				+ interval + " ms for " + n + " times.") ;

		// first, send the static state if the corresponding port is connected
		//this.sendStaticState() ;

		final RequestDispatcherMonitor rd = this ;
		this.pushingFuture =
				this.scheduleTask(
						new ComponentI.ComponentTask() {
							@Override
							public void run() {
								try {
									rd.sendDynamicData();

								} catch (Exception e) {
									throw new RuntimeException(e) ;
								}
							}
						}, interval, TimeUnit.MILLISECONDS) ;
	}

	@Override
	public void stopPushing() throws Exception {
		// TODO Auto-generated method stub

	}

}
