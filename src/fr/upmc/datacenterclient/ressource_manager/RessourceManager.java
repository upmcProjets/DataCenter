package fr.upmc.datacenterclient.ressource_manager;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.exceptions.ComponentStartException;
import fr.upmc.datacenter.connectors.ControlledDataConnector;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerDynamicStateI;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerServicesI;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerStateDataConsumerI;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerStaticStateI;
import fr.upmc.datacenter.hardware.computers.ports.ComputerDynamicStateDataOutboundPort;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.interfaces.ControlledDataRequiredI;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;
import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.upmc.datacenterclient.requestDispatcher.components.RequestDispatcher;
import fr.upmc.datacenterclient.requestDispatcher.connectors.RequestDispatcherManagerConnector;
import fr.upmc.datacenterclient.requestDispatcher.interfaces.RequestDispatcherManagerI;
import fr.upmc.datacenterclient.requestDispatcher.ports.RequestDispatcherManagerOutboundPort;
import fr.upmc.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.upmc.datacenterclient.ressource_manager.interfaces.RessourceManagerI;
import fr.upmc.datacenterclient.ressource_manager.ports.RessourceManagerInboundPort;
import fr.upmc.datacenterclient.ressource_manager.ports.RessourceManagerOutboundPort;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Hacene on 12/30/2016.
 */
public class RessourceManager extends AbstractComponent implements ComputerStateDataConsumerI {
    private Map<String, ComputerServicesOutboundPort> computers = new HashMap<>();
    private Map<String, ComputerServicesOutboundPort> vmComputer = new HashMap<>();
    private Map<String, Computer.AllocatedCore[]> vmAllocatedCoreMap = new HashMap<>();
    private Map<String, ComputerDynamicStateI> computersDynamicState = new HashMap<>();
    private Map<String, ComputerStaticStateI> computersStaticState = new HashMap<>();
    private Map<String, String> appVMSubmissionIPToAppVMManIP = new HashMap<>();
    private Map<String, ComputerDynamicStateDataOutboundPort> computerDynamicStateDataOutboundPortMap = new HashMap<>();

    private static int vmCount = 0;

    private static final String VM_PREFIX = "vm";
    private static final String APPLICATION_VM_MANAGMENT_INBOUND_PORT_PREFIX = "avmmip";
    private static final String REQUEST_SUBMISSION_INBOUD_PORT_PREFIX = "rsip";
    private static final String REQUEST_NOTIFICATION_OUTBOUND_PORT = "rnop";
    private static final String APPLICATION_VM_MANAGMENT_OUTBOUND_PORT_PREFIX = "avmmop";

    private RessourceManagerInboundPort rmip;

    public RessourceManager(List<String> initialComputerUri,
                            List<String> initialComputerServicesInboundPorts,
                            List<String> initialComputerDynamicStateDataInboundPorts,
                            String ressourceManagerInboundPortUri
                            ) throws Exception{
        super(1, 1);
        addOfferedInterface(RessourceManagerI.class);
        addRequiredInterface(ComputerServicesI.class);
        addRequiredInterface(ControlledDataRequiredI.ControlledPullI.class);
        addRequiredInterface(RequestDispatcherManagerI.class);
        rmip = new RessourceManagerInboundPort(ressourceManagerInboundPortUri, this);
        this.addPort(rmip);
        rmip.publishPort();

        assert initialComputerUri.size() == initialComputerDynamicStateDataInboundPorts.size();
        assert initialComputerUri.size() == initialComputerServicesInboundPorts.size();
        for(int i = 0; i < initialComputerUri.size(); i++)
            connectComputer(initialComputerServicesInboundPorts.get(i),
                    initialComputerDynamicStateDataInboundPorts.get(i),
                    initialComputerUri.get(i));
    }

    public String createVM(String rdmipUri, int coreCount) throws Exception{
        if(coreCount == 0)
            throw new Exception("can not create vm with 0 core");
        if(!canCreateVM(coreCount))
            throw new Exception("not enough cores to create vm");
        vmCount++;
        ApplicationVM vm = new ApplicationVM(
                VM_PREFIX + vmCount,
                APPLICATION_VM_MANAGMENT_INBOUND_PORT_PREFIX + vmCount,
                REQUEST_SUBMISSION_INBOUD_PORT_PREFIX + vmCount,
                REQUEST_NOTIFICATION_OUTBOUND_PORT + vmCount
        );
        appVMSubmissionIPToAppVMManIP.put(REQUEST_SUBMISSION_INBOUD_PORT_PREFIX + vmCount, APPLICATION_VM_MANAGMENT_INBOUND_PORT_PREFIX + vmCount);
        vm.toggleTracing();
        vm.toggleLogging();
        vm.start();

        ApplicationVMManagementOutboundPort avmPort = new ApplicationVMManagementOutboundPort(
                APPLICATION_VM_MANAGMENT_OUTBOUND_PORT_PREFIX + vmCount,
                new AbstractComponent() {}) ;
        avmPort.localPublishPort();
        avmPort.doConnection( APPLICATION_VM_MANAGMENT_INBOUND_PORT_PREFIX + vmCount,
                ApplicationVMManagementConnector.class.getCanonicalName()) ;
        avmPort.allocateCores(allocateCores(coreCount, APPLICATION_VM_MANAGMENT_INBOUND_PORT_PREFIX + vmCount));
        // connexion avec les vm
        RequestDispatcherManagerOutboundPort rdmop = new RequestDispatcherManagerOutboundPort(this);
        this.addPort(rdmop);
        rdmop.publishPort();
        rdmop.doConnection(rdmipUri, RequestDispatcherManagerConnector.class.getCanonicalName());
        String requestDispatcherNotificationInboundPort = rdmop.ajouterVM(
                REQUEST_SUBMISSION_INBOUD_PORT_PREFIX + vmCount);
        RequestNotificationOutboundPort rpnop =(RequestNotificationOutboundPort) vm.findPortFromURI(
                REQUEST_NOTIFICATION_OUTBOUND_PORT + vmCount);

        rpnop.doConnection(requestDispatcherNotificationInboundPort,RequestNotificationConnector.class.getCanonicalName()) ;
        rdmop.doDisconnection();
        System.out.println("created new VM with " + coreCount + " core to request dispatcher " + rdmipUri
                + " Total free core count " + freeCoreCount());
        return APPLICATION_VM_MANAGMENT_OUTBOUND_PORT_PREFIX + vmCount;
    }

    @Override
    public void acceptComputerStaticData(String computerURI, ComputerStaticStateI staticState) throws Exception {
        computersStaticState.put(computerURI, staticState);
    }

    @Override
    public void acceptComputerDynamicData(String computerURI, ComputerDynamicStateI currentDynamicState) throws Exception {
        computersDynamicState.put(computerURI, currentDynamicState);
//        System.out.print(computerURI + " : ");
//        for(boolean ba[] : currentDynamicState.getCurrentCoreReservations()){
//            for(boolean b : ba){
//                System.out.print(b + " ");
//            }
//        }
//        System.out.println();
    }

    private Computer.AllocatedCore[] allocateCores(int requestedNumber, String applicationVMManagementInboundPortURI) throws Exception {
        if(computersDynamicState.isEmpty())
            throw new Exception("no computers");
        for(String computerUri : computersDynamicState.keySet()){
            int freeCoreCount = 0;
            for(boolean ba[] : computersDynamicState.get(computerUri).getCurrentCoreReservations()){
                for(boolean b : ba){
                    if(!b) freeCoreCount++;
                    if(freeCoreCount == requestedNumber){
                        Computer.AllocatedCore[] cores = computers.get(computerUri).allocateCores(requestedNumber);
                        System.out.println("allocating " + cores.length + " to vm " + applicationVMManagementInboundPortURI);
                        vmAllocatedCoreMap.put(applicationVMManagementInboundPortURI, cores);
                        vmComputer.put(applicationVMManagementInboundPortURI, computers.get(computerUri));
                        return cores;
                    }
                }
            }
        }
        throw new Exception("the requested core count is currently unavailable");
    }
    public boolean canCreateVM(int coreCount){
        for(String computerUri : computersDynamicState.keySet()){
            int freeCoreCount = 0;
            for(boolean ba[] : computersDynamicState.get(computerUri).getCurrentCoreReservations()){
                for(boolean b : ba){
                        if(!b)
                           freeCoreCount ++;
                        if(freeCoreCount == coreCount)
                            return true;
                }
            }
        }
        return false;
    }

    public int freeCoreCount(){
        int coreCount = 0;
        for(String computerUri : computersDynamicState.keySet()){
            int freeCoreCount = 0;
            for(boolean ba[] : computersDynamicState.get(computerUri).getCurrentCoreReservations()){
                for(boolean b : ba){
                    if(!b)
                        coreCount ++;
                }
            }
        }
        return coreCount;
    }

    public boolean canHandleApplication(int vmCount, int coreCountPerVm){
        for(int i = 0; i < vmCount; i++){
            if(!canCreateVM(coreCountPerVm))
                return false;
        }
        return true;
    }

    public String connectComputer(String csipUri, String cdsdipUri, String computerURI) throws Exception{
        ComputerServicesOutboundPort csop = new ComputerServicesOutboundPort(this);
        ComputerDynamicStateDataOutboundPort cdsdop = new ComputerDynamicStateDataOutboundPort(this, computerURI);
        this.addPort(csop);
        this.addPort(cdsdop);

        if(AbstractCVM.isDistributed){
            csop.publishPort();
            cdsdop.publishPort();
        }else{
            csop.localPublishPort();
            cdsdop.localPublishPort();
        }
        csop.doConnection(csipUri, ComputerServicesConnector.class.getCanonicalName());
        cdsdop.doConnection(cdsdipUri, ControlledDataConnector.class.getCanonicalName());
        System.out.println("connected computer " + computerURI + " Total free core count : " + freeCoreCount());

        computers.put(computerURI, csop);
        computerDynamicStateDataOutboundPortMap.put(computerURI, cdsdop);
        return csop.getPortURI();
    }

    @Override
    public void start() throws ComponentStartException {
        super.start();
        for(String key : computers.keySet()){
            ComputerDynamicStateDataOutboundPort cdsdop = computerDynamicStateDataOutboundPortMap.get(key);
            try {
                cdsdop.startLimitedPushing(1, 10000) ;
            } catch (Exception e) {
                throw new ComponentStartException(
                        "Unable to start the pushing of dynamic data from"
                                + " the comoter component.", e) ;
            }
        }
    }

    public void removeVM(String rdmipURI, String rsipURI) throws Exception{
        RequestDispatcherManagerOutboundPort rdmop = new RequestDispatcherManagerOutboundPort(this);
        rdmop.publishPort();
        this.addPort(rdmop);
        rdmop.doConnection(rdmipURI, RequestGeneratorManagementConnector.class.getCanonicalName());
        rdmop.supprimerVM(rsipURI);
        rdmop.doDisconnection();
        vmComputer.remove(appVMSubmissionIPToAppVMManIP.get(rsipURI));
        vmAllocatedCoreMap.remove(appVMSubmissionIPToAppVMManIP.get(rsipURI));
    }

    public Computer.AllocatedCore[] getAllocatedCores(String vmsipURI) throws Exception{
        return vmAllocatedCoreMap.get(appVMSubmissionIPToAppVMManIP.get(vmsipURI));
    }

    public void updateVMCoresNumber(String vmmipURI, int coreCount) throws Exception{
        ApplicationVMManagementOutboundPort avmop = new ApplicationVMManagementOutboundPort(this);
        avmop.publishPort();
        addPort(avmop);
        avmop.doConnection(vmmipURI, ApplicationVMManagementConnector.class.getCanonicalName());
        avmop.allocateCores(allocateCores(coreCount, vmmipURI));
        avmop.doDisconnection();
    }

    public String createServicePort() throws Exception{
        RessourceManagerInboundPort port = new RessourceManagerInboundPort(this);
        port.publishPort();
        addPort(port);
        return port.getPortURI();
    }
}
