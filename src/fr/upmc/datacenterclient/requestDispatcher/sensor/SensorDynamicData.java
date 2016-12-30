package fr.upmc.datacenterclient.requestDispatcher.sensor;

public class SensorDynamicData 
implements SensorDynamicDataI {
	
	
	private static final long serialVersionUID = -1737921338555249506L;
	protected String sensorURI;
	protected long meanTime ;
	
	
	

	/**
	 * @param sensorURI
	 * @param meanTime
	 */
	public SensorDynamicData(String sensorURI, long meanTime) {
		super();
		this.sensorURI = sensorURI;
		this.meanTime = meanTime;
	}

	@Override
	public String getSensorUri() {
		
		return this.sensorURI;
	}

	@Override
	public long getMeanTime() {
		
		return this.meanTime;
	}

}
