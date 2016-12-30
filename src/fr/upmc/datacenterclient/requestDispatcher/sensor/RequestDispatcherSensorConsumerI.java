package fr.upmc.datacenterclient.requestDispatcher.sensor;

/**
 * @author chelbi
 *
 */
public interface RequestDispatcherSensorConsumerI {
	
	public void	 acceptRequestDispatcherDynamicData(
			String					monitorURI,
			SensorDynamicDataI	currentDynamicData
	) throws Exception ;

}
