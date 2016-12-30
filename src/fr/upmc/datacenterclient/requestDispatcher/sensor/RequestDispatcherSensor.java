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
public class RequestDispatcherSensor 
extends AbstractComponent 
implements RequestDispatcherDataConsumerI,
PushModeControllerI{


	// -------------------------------------------------------------------------
	// Component Variables declaration Constructor
	// -------------------------------------------------------------------------


	protected boolean										active ;
	protected RequestDispatcherDynamicStateOutboundPort    	rddsop ;
	protected SensorDynamicDataInboundPort					sddip ;

	protected String 										sensorURI ;
	protected double										meanTime ;
	protected long 											timeRequest;
	protected ScheduledFuture<?>							pushingFuture;

	//variable pour fonctions utils
	private static int 				w = 10;
	private int 					cpt = 0;
	public  long 					h[] ;
	private  int 					r ;
	private long 					total;

	public RequestDispatcherSensor(
			String sensorURI,
			boolean active,
			String sensorDynamicDataInboundPort,
			String requestdispatcherDynamicStateDataOutboundPortURI
			)throws Exception {
		super(1,0);

		this.active = active ;
		this.sensorURI = sensorURI;
		this.pushingFuture = null ;
		this.timeRequest=0;

		this.meanTime = 0;
		r= 0;
		total = 0;
		h = new long[w];
		for(int i=1;i<w; i++) h[i]=0;

		this.addRequiredInterface(ControlledDataRequiredI.ControlledPullI.class) ;
		this.rddsop = new RequestDispatcherDynamicStateOutboundPort(
				requestdispatcherDynamicStateDataOutboundPortURI,
				this, sensorURI);
		this.addPort(rddsop);
		this.rddsop.publishPort();

		this.addOfferedInterface(ControlledDataOfferedI.ControlledPullI.class) ;
		this.sddip = new SensorDynamicDataInboundPort(sensorDynamicDataInboundPort, this);
		this.addPort(sddip);
		this.sddip.publishPort();

		assert this.rddsop != null  ;
	}

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
	
	//** Component Methods for Recieving data from RequestDispatcher         **
	/**
	 *  @see fr.upmc.datacenterclient.requestDispatcher.interfaces.RequestDispatcherDataConsumerI#acceptRequestDispatcherDynamicData(java.lang.String, fr.upmc.datacenterclient.requestDispatcher.interfaces.RequestDispatcherDynamicStateI)
	 */
	@Override
	public void acceptRequestDispatcherDynamicData(
			String computerURI,
			RequestDispatcherDynamicStateI currentDynamicState
	) throws Exception {

		this.timeRequest=currentDynamicState.getTimeRequest();
		System.out.println("Sensor Receive time :"+currentDynamicState.getTimeRequest()+
						   " from : "+currentDynamicState.getDispatcherURI());
		this.sendDynamicData();
	}

	// methodes pour l'actuator
	
	public double getMeanTime(){
		return this.meanTime ;
	}

	public String getSensorURI(){
		return this.sensorURI;
	}

	public SensorDynamicDataI getDynamicData(){
		double t = winnowing(this.timeRequest);
		return new SensorDynamicData(this.getSensorURI(), t);
	}

	public void sendDynamicData() throws Exception{

		if(this.sddip.connected()){
			SensorDynamicDataI sdd = this.getDynamicData();
			this.sddip.send(sdd);

		}

	}

	@Override
	public void startUnlimitedPushing(int interval) throws Exception {
		
	}

	@Override
	public void startLimitedPushing(int interval, int n) throws Exception {
		assert	n > 0 ;

		this.logMessage(this.sensorURI + " startLimitedPushing with interval "
				+ interval + " ms for " + n + " times.") ;

		final RequestDispatcherSensor rd = this ;
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
		this.active=false ;

	}

	// fonctions utils
	
	public  double winnowing(long timeRequest) {
		long first = h[r];
		h[r]=timeRequest;
		double meanTime=0 ;
		if(cpt<w){
			cpt++;
			total +=timeRequest;
			meanTime = total/cpt;
			//System.out.println("ppp :"+meanTime);
		}else{
			meanTime = (total-first+timeRequest)/w;
			total -= first;
		}
		r=(r+1)%w;
		return meanTime;
	}
}