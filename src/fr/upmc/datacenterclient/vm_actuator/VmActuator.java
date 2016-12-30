package fr.upmc.datacenterclient.vm_actuator;

import fr.upmc.components.AbstractComponent;
import fr.upmc.datacenterclient.requestDispatcher.connectors.RequestDispatcherManagerConnector;
import fr.upmc.datacenterclient.requestDispatcher.interfaces.RequestDispatcherManagerI;
import fr.upmc.datacenterclient.requestDispatcher.ports.RequestDispatcherManagerOutboundPort;
import fr.upmc.datacenterclient.ressource_manager.connectors.RessourceManagerConnector;
import fr.upmc.datacenterclient.ressource_manager.interfaces.RessourceManagerI;
import fr.upmc.datacenterclient.ressource_manager.ports.RessourceManagerOutboundPort;
import fr.upmc.datacenterclient.vm_actuator.interfaces.VmActuatorI;
import fr.upmc.datacenterclient.vm_actuator.ports.VmActuatorInboundPort;

/**
 * Created by Hacene on 12/28/2016.
 */
public class VmActuator extends AbstractComponent {
    private RessourceManagerOutboundPort rmop;
    private RequestDispatcherManagerOutboundPort rdmop;

    private String vaipUri = "vaip_uri_1";

    public VmActuator(
            String ressourceManagerInboundPortUri,
            String requestDispatcherManagerInboundPortUri,
            String vaipUri) throws Exception {
        super(1, 1);
        addOfferedInterface(VmActuatorI.class);
        addRequiredInterface(RessourceManagerI.class);
        addRequiredInterface(RequestDispatcherManagerI.class);
        this.vaipUri = vaipUri;

        VmActuatorInboundPort vaip = new VmActuatorInboundPort(vaipUri, this);
        this.addPort(vaip);
        vaip.publishPort();

        rmop = new RessourceManagerOutboundPort(this);
        rmop.publishPort();
        this.addPort(rmop);
        rmop.doConnection(ressourceManagerInboundPortUri, RessourceManagerConnector.class.getCanonicalName());

        rdmop = new RequestDispatcherManagerOutboundPort(this);
        rdmop.publishPort();
        this.addPort(rdmop);
        rdmop.doConnection(requestDispatcherManagerInboundPortUri, RequestDispatcherManagerConnector.class.getCanonicalName());
    }

    public String removeVM(String requestSubmissionOutboundPort) throws Exception{
        rdmop.supprimerVM(requestSubmissionOutboundPort);
        return "";
    }

    public String addVM(int coreCount) throws Exception{
        if(!rmop.canCreateVM(coreCount))
            throw new Exception("no enough available resources to create vm");
        return rmop.createVM(rdmop.getServerPortURI(), coreCount);
    }
}
