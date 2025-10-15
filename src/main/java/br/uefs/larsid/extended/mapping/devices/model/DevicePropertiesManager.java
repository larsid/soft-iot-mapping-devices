package br.uefs.larsid.extended.mapping.devices.model;

import br.uefs.larsid.extended.mapping.devices.services.IDevicePropertiesManager;
import br.uefs.larsid.extended.mapping.devices.services.IPIDEditor;
import br.uefs.larsid.extended.mapping.devices.tatu.DeviceWrapper;
import br.ufba.dcc.wiser.soft_iot.entities.Device;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;

/**
 *
 * @author Uellington Damasceno
 */
public class DevicePropertiesManager implements IDevicePropertiesManager {

    private final String mappingDevicePID;
    private final String connected;
    private final String removed;
    private IPIDEditor pidEditor;

    public DevicePropertiesManager(String mappingDevicePID, String connected, String removed) {
        this.mappingDevicePID = mappingDevicePID;
        this.connected = connected;
        this.removed = removed;
    }

    public void setPidEditor(IPIDEditor pidEditor) {
        this.pidEditor = pidEditor;
    }

    @Override
    public void addDevice(Device device) throws IOException {
        Optional<Object> property = pidEditor.getProperty(mappingDevicePID, connected);
        String devicesConnected = (String) property.get();
        JSONArray devices = new JSONArray(devicesConnected);
        devices.put(DeviceWrapper.toJSONObject(device));
        this.pidEditor.updateProperty(mappingDevicePID, connected, devices.toString());
    }

    @Override
    public void addAllDevices(List<Device> devices) throws IOException {
        Optional<Object> property = pidEditor.getProperty(mappingDevicePID, connected);
        String devicesConnected = (String) property.get();
        JSONArray devicesJson = new JSONArray(devicesConnected);
        
        for (Device device : devices) {
            devicesJson.put(DeviceWrapper.toJSONObject(device));
        }
        
        this.pidEditor.updateProperty(mappingDevicePID, connected, devicesJson.toString());
    }

    @Override
    public List<Device> getAllDevices() throws IOException {
        return this.getDevicesInPIDFile(connected);
    }

    @Override
    public Optional<Device> getDevice(String deviceId) throws IOException {
        return this.getAllDevices().stream()
                .filter(device -> device.getId().equals(deviceId))
                .findAny();
    }

    @Override
    public void updateDevice(String deviceId, Device device) throws IOException {
        List<Device> devices = this.getDevicesInPIDFile(connected);
        if (!devices.isEmpty()) {
            devices.stream()
                    .filter(currentDevice -> !currentDevice.getId().equals(deviceId))
                    .collect(Collectors.toList());
        }
        devices.add(device);
        String jsonArray = new JSONArray(devices).toString();
        this.pidEditor.updateProperty(mappingDevicePID, connected, jsonArray);
    }

    @Override
    public boolean removeDevice(String deviceId) throws IOException {
        Optional<Map<String, Object>> optProperties = this.pidEditor.getProperties(mappingDevicePID);
        if (!optProperties.isPresent()) {
            return false;
        }
        Map<String, Object> properties = optProperties.get();
        String sDevice = (String) properties.getOrDefault(connected, "");
        if (sDevice.isEmpty()) {
            return false;
        }
        List<Device> connectedDevices = DeviceWrapper.getAllDevices(sDevice);
        if (connectedDevices.isEmpty()) {
            return false;
        }
        Optional<Device> optDevice = connectedDevices.stream()
                .filter(device -> device.getId().equals(deviceId))
                .findAny();
        if (!optDevice.isPresent()) {
            return false;
        }
        Device device = optDevice.get();
        boolean wasRemoved = connectedDevices.remove(device);
        if (!wasRemoved) {
            return false;
        }
        sDevice = (String) properties.getOrDefault(removed, "");
        List<Device> removedDevices = sDevice.isEmpty()
                ? new ArrayList(1)
                : DeviceWrapper.getAllDevices(sDevice);
        removedDevices.add(device);
        String connectedUpdated = new JSONArray(connectedDevices).toString();
        String removedUpdated = new JSONArray(removedDevices).toString();

        properties = Stream.of(new Object[][]{
            {connected, connectedUpdated},
            {removed, removedUpdated},}).collect(Collectors.toMap(data -> (String) data[0], data -> (Object) data[1]));

        this.pidEditor.updateProperties(mappingDevicePID, properties);
        return wasRemoved;
    }

    @Override
    public void removeAll() throws IOException {
        String emptyJSONArray = new JSONArray().toString();
        try {
            pidEditor.updateProperty(mappingDevicePID, connected, emptyJSONArray);
        } catch (IOException ex) {
            Logger.getLogger(DevicePropertiesManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private List<Device> getDevicesInPIDFile(String property) throws IOException {
        try {
            Optional<Object> stringOptional = this.pidEditor.getProperty(mappingDevicePID, property);
            if (stringOptional.isPresent()) {
                String array = (String) stringOptional.get();
                return DeviceWrapper.getAllDevices(array);
            }
        } catch (IOException ex) {
            Map<String, Object> properties = new HashMap();
            String emptyJSONArray = new JSONArray().toString();
            properties.put(connected, emptyJSONArray);
            properties.put(removed, emptyJSONArray);
            properties.put("debugMode", false);
            pidEditor.createPIDFile(mappingDevicePID, properties);
        }
        return new ArrayList();
    }
}
