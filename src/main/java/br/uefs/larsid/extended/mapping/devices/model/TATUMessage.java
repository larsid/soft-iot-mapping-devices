package br.uefs.larsid.extended.mapping.devices.model;

import br.uefs.larsid.extended.mapping.devices.enums.ExtendedTATUMethods;
import br.uefs.larsid.extended.mapping.devices.tatu.ExtendedTATUWrapper;
import br.ufba.dcc.wiser.soft_iot.tatu.TATUWrapper;
import java.util.Optional;
import org.json.JSONObject;

/**
 *
 * @author Uellington Damasceno
 */
public class TATUMessage {

    private String message;
    private ExtendedTATUMethods method;
    private String targetName;
    private Optional<String> content;
    private boolean response;
    private String formatedMessage;

    public TATUMessage(byte[] payload) {
        this(new String(payload));
    }

    public TATUMessage(String message) {
        this.message = message;
        this.method = ExtendedTATUMethods.valueOf(ExtendedTATUWrapper.getMethod(message));
        this.response = ExtendedTATUWrapper.isTATUResponse(message);
        this.targetName = this.getTarget(message);
        this.content = this.findMessageContent(message);
    }

    public ExtendedTATUMethods getMethod() {
        return this.method;
    }

    public String getTarget() {
        return this.targetName;
    }

    public String getMessageContent() {
        return this.content.orElse("");
    }

    public boolean isResponse() {
        return this.response;
    }

    private String getTarget(String message) {
        return (!this.isResponse())
                ? ExtendedTATUWrapper.getSensorIdByTATURequest(message)
                : TATUWrapper.getSensorIdByTATUAnswer(message);
    }

    private Optional<String> findMessageContent(String message) {
        String newMsg = message.replace("\\", "");
        if (this.isResponse()) {
            if (TATUWrapper.isValidTATUAnswer(newMsg)) {
                return Optional.ofNullable(newMsg);
            }
        } else if (method.equals(ExtendedTATUMethods.FLOW)
                || method.equals(ExtendedTATUMethods.SET)
                || method.equals(ExtendedTATUMethods.CONNECT)) {
            return Optional.ofNullable(newMsg.substring(newMsg.indexOf("{")));
        }
        return Optional.ofNullable("");
    }

    @Override
    public String toString() {
        if (this.formatedMessage == null) {
            JSONObject json = new JSONObject();
            json.accumulate("isResponse", this.response);
            json.accumulate("message", this.message);
            json.accumulate("messageContent", this.content.get());
            json.accumulate("sensor", this.targetName);
            json.accumulate("method", this.method);
            this.formatedMessage = json.toString();
        }
        return this.formatedMessage;
    }

}
