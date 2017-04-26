package br.ufba.dcc.wiser.soft_iot.entities;

import java.sql.Timestamp;

public class SensorData {
	
	private Sensor sensor;
	private String value;
	private Timestamp startTime;
	private Timestamp endTime;
	
	public SensorData(){
		
	}

	public SensorData(Sensor sensor, String value, Timestamp startTime,
			Timestamp endTime) {
		super();
		this.sensor = sensor;
		this.value = value;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public Sensor getSensor() {
		return sensor;
	}

	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}
	

}
