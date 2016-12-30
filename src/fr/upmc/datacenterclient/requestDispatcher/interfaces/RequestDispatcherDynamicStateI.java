/**
 * 
 */
package fr.upmc.datacenterclient.requestDispatcher.interfaces;

import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.components.interfaces.DataRequiredI;

/**
 * @author chelbi
 *
 */
public interface RequestDispatcherDynamicStateI 
extends DataOfferedI.DataI,
		DataRequiredI.DataI {
	
	
	public String		getDispatcherURI() ; 
	
	public long   getTimeRequest();

}
