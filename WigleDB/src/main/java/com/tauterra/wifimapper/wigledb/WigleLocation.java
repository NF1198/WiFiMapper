/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tauterra.wifimapper.wigledb;

import java.util.Objects;

/**
 *
 * @author nickfolse
 */
public class WigleLocation {
    private Long id;
    private String bssid;
    private Integer level;
    private Double lat;
    private Double lon;
    private Float altitude;
    private Double accuracy;
    private Long time;

    public WigleLocation() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "WigleLocation{" + "id=" + id + ", bssid=" + bssid + ", level=" + level + ", lat=" + lat + ", lon=" + lon + ", altitude=" + altitude + ", accuracy=" + accuracy + ", time=" + time + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.id);
        hash = 89 * hash + Objects.hashCode(this.bssid);
        hash = 89 * hash + Objects.hashCode(this.level);
        hash = 89 * hash + Objects.hashCode(this.lat);
        hash = 89 * hash + Objects.hashCode(this.lon);
        hash = 89 * hash + Objects.hashCode(this.altitude);
        hash = 89 * hash + Objects.hashCode(this.accuracy);
        hash = 89 * hash + Objects.hashCode(this.time);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WigleLocation other = (WigleLocation) obj;
        if (!Objects.equals(this.bssid, other.bssid)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.level, other.level)) {
            return false;
        }
        if (!Objects.equals(this.lat, other.lat)) {
            return false;
        }
        if (!Objects.equals(this.lon, other.lon)) {
            return false;
        }
        if (!Objects.equals(this.altitude, other.altitude)) {
            return false;
        }
        if (!Objects.equals(this.accuracy, other.accuracy)) {
            return false;
        }
        if (!Objects.equals(this.time, other.time)) {
            return false;
        }
        return true;
    }
    
    
}
