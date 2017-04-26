package br.ufba.dcc.wiser.soft_iot.entities;

import java.sql.Timestamp;
import java.util.Date;

public class SensorData {
	
	private Sensor sensor;
	private String value;
	private Date startTime;
	private Date endTime;
	
	public SensorData(){
		
	}

	public SensorData(Sensor sensor, String value, Date startTime,
			Date endTime) {
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

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}
	

}
