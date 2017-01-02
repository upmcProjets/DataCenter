package fr.upmc.datacenterclient.ressource_manager;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenterclient.ressource_manager.interfaces.RessourceManagerI;

/**
 * Created by Hacene on 12/28/2016.
 */
public class RessourceManagerConnector extends AbstractConnector implements RessourceManagerI {
    @Override
    public String createVM(String requestDispacherManamentInBoundPortUri, int coreCount) throws Exception {
        return ((RessourceManagerI)this.offering).createVM(requestDispacherManamentInBoundPortUri, coreCount);
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
    public Computer.AllocatedCore[] getAllocatedCores(String vmsipURI) throws Exception {
        return ((RessourceManagerI)this.offering).getAllocatedCores(vmsipURI);
    }

    @Override
    public void updateVMCoresNumber(String vmmipURI, int coreCount) throws Exception {
        updateVMCoresNumber(vmmipURI, coreCount);
    }

    @Override
    public String createServicePort() throws Exception {
        return ((RessourceManagerI)this.offering).createServicePort();
    }

    @Override
    public void removeVM(String rdmipURI, String rsipURI) throws Exception {
        ((RessourceManagerI)this.offering).removeVM(rdmipURI, rsipURI);
    }
}
