package fr.upmc.datacenterclient.ressource_manager.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenterclient.ressource_manager.RessourceManager;
import fr.upmc.datacenterclient.ressource_manager.interfaces.RessourceManagerI;

/**
 * Created by Hacene on 12/28/2016.
 */
public class RessourceManagerOutboundPort extends AbstractOutboundPort implements RessourceManagerI {
    private static final long serialVersionUID = 5685810782696564978L;

    public RessourceManagerOutboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, RessourceManagerI.class, owner);
    }

    public RessourceManagerOutboundPort(ComponentI owner) throws Exception {
        super(RessourceManagerI.class, owner);
    }

    @Override
    public String createVM(String requestDispacherManamentOutBoundPortUri, int coreCount) throws Exception {
        return ((RessourceManagerI)this.connector).createVM(requestDispacherManamentOutBoundPortUri, coreCount);
    }

    @Override
    public String connectComputer(String csipUri, String cdsdipUri, String computerURI) throws Exception {
        return ((RessourceManager)this.connector).connectComputer(csipUri, cdsdipUri, computerURI);
    }

    @Override
    public Boolean canCreateVM(int coreCount) throws Exception {
        return ((RessourceManager)this.connector).canCreateVM(coreCount);
    }

    @Override
    public Boolean canHandleApplication(int vmCount, int coreCountPerVm) throws Exception {
        return ((RessourceManager)this.connector).canHandleApplication(vmCount, coreCountPerVm);
    }

	@Override
	public AllocatedCore[] getAllocatedCores(String vmmipURI) throws Exception {
		return ((RessourceManager)this.connector).getAllocatedCores(vmmipURI);
		   
	}

	@Override
	public void updateVMCoresNumber(String vmmipURI, int coreCount) throws Exception {
		((RessourceManager)this.connector).updateVMCoresNumber(vmmipURI, coreCount);
		   
	}
}
