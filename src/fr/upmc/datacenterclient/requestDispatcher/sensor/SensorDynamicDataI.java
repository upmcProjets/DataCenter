/**
 * 
 */
package fr.upmc.datacenterclient.requestDispatcher.sensor;

import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.components.interfaces.DataRequiredI ;

/**
 * @author chelbi
 *
 */
public interface SensorDynamicDataI 
extends DataOfferedI.DataI,
		DataRequiredI.DataI {
	
	public String getSensorUri();
	
	public long getMeanTime();

}
