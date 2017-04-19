package br.ufba.dcc.wiser.soft_iot.entities;

public class Sensor {
	private String id;
	private String type;
	private Device device;
	
	public Sensor(){
		
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Device getDevice() {
		return device;
	}
	public void setDevice(Device device) {
		this.device = device;
	}
	
	
}
