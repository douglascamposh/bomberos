package com.example.bomber;

public class DatosHospital {
	protected String hospital;
	protected String nivel;
	protected String tipo;
	protected String direccion;
	protected int telefono;
	protected Double latitude;
	protected Double longitude;
	public DatosHospital(){
	    this ("", "", "","",0, 0.0, 0.0);
	}
     
	public DatosHospital(String hospital, String nivel,String tipo,String direccion,int telefono, Double latitude, Double longitude){
		this.hospital =hospital;
		this.nivel = nivel;
		this.tipo = tipo;
		this.direccion = direccion;
		this.telefono = telefono;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	public String getHospital(){
		return hospital;
	}
	public String getNivel(){
		return nivel;
	}
	public String getTipo(){
		return tipo;
	}
	public String getDireccion(){
		return direccion;
	}
	public int getTelefono(){
		return telefono;
	}
	public double getLatitude(){
		return latitude;
	}
	public double getLongitude(){
		return longitude;
	}
	
}
