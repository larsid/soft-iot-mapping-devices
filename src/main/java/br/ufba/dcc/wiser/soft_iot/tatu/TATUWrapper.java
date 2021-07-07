package br.ufba.dcc.wiser.soft_iot.tatu;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import br.ufba.dcc.wiser.soft_iot.entities.Device;
import br.ufba.dcc.wiser.soft_iot.entities.Sensor;
import br.ufba.dcc.wiser.soft_iot.entities.SensorData;
import org.json.JSONException;

public final class TATUWrapper {

    public static final String topicBase = "dev/";
    public static final String topicResponse = "/RES";

    public static String getTATUFlowInfo(String sensorId, int collectSeconds, int publishSeconds) {
        return buildTATUFlow(sensorId, collectSeconds, publishSeconds, "INFO ");
    }

    public static String getTATUFlowValue(String sensorId, int collectSeconds, int publishSeconds) {
        return buildTATUFlow(sensorId, collectSeconds, publishSeconds, "VALUE ");
    }

    private static String buildTATUFlow(String sensorId, int collectSeconds, int publishSeconds, String command) {
        return new StringBuilder()
                .append("FLOW ")
                .append(command)
                .append(sensorId)
                .append(" {\"collect\":")
                .append(collectSeconds)
                .append(",\"publish\":")
                .append(publishSeconds)
                .append("}").toString();
    }

    //{"CODE":"POST","METHOD":"FLOW","HEADER":{"NAME":"ufbaino04"},"BODY":{"temperatureSensor":["36","26"],"FLOW":{"publish":10000,"collect":5000}}}
    public static boolean isValidTATUAnswer(String answer) {
        try {
            JSONObject json = new JSONObject(answer);
            return ((json.get("CODE").toString().contentEquals("POST"))
                    && json.getJSONObject("BODY") != null);
        } catch (JSONException e) {
            return false;
        }
    }

    public static String getDeviceIdByTATUAnswer(String answer) {
        JSONObject json = new JSONObject(answer);
        return json.getJSONObject("HEADER").getString("NAME");
    }

    public static String getSensorIdByTATUAnswer(String answer) {
        JSONObject json = new JSONObject(answer);
        Iterator<?> keys = json.getJSONObject("BODY").keys();
        String sensorId = keys.next().toString();
        while (sensorId.contentEquals("FLOW")) {
            sensorId = keys.next().toString();
        }
        return sensorId;
    }

    public static List<SensorData> parseTATUAnswerToListSensorData(String answer, Device device, Sensor sensor, Date baseDate) {
        List<SensorData> listSensorData = new ArrayList();
        String value;
        SensorData sensorData;
        try {
            JSONObject json = new JSONObject(answer);
            JSONArray body = json.getJSONObject("BODY")
                    .getJSONArray(sensor.getId());
            int collectTime = json.getJSONObject("BODY")
                    .getJSONObject("FLOW")
                    .getInt("collect");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(baseDate);
            for (int i = 0; i < body.length(); i++) {
                value = String.valueOf(body.getInt(i));
                sensorData = new SensorData(device, sensor, value, calendar.getTime(), calendar.getTime());
                listSensorData.add(sensorData);
                calendar.add(Calendar.MILLISECOND, collectTime);
            }
        } catch (org.json.JSONException e) {
        }
        return listSensorData;
    }

}
