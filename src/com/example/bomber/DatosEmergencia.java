package com.example.bomber;

import java.io.Serializable;

public class DatosEmergencia implements Serializable {
	protected int emergencia_id;
	protected String lugar;
	protected String tipo;
	protected String telefono;
	protected Double latitude;
	protected Double longitude;
	private int imagen;
	public DatosEmergencia(){
	    this (0,"", "", "", 0.0, 0.0);
	}
     
	public DatosEmergencia(int emergencia_id,String lugar, String tipo,String telefono, Double latitude, Double longitude){
		this.emergencia_id = emergencia_id;
		this.lugar =lugar;
		this.tipo = tipo;
		this.telefono = telefono;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	public String getLugar(){
		return lugar;
	}
	public String getTipo(){
		return tipo;
	}
	public String getTelefono(){
		return telefono;
	}
	public Double getLatitude(){
		return latitude;
	}
	public Double getLongitude(){
		return longitude;
	}
	public int getImagen(){
		return imagen;
	}
	public void setImagen(int imagen){
        this.imagen=imagen;
    }
	public int getEmergencia_id(){
		return emergencia_id;
	}
	
	@Override
	public String toString(){return lugar+":"+telefono;}
}

