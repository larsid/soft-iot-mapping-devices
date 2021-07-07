package br.uefs.larsid.extended.mapping.devices.tatu;

import br.ufba.dcc.wiser.soft_iot.entities.Device;
import br.uefs.larsid.extended.mapping.devices.enums.ExtendedTATUMethods;
import br.ufba.dcc.wiser.soft_iot.tatu.TATUWrapper;
import org.json.JSONObject;

/**
 *
 * @author Uellington Damasceno
 */
public final class ExtendedTATUWrapper {

    public static final String TOPIC_BASE = "dev/";
    public static final String TOPIC_RESPONSE = "/RES";

    //"dev/CONNECTIONS"
    public static String getConnectionTopic() {
        return new StringBuilder()
                .append(TOPIC_BASE)
                .append("CONNECTIONS")
                .toString();
    }

    //"dev/CONNECTIONS/RES"
    public static String getConnectionTopicResponse() {
        return getConnectionTopic().concat(TOPIC_RESPONSE);
    }

    //CONNECT VALUE BROKER {"HEADER":{"NAME":String}, DEVICE:{id:String, sensors[]:Sensor, latitude:long, longitude:long} "TIME_OUT":Double}
    public static String buildConnectMessage(Device device, Double timeOut) {
        JSONObject requestBody = new JSONObject();
        JSONObject header = new JSONObject();

        requestBody.put("TIME_OUT", timeOut);
        header.put("NAME", device.getId());
        requestBody.put("HEADER", header);
        requestBody.put("DEVICE", DeviceWrapper.toJSONObject(device));
        return new StringBuilder()
                .append("CONNECT VALUE BROKER ")
                .append(requestBody.toString())
                .toString();
    }

    //{"CODE":"POST", "METHOD":"CONNACK", "HEADER":{"NAME":String}, "BODY":{"NEW_NAME":String, "CAN_CONNECT":Boolean}}
    public static String buildConnackMessage(String deviceName, String newDeviceName, boolean sucess) {
        JSONObject header = new JSONObject();
        JSONObject body = new JSONObject();
        JSONObject response = new JSONObject();

        header.put("NAME", deviceName);
        body.put("NEW_NAME", newDeviceName);
        body.put("CAN_CONNECT", sucess);

        response.put("METHOD", "CONNACK");
        response.put("CODE", "POST");
        response.put("HEADER", header);
        response.put("BODY", body);

        return response.toString();
    }

    public static String buildTATUTopic(String deviceName) {
        return new StringBuilder()
                .append(TOPIC_BASE)
                .append(deviceName)
                .toString();
    }

    public static String buildTATUResponseTopic(String deviceName) {
        return new StringBuilder()
                .append(TOPIC_BASE)
                .append(deviceName)
                .append(TOPIC_RESPONSE)
                .toString();
    }

    public static String buildGetMessageResponse(String deviceName, String sensorName, Object value) {
        JSONObject response = new JSONObject();
        JSONObject header = new JSONObject();
        JSONObject body = new JSONObject();

        header.put("NAME", deviceName);
        body.put(sensorName, value);
        response.put("METHOD", "GET");
        response.put("CODE", "POST");
        response.put("HEADER", header);
        response.put("BODY", body);

        return response.toString();
    }

    public static String buildFlowMessageResponse(String deviceName, String sensorName, int publish, int collect, Object[] values) {
        JSONObject response = new JSONObject();
        JSONObject header = new JSONObject();
        JSONObject body = new JSONObject();
        JSONObject flow = new JSONObject();

        flow.put("collect", collect);
        flow.put("publish", publish);

        header.put("NAME", deviceName);

        body.put(sensorName, values);
        body.put("FLOW", flow);

        response.put("METHOD", "FLOW");
        response.put("CODE", "POST");
        response.put("HEADER", header);
        response.put("BODY", body);

        return response.toString();
    }

    public static boolean isTATUResponse(String message) {
        return isValidMessage(message)
                && TATUWrapper.isValidTATUAnswer(message);
    }

    public static boolean isValidMessage(String message) {
        return !(message == null || message.isEmpty() || message.length() <= 10);
    }

    public static String getMethod(String message) {
        if (isValidMessage(message)) {
            if (TATUWrapper.isValidTATUAnswer(message)) {
                return new JSONObject(message).getString("METHOD");
            } else {
                return message.split(" ")[0];
            }
        }
        return ExtendedTATUMethods.INVALID.name();
    }

    public static String getSensorIdByTATURequest(String request) {
        return request.split(" ")[2];
    }
}
