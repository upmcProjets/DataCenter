package fr.upmc.datacenterclient.requestDispatcher.sensor;

public class SensorDynamicData 
implements SensorDynamicDataI {
	
	
	private static final long serialVersionUID = -1737921338555249506L;
	protected String sensorURI;
	protected double meanTime ;
	
	
	

	/**
	 * @param sensorURI
	 * @param meanTime
	 */
	public SensorDynamicData(String sensorURI, double meanTime) {
		super();
		this.sensorURI = sensorURI;
		this.meanTime = meanTime;
	}

	@Override
	public String getSensorUri() {
		
		return this.sensorURI;
	}

	@Override
	public double getMeanTime() {
		
		return this.meanTime;
	}

}
