package br.ufba.dcc.wiser.soft_iot.tatu;


import br.ufba.dcc.wiser.soft_iot.entities.Sensor;

public final class Wrapper {
	
	public static String getTATUFlow(Sensor sensor, int collectSeconds, int publishSeconds){
		String msgStr = "FLOW " + "INFO " + sensor.getId() + " {collect:" + collectSeconds + ",publish:" + publishSeconds + "}";
		
		return msgStr;
	}

}
