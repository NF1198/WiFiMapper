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
public class WigleNetwork {

    private String bssid;
    private String ssid;
    private Integer frequency;
    private String capabilities;
    private Long lasttime;
    private Double lastlat;
    private Double lastlon;
    private String type;
    private Integer bestlevel;
    private Double bestlat;
    private Double bestlon;

    public WigleNetwork() {
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }

    public Long getLasttime() {
        return lasttime;
    }

    public void setLasttime(Long lasttime) {
        this.lasttime = lasttime;
    }

    public Double getLastlat() {
        return lastlat;
    }

    public void setLastlat(Double lastlat) {
        this.lastlat = lastlat;
    }

    public Double getLastlon() {
        return lastlon;
    }

    public void setLastlon(Double lastlon) {
        this.lastlon = lastlon;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getBestlevel() {
        return bestlevel;
    }

    public void setBestlevel(Integer bestlevel) {
        this.bestlevel = bestlevel;
    }

    public Double getBestlat() {
        return bestlat;
    }

    public void setBestlat(Double bestlat) {
        this.bestlat = bestlat;
    }

    public Double getBestlon() {
        return bestlon;
    }

    public void setBestlon(Double bestlon) {
        this.bestlon = bestlon;
    }

    @Override
    public String toString() {
        return "WigleNetwork{" + "bssid=" + bssid + ", ssid=" + ssid + ", frequency=" + frequency + ", capabilities=" + capabilities + ", lasttime=" + lasttime + ", type=" + type + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.bssid);
        hash = 37 * hash + Objects.hashCode(this.ssid);
        hash = 37 * hash + Objects.hashCode(this.frequency);
        hash = 37 * hash + Objects.hashCode(this.capabilities);
        hash = 37 * hash + Objects.hashCode(this.lasttime);
        hash = 37 * hash + Objects.hashCode(this.type);
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
        final WigleNetwork other = (WigleNetwork) obj;
        if (!Objects.equals(this.bssid, other.bssid)) {
            return false;
        }
        if (!Objects.equals(this.ssid, other.ssid)) {
            return false;
        }
        if (!Objects.equals(this.capabilities, other.capabilities)) {
            return false;
        }
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        if (!Objects.equals(this.frequency, other.frequency)) {
            return false;
        }
        if (!Objects.equals(this.lasttime, other.lasttime)) {
            return false;
        }
        return true;
    }

}
