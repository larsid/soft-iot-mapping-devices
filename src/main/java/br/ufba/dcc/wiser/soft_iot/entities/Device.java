package br.ufba.dcc.wiser.soft_iot.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.LinkedList;
import java.util.Optional;

public class Device {

    private String id;
    private double latitude;
    private double longitude;
    @JsonIgnoreProperties("device")
    private List<Sensor> sensors;

    public Device() {
        this("", 0, 0, new LinkedList());
    }

    public Device(String id, double longitude, double latitude, List<Sensor> sensors) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.sensors = sensors;
    }

    public Sensor getSensorbySensorId(String sensorId) {
        Optional<Sensor> optSensor = sensors.stream()
                .filter(sensor -> sensor.getId().equals(sensorId))
                .findAny();
        return optSensor.isPresent() ? optSensor.get() : null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }

}
