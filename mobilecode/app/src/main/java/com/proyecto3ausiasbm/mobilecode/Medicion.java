package com.proyecto3ausiasbm.mobilecode;

import android.util.Log;

import java.util.Date;

public class Medicion {

        private String id;
        private int medicion;
        private int tipoMedicion;
        private Date fechaHora;
        private double lat;
        private double lng;

    public Medicion(String id, int medicion, int tipoMedicion, Date fechaHora, double lat, double lng) {
        this.id = id;
        this.medicion = medicion;
        this.tipoMedicion = tipoMedicion;
        this.fechaHora = fechaHora;
        this.lat = lat;
        this.lng = lng;
    }

    public Medicion(int medicion, int tipoMedicion, double lat, double lng) {
        this.medicion = medicion;
        this.tipoMedicion = tipoMedicion;
        this.lat = lat;
        this.lng = lng;
    }

    public String getId() {
        return id;
    }

    public int getMedicion() {
        return medicion;
    }

    public int getTipoMedicion() {
        return tipoMedicion;
    }

    public Date getFechaHora() {
        return fechaHora;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    @Override
    public String toString() {
        return "{" +
                "\"medicion\":" +
                "\"" + this.medicion + "\"," +
                "\"tipoMedicion\":" +
                "\"" + this.tipoMedicion + "\"," +
                "\"lat\":" +
                "\"" + this.lat + "\"," +
                "\"lng\":" +
                "\"" + this.lng + "\"" +
                "}";
    }
}
