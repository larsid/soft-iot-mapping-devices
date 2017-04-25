package br.ufba.dcc.wiser.soft_iot.tatu;

import org.json.JSONObject;



public final class Wrapper {
	
	public static String topicBase = "dev/";
	
	public static String getTATUFlow(String sensorId, int collectSeconds, int publishSeconds){
		String msgStr = "FLOW " + "INFO " + sensorId + " {collect:" + collectSeconds + ",publish:" + publishSeconds + "}";
		
		return msgStr;
	}
	
	//{"CODE":"POST","METHOD":"FLOW","HEADER":{"NAME":"ufbaino04"},"BODY":{"temperatureSensor":["36","26"],"FLOW":{"publish":10000,"collect":5000}}}
	public static boolean isValidTATUAnswer(String answer){
		try{
			JSONObject json = new JSONObject(answer);
			if ((json.get("CODE").toString().contentEquals("POST"))
					&& json.getJSONObject("BODY") != null) {
				return true;
			}
		} catch (org.json.JSONException e) {
		}
		return false;
	}

}
