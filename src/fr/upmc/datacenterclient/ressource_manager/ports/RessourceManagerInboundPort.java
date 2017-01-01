package fr.upmc.datacenterclient.ressource_manager.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenterclient.ressource_manager.RessourceManager;
import fr.upmc.datacenterclient.ressource_manager.interfaces.RessourceManagerI;

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
    public String createVM(String requestDispacherManamentOutBoundPortUri, final int coreCount) throws Exception {
        final String rdmop = requestDispacherManamentOutBoundPortUri;
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
    public boolean canCreateVM(final int coreCount) throws Exception {
        final RessourceManager manager = (RessourceManager)this.owner;
        return this.owner.handleRequestSync(new ComponentI.ComponentService<Boolean>(){
            @Override
            public Boolean call() throws Exception{
                return manager.canCreateVM(coreCount);
            }
        });
    }

    @Override
    public boolean canHandleApplication(final int vmCount, final int coreCountPerVm) throws Exception {
        final RessourceManager manager = (RessourceManager)this.owner;
        return this.owner.handleRequestSync(new ComponentI.ComponentService<Boolean>(){
            @Override
            public Boolean call() throws Exception{
                return manager.canHandleApplication(vmCount, coreCountPerVm);
            }
        });
    }

	@Override
	public AllocatedCore[] getAllocatedCores(final String vmmipURI) throws Exception {
		final RessourceManager manager = (RessourceManager)this.owner;
        return this.owner.handleRequestSync(new ComponentI.ComponentService<AllocatedCore[]>(){
            @Override
            public AllocatedCore[] call() throws Exception{
                return manager.getAllocatedCores(vmmipURI);
            }
        });
	}

	@Override
	public void updateVMCoresNumber(String vmmipURI, int coreCount) throws Exception {
		((RessourceManager)this.owner).updateVMCoresNumber(vmmipURI, coreCount);
		
	}
}
