package br.ufba.dcc.wiser.soft_iot.mapping_devices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufba.dcc.wiser.soft_iot.entities.Device;
import br.ufba.dcc.wiser.soft_iot.entities.Sensor;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerImpl implements Controller, ManagedService {

    private List<Device> listDevices = new CopyOnWriteArrayList<>();
    private boolean debugModeValue;
    private static final Logger logger = Logger.getLogger(ControllerImpl.class.getName());

    public void init() {
        logger.info("ControllerImpl inicializado. Aguardando configurações...");
        this.debugModeValue = this.readDebugModeValue();
    }

    @Override
    public void updated(Dictionary<String, ?> properties) throws ConfigurationException {
        if (properties == null) {
            printlnDebug("Nenhuma configuração encontrada. Limpando lista de dispositivos.");
            this.listDevices.clear();
            this.debugModeValue = false;
            return;
        }

        logger.info("Configuração recebida. Atualizando mapeamento de dispositivos...");

        Object debugProp = properties.get("debugMode");
        this.debugModeValue = Boolean.parseBoolean(String.valueOf(debugProp));
        printlnDebug("Modo debug definido para: " + this.debugModeValue);

        Object devicesProp = properties.get("DevicesConnected");
        if (devicesProp instanceof String) {
            loadConnectedDevices((String) devicesProp);
        } else {
            printlnDebug("Propriedade 'DevicesConnected' não encontrada ou não é uma String. Nenhuma alteração feita.");
        }
    }

    private void loadConnectedDevices(String strDevices) {
        // Cria uma lista temporária para evitar alterar a lista principal durante o parsing
        List<Device> tempListDevices = new ArrayList<>();
        try {
            JSONArray jsonArrayDevices = new JSONArray(strDevices);
            for (int i = 0; i < jsonArrayDevices.length(); i++) {
                JSONObject jsonDevice = jsonArrayDevices.getJSONObject(i);
                ObjectMapper mapper = new ObjectMapper();
                Device device = mapper.readValue(jsonDevice.toString(), Device.class);
                tempListDevices.add(device);

                List<Sensor> listSensors = new ArrayList<>();
                if (jsonDevice.has("sensors")) {
                    JSONArray jsonArraySensors = jsonDevice.getJSONArray("sensors");
                    for (int j = 0; j < jsonArraySensors.length(); j++) {
                        JSONObject jsonSensor = jsonArraySensors.getJSONObject(j);
                        Sensor sensor = mapper.readValue(jsonSensor.toString(), Sensor.class);
                        listSensors.add(sensor);
                    }
                }
                device.setSensors(listSensors);
            }
            this.listDevices.clear();
            this.listDevices.addAll(tempListDevices);
            printlnDebug("Mapeamento de dispositivos atualizado com sucesso. Total: " + this.listDevices.size());

        } catch (JsonParseException | JsonMappingException e) {
            logger.severe("Erro de parsing no JSON. Verifique o formato da propriedade 'DevicesConnected' no arquivo de configuração.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Device getDeviceById(String deviceId) {
        for (Device device : listDevices) {
            if (device.getId().contentEquals(deviceId)) {
                return device;
            }
        }
        return null;
    }

    private void printlnDebug(String str) {
        if (debugModeValue) {
            logger.log(Level.INFO, "[Mapping-Devices-DEBUG] {0}", str);
        }
    }

    @Override
    public List<Device> getListDevices() {
        return this.listDevices;
    }

    private boolean readDebugModeValue() {
        String debugModeStr = System.getenv("MAPPING_DEBUG_VALUE");
        if (debugModeStr == null) {
            logger.warning("Mepping device debug mode are not defined.");
            return false;
        } else {
            return Boolean.parseBoolean(debugModeStr);
        }
    }
}
