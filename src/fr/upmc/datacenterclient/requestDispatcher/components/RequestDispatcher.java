/**
 * 
 */
package fr.upmc.datacenterclient.requestDispatcher.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.ComponentI;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.components.exceptions.ComponentStartException;
import fr.upmc.datacenter.interfaces.ControlledDataOfferedI;
import fr.upmc.datacenter.interfaces.PushModeControllerI;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.interfaces.RequestI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationHandlerI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionHandlerI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionI;
import fr.upmc.datacenter.software.ports.RequestNotificationInboundPort;
import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionInboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;
import fr.upmc.datacenterclient.requestDispatcher.interfaces.RequestDispatcherDynamicStateI;
import fr.upmc.datacenterclient.requestDispatcher.interfaces.RequestDispatcherManagerI;
import fr.upmc.datacenterclient.requestDispatcher.ports.RequestDispatcherDynamicStateInboundPort;
import fr.upmc.datacenterclient.requestDispatcher.ports.RequestDispatcherManagerInboundPort;
import fr.upmc.datacenterclient.utils.TimeProcessing;

/**
 * La classe <code>RequestDispatcher</code> est le composant qui permet de
 * connecter et déconnecter Les machines virtuelles
 *
 * <p><strong>Description</strong></p>
 *
 * une request qui arrive par le port de soumission sera transmise a une machine virtuelle
 * préalablement connectée au <code>RequestDispatcher</code>.le RequestDispatcher stocke 
 * les uris de soumission des machine virtuelles dans une list , et transmis ainsi la request 
 * reçu à la machine virtuelle la moins récente   
 * 
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * 
 * @authors zahir CHELBI, hacene KEDJAR, lyes CHELFI
 */
public class RequestDispatcher 
extends AbstractComponent
implements RequestNotificationHandlerI,
RequestSubmissionHandlerI,
PushModeControllerI{


	protected final String  dispatcherURI ;
	protected RequestSubmissionInboundPort      rsip ;
	protected RequestSubmissionOutboundPort		rsop ;
	protected RequestNotificationInboundPort	rnip ;
	protected RequestNotificationOutboundPort   rnop ;
	//protected RequestDispatcherSensorInboundPort rdddip ;
	protected RequestDispatcherManagerInboundPort rprmip ;

	// port pour le sensor
	
	protected RequestDispatcherDynamicStateInboundPort    rddsip ;

	protected Map<String,Long> debutReq;
	protected long finReq;
	protected ScheduledFuture<?>		pushingFuture ;
	protected boolean active ;

	//Structure pour gerer les vm allouer
	protected Map<String,RequestSubmissionOutboundPort> vmsOutboundPort ;
	protected Queue<RequestDispatcherDynamicState> dataTimes ;
	protected int vmNumero ;

	/**
	 * cree le composant <code>RequestDispatcher</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	repURI != null and
			requestSubmissionInboundPort != null and
			requestNotificationInboundPort != null and
			requestSubmissionOutboundPort != null and
			requestNotificationOutboundPort != null and
			repartiteurRequestManagerInboundPort != null and
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param repURI    l'uri du composant RequestDispatcher
	 * @param requestSubmissionInboundPort 			l'uri du port de soumission   inboundPort  du RequestDispatcher
	 * @param requestNotificationInboundPort 		l'uri du port de notification inboundPort  du RequestDispatcher
	 * @param requestSubmissionOutboundPort  		l'uri du port de soumission   outboundPort du RequestDispatcher
	 * @param requestNotificationOutboundPort 		l'uri du port de notification outboundPort du RequestDispatcher
	 * @param repartiteurRequestManagerInboundPort  l'uri du port de management   inboundPort  du RequestDispatcher
	 * @throws Exception
	 */
	public RequestDispatcher(
			String repURI,
			String requestSubmissionInboundPort,
			String requestNotificationInboundPort,
			String requestSubmissionOutboundPort,
			String requestNotificationOutboundPort,
			String repartiteurRequestManagerInboundPort,
			String requestDispatcherStaticStateDataInboundPort,
			String requestDispatcherDynamiceDataInboundPort
			) throws Exception
	{
		super(1, 1);

		assert repURI != null &&
				requestSubmissionInboundPort != null &&
				requestNotificationInboundPort != null &&
				requestSubmissionOutboundPort != null &&
				requestNotificationOutboundPort != null &&
				repartiteurRequestManagerInboundPort != null ;

		this.dispatcherURI = repURI ;
		this.active = false ;

		this.vmsOutboundPort = new HashMap<String,RequestSubmissionOutboundPort>();
		this.vmNumero = 0 ;
		this.debutReq = new HashMap<String,Long>();
		this.dataTimes = new  LinkedList<RequestDispatcherDynamicState>();
		this.pushingFuture = null ;

		this.addRequiredInterface(RequestSubmissionI.class);
		this.rsop = new RequestSubmissionOutboundPort(requestSubmissionOutboundPort, this);
		this.addPort(rsop);
		if(AbstractCVM.isDistributed){
			this.rsop.publishPort();
		}else{
			this.rsop.localPublishPort();
		}

		this.addRequiredInterface(RequestNotificationI.class);
		this.rnop = new RequestNotificationOutboundPort(requestNotificationOutboundPort, this);
		this.addPort(rnop);
		if(AbstractCVM.isDistributed){
			this.rnop.publishPort();
		}else{
			this.rnop.localPublishPort();
		}

		this.addOfferedInterface(RequestSubmissionI.class);
		this.rsip = new RequestSubmissionInboundPort(
				requestSubmissionInboundPort, this);
		this.addPort(rsip);

		if(AbstractCVM.isDistributed){
			this.rsip.publishPort();
		}else{
			this.rsip.localPublishPort();
		}

		this.addOfferedInterface(RequestNotificationI.class);
		this.rnip = new RequestNotificationInboundPort(
				requestNotificationInboundPort, this);
		this.addPort(rnip);
		if(AbstractCVM.isDistributed){
			this.rnip.publishPort();
		}else{
			this.rnip.localPublishPort();
		}

		this.addOfferedInterface(RequestDispatcherManagerI.class);
		this.rprmip = new RequestDispatcherManagerInboundPort(
				repartiteurRequestManagerInboundPort,this);
		this.addPort(rprmip);
		if(AbstractCVM.isDistributed){
			this.rprmip.publishPort();
		}else{
			this.rprmip.localPublishPort();
		}


		// sensor port

		

		this.addOfferedInterface(ControlledDataOfferedI.ControlledPullI.class) ;
		this.rddsip = new RequestDispatcherDynamicStateInboundPort(
				requestDispatcherDynamiceDataInboundPort, this);
		this.addPort(rddsip);
		this.rddsip.publishPort();



		assert  this.vmsOutboundPort != null && this.rsop != null && this.rnop !=null &&
				this.rsip != null && this.rnip != null && this.rprmip != null ;
		assert this.rddsip != null ;

	}


	@Override
	public void start() throws ComponentStartException {
		// TODO Auto-generated method stub
		super.start();

	}


	@Override
	public void shutdown() throws ComponentShutdownException {
		try {
			for(RequestSubmissionOutboundPort rsopi : this.vmsOutboundPort.values()){
				if(rsopi.connected()){
					rsopi.doDisconnection();
				}
			}
			if(this.rnop.connected()){
				this.rnop.doDisconnection();	
			}
			if(this.rsop.connected()){
				this.rsop.doDisconnection();	
			}	
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	/**
	 * @see fr.upmc.datacenter.software.interfaces.RequestSubmissionHandlerI#acceptRequestSubmission(fr.upmc.datacenter.software.interfaces.RequestI)
	 */
	@Override
	public void acceptRequestSubmission(RequestI r) throws Exception {
		this.rsop.submitRequest(r);

	}

	/**
	 * @see fr.upmc.datacenter.software.interfaces.RequestSubmissionHandlerI#acceptRequestSubmissionAndNotify(fr.upmc.datacenter.software.interfaces.RequestI)
	 */
	@Override
	public void acceptRequestSubmissionAndNotify(RequestI r) throws Exception {
		assert r != null ;
		if(this.rddsip.connected()){
			long time=System.currentTimeMillis();
			debutReq.put(r.getRequestURI(), time);
		}
		this.logMessage("RequestDispatcher "+this.dispatcherURI+
				" submitting request "+r.getRequestURI()+
				" at " +TimeProcessing.toString(System.currentTimeMillis()) +
				" and notify");
		startRepartition(r);
	}

	/**
	 * @see fr.upmc.datacenter.software.interfaces.RequestNotificationHandlerI#acceptRequestTerminationNotification(fr.upmc.datacenter.software.interfaces.RequestI)
	 */
	@Override
	public void acceptRequestTerminationNotification(RequestI r) throws Exception {
		assert	r != null ;
		
		if(this.rddsip.connected()){
			finReq =System.currentTimeMillis();
			long timeRequest =finReq - debutReq.get(r.getRequestURI());
			System.out.println("temps : "+debutReq.get(r.getRequestURI())+" + "+finReq+" = "+timeRequest);
			debutReq.remove(r.getRequestURI());
			dataTimes.add(new RequestDispatcherDynamicState(dispatcherURI, timeRequest));
			this.startLimitedPushing(1, 1);
		}
		this.logMessage("RequestDispatcher " + this.dispatcherURI +
				" is notified that request "+ r.getRequestURI() + " has ended.") ;


		
		

		this.rnop.notifyRequestTermination(r);
	}

	// -------------------------------------------------------------------------
	// Component internal services
	// -------------------------------------------------------------------------

	/**
	 * la methode startRepartition est une methode interne au composant qui reçois en
	 * paramètre une request et la soumet à une machine virtuelle
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre  r != null
	 * post	true
	 * </pre>
	 * @param r  			la request à soumettre à une machine virtuelle
	 * @throws Exception
	 */
	private void startRepartition(RequestI r) throws Exception{

		assert	r != null ;


		if(this.vmsOutboundPort.size()!=0){
			vmNumero= (vmNumero+1)%vmsOutboundPort.size();
			RequestSubmissionOutboundPort rsopi = (RequestSubmissionOutboundPort) 
					this.vmsOutboundPort.values().toArray()[vmNumero] ;
			rsopi.submitRequestAndNotify(r);
		}else{
			assert rsop.connected();
			//this.rsop.submitRequestAndNotify(r);
		}
	}

	/**
	 * connecte les machines virtuelles au Dispatcher
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre  vms != null and vms.lenght > 0
	 * post	return !=null and return.lenght= vms.lenght
	 * </pre>
	 * @param vms  	List<String> des URI SubmissionInboundPort des machine virtuelles
	 * @return		List<String> des URI NotificationInboundPort du RequestDispatcher pour les machines virtuelles
	 * @throws Exception
	 */
	public List<String> connectWithVMS(List<String> appVMS)throws Exception{

		assert appVMS != null && appVMS.size() != 0 ;
		List<String> inboundPort = new ArrayList<String>(); 

		for(String port : appVMS ){
			//port de submission
			RequestSubmissionOutboundPort rsopp = new RequestSubmissionOutboundPort(this);
			this.addPort(rsopp);
			if(AbstractCVM.isDistributed){
				rsopp.publishPort();
			}else{
				rsopp.localPublishPort();
			}
			this.vmsOutboundPort.put(rsopp.getPortURI(), rsopp);

			rsopp.doConnection(port, RequestSubmissionConnector.class.getCanonicalName()) ;

			// port de Notification  
			RequestNotificationInboundPort rnipp = new RequestNotificationInboundPort(this); 
			this.addPort(rnipp);
			if(AbstractCVM.isDistributed){
				rnipp.publishPort();
			}else{
				rnipp.localPublishPort();
			}

			inboundPort.add(rnipp.getPortURI());

			assert inboundPort != null && inboundPort.size() == appVMS.size();
		}
		return inboundPort ;
	}

	/**
	 * connecte une machine virtuelle au Dispatcher
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre  requestSubmissionOutboundPort != null
	 * post	return !=null
	 * </pre>
	 * @param requestSubmissionInboundPort  	l'URI de SubmissionInboundPort d'une machine virtuelle
	 * @return					l'URI de NotificationInboundPort du RequestDispatcher pour la machine virtuelle
	 * @throws Exception
	 */
	public String ajouterVM(String requestSubmission) throws Exception {

		assert requestSubmission != null ;

		//port de submission
		RequestSubmissionOutboundPort rsopp = new RequestSubmissionOutboundPort(this);
		this.addPort(rsopp);
		if(AbstractCVM.isDistributed){
			rsopp.publishPort();
		}else{
			rsopp.localPublishPort();
		}
		this.vmsOutboundPort.put(rsopp.getPortURI(), rsopp);
		rsopp.doConnection(requestSubmission, RequestSubmissionConnector.class.getCanonicalName()) ;

		// port de Notification  
		RequestNotificationInboundPort rnipp = new RequestNotificationInboundPort(this); 
		this.addPort(rnipp);
		if(AbstractCVM.isDistributed){
			rnipp.publishPort();
		}else{
			rnipp.localPublishPort();
		}

		assert rnipp.getPortURI() != null ;

		return rnipp.getPortURI();
	}

	/**
	 * déconnecte une machine virtuelle du Dispatcher
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre  requestSubmissionOutboundPort != null
	 * post	true // pas de postcondition
	 * </pre>
	 * @param requestSubmissionOutboundPort  	l'URI de SubmissionInboundPort de la machine virtuelle a supprimer
	 * @throws Exception
	 */
	public void supprimerVM(String requestSubmission) throws Exception {

		assert requestSubmission != null ;

		for(String rsopp: this.vmsOutboundPort.keySet()){

			if(vmsOutboundPort.get(rsopp).getServerPortURI()==requestSubmission){
				vmsOutboundPort.get(rsopp).doDisconnection();
				vmsOutboundPort.remove(rsopp);	

			}	
		}	
	}


	// -------------------------------------------------------------------------
	// Component Sensor services
	// -------------------------------------------------------------------------

	//***************************

	public RequestDispatcherDynamicStateI getDynamicState() {

		return dataTimes.poll();
	}

	public void			sendDynamicState() throws Exception
	{

		if (this.rddsip.connected()) {

			RequestDispatcherDynamicStateI cds = this.getDynamicState() ;
			this.rddsip.send(cds);
		}
	}

//	public void			sendDynamicState(
//			final int interval,
//			int numberOfRemainingPushes
//			) throws Exception
//	{
//
//		//this.sendDynamicState();
//		final int fNumberOfRemainingPushes = numberOfRemainingPushes - 1 ;
//		if (fNumberOfRemainingPushes > 0) {
//			final RequestDispatcher rd = this ;
//			this.pushingFuture =
//					this.scheduleTask(
//							new ComponentI.ComponentTask() {
//								@Override
//								public void run() {
//									try {
//										rd.sendDynamicState(
//												interval,
//												fNumberOfRemainingPushes) ;
//									} catch (Exception e) {
//										throw new RuntimeException(e) ;
//									}
//								}
//							}, interval, TimeUnit.MILLISECONDS) ;
//		}
//	}


	//****************************


	public String		getDispatcherURI(){
		return this.dispatcherURI;
	}

	public long   getTimeRequest(){
		return 100;
	}


	@Override
	public void startUnlimitedPushing(int interval) throws Exception {
		this.logMessage("startUnlimitedPushing");

		// first, send the static state if the corresponding port is connected
		this.sendDynamicState() ;

		final RequestDispatcher rd = this ;
		this.pushingFuture =
				this.scheduleTaskAtFixedRate(
						new ComponentI.ComponentTask() {
							@Override
							public void run() {
								try {
									rd.sendDynamicState() ;
								} catch (Exception e) {
									throw new RuntimeException(e) ;
								}
							}
						}, interval, interval, TimeUnit.MILLISECONDS) ;

	}


	@Override
	public void startLimitedPushing(final int interval,final  int n) throws Exception {

		assert	n > 0 ;

		this.logMessage(this.dispatcherURI + " startLimitedPushing with interval "
				+ interval + " ms for " + n + " times.") ;

		// first, send the static state if the corresponding port is connected
		//this.sendStaticState() ;
		
		final RequestDispatcher rd = this ;
		this.pushingFuture =
				this.scheduleTask(
						new ComponentI.ComponentTask() {
							@Override
							public void run() {
								
									//rd.sendDynamicState(interval, n) ;
								try {
									rd.sendDynamicState();
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
							}
						}, interval, TimeUnit.MILLISECONDS) ;


	}


	@Override
	public void stopPushing() throws Exception {
		this.logMessage("stopPushing");

	}




}
