package fr.upmc.datacenterclient.ressource_manager.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenterclient.ressource_manager.RessourceManager;
import fr.upmc.datacenterclient.ressource_manager.interfaces.RessourceManagerI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hacene on 12/28/2016.
 */
public class RessourceManagerInboundPort extends AbstractInboundPort implements RessourceManagerI {
    private static final long serialVersionUID = 5685810782695264978L;

    public RessourceManagerInboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, RessourceManagerI.class, owner);
    }

    public RessourceManagerInboundPort(ComponentI owner) throws Exception {
        super(RessourceManagerI.class, owner);
    }

    @Override
    public String createVM(String requestDispacherManamentInBoundPortUri, final int coreCount) throws Exception {
        final String rdmop = requestDispacherManamentInBoundPortUri;
        final RessourceManager manager = (RessourceManager)this.owner;
        return this.owner.handleRequestSync(new ComponentI.ComponentService<String>(){
            @Override
            public String call() throws Exception{
                return manager.createVM(rdmop, coreCount);
            }
        });
    }

    @Override
    public String connectComputer(final String csipUri, final String cdsdipUri, final String computerURI) throws Exception {
        final RessourceManager manager = (RessourceManager)this.owner;
        return this.owner.handleRequestSync(new ComponentI.ComponentService<String>(){
            @Override
            public String call() throws Exception{
                return manager.connectComputer(csipUri, cdsdipUri, computerURI);
            }
        });
    }

    @Override
    public Boolean canCreateVM(final int coreCount) throws Exception {
        final RessourceManager manager = (RessourceManager)this.owner;
        return this.owner.handleRequestSync(new ComponentI.ComponentService<Boolean>(){
            @Override
            public Boolean call() throws Exception{
                return manager.canCreateVM(coreCount);
            }
        });
    }

    @Override
    public Boolean canHandleApplication(final int vmCount, final int coreCountPerVm) throws Exception {
        final RessourceManager manager = (RessourceManager)this.owner;
        return this.owner.handleRequestSync(new ComponentI.ComponentService<Boolean>(){
            @Override
            public Boolean call() throws Exception{
                return manager.canHandleApplication(vmCount, coreCountPerVm);
            }
        });
    }
    @Override
    public Computer.AllocatedCore[] getAllocatedCores(final String vmsipURI) throws Exception {
        final RessourceManager manager = (RessourceManager)this.owner;
        return this.owner.handleRequestSync(new ComponentI.ComponentService<Computer.AllocatedCore[]>(){
            @Override
            public Computer.AllocatedCore[] call() throws Exception{
                return manager.getAllocatedCores(vmsipURI);
            }
        });
    }

    @Override
    public void updateVMCoresNumber(final String vmmipURI, final int coreCount) throws Exception {
        final RessourceManager manager = (RessourceManager)this.owner;
        this.owner.handleRequestSync(new ComponentI.ComponentService<String>() {
            @Override
            public String call() throws Exception {
                manager.updateVMCoresNumber(vmmipURI, coreCount);
                return null;
            }
        });
    }

    @Override
    public String createServicePort() throws Exception {
        final RessourceManager manager = (RessourceManager)this.owner;
        return this.owner.handleRequestSync(new ComponentI.ComponentService<String>(){
            @Override
            public String call() throws Exception{
                return manager.createServicePort();
            }
        });
    }

    @Override
    public void removeVM(final String rdmipURI, final String rsipURI) throws Exception {
        final RessourceManager manager = (RessourceManager)this.owner;
        this.owner.handleRequestSync(new ComponentI.ComponentService<String>() {
            @Override
            public String call() throws Exception {
                manager.removeVM(rdmipURI, rsipURI);
                return null;
            }
        });
    }

}
