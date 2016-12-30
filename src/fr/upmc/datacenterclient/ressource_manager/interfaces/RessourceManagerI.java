package fr.upmc.datacenterclient.ressource_manager.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;
import fr.upmc.datacenter.hardware.computers.Computer;

/**
 * Created by Hacene on 12/28/2016.
 */
public interface RessourceManagerI extends OfferedI, RequiredI {
    String createVM(String requestDispacherManamentOutBoundPortUri, int coreCount) throws Exception;
    String connectComputer(String csipUri, String cdsdipUri, String computerURI) throws Exception;
    Boolean canCreateVM(int coreCount) throws Exception;
    Boolean canHandleApplication(int vmCount, int coreCountPerVm) throws Exception;
    Computer.AllocatedCore[] getAllocatedCores(String vmmipURI) throws Exception;
    void updateVMCoresNumber(String vmmipURI, int coreCount) throws Exception;
        
}
