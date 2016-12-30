package fr.upmc.datacenterclient.ressource_manager.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenterclient.ressource_manager.interfaces.RessourceManagerI;

/**
 * Created by Hacene on 12/28/2016.
 */
public class RessourceManagerConnector extends AbstractConnector implements RessourceManagerI {
    @Override
    public String createVM(String requestDispacherManamentOutBoundPortUri, int coreCount) throws Exception {
        return ((RessourceManagerI)this.offering).createVM(requestDispacherManamentOutBoundPortUri, coreCount);
    }

    @Override
    public String connectComputer(String csipUri, String cdsdipUri, String computerURI) throws Exception {
        return ((RessourceManagerI)this.offering).connectComputer(csipUri, cdsdipUri, computerURI);
    }

    @Override
    public Boolean canCreateVM(int coreCount) throws Exception {
        return ((RessourceManagerI)this.offering).canCreateVM(coreCount);
    }

    @Override
    public Boolean canHandleApplication(int vmCount, int coreCountPerVm) throws Exception {
        return ((RessourceManagerI)this.offering).canHandleApplication(vmCount, coreCountPerVm);
    }

	@Override
	public AllocatedCore[] getAllocatedCores(String vmmipURI) throws Exception {
		return ((RessourceManagerI)this.offering).getAllocatedCores(vmmipURI);
	}

	@Override
	public void updateVMCoresNumber(String vmmipURI, int coreCount) throws Exception {
		((RessourceManagerI)this.offering).updateVMCoresNumber(vmmipURI, coreCount);
	}
}
