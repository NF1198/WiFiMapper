/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tauterra.wifimapper.wifimappergui.data;

import com.tauterra.geo.Coordinate;

/**
 *
 * @author nickfolse
 */
public class Location {

    private Network network;
    private String bssid;
    private Integer level;
    private Double lat;
    private Double lon;
    private Float altitude;
    private Double accuracy;
    private Long time;
    private Coordinate localCoord = null;

    public Location() {
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Float getAltitude() {
        return altitude;
    }

    public void setAltitude(Float altitude) {
        this.altitude = altitude;
    }

    public Double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Double accuracy) {
        this.accuracy = accuracy;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Network getNetwork() {
        return network;
    }

    void setNetwork(Network network) {
        this.network = network;
    }

    public Coordinate getLocalCoord() {
        return localCoord;
    }

    public void setLocalCoord(Coordinate localCoord) {
        this.localCoord = localCoord;
    }

}
