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
public class NetworkSummary {

    private String bssid;
    private String ssid;
    private Integer frequency;
    private String capabilities;
    private String type;

    public NetworkSummary() {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "NetworkSummary{" + "bssid=" + bssid + ", ssid=" + ssid + ", frequency=" + frequency + ", capabilities=" + capabilities + ", type=" + type + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + Objects.hashCode(this.bssid);
        hash = 19 * hash + Objects.hashCode(this.ssid);
        hash = 19 * hash + Objects.hashCode(this.frequency);
        hash = 19 * hash + Objects.hashCode(this.capabilities);
        hash = 19 * hash + Objects.hashCode(this.type);
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
        final NetworkSummary other = (NetworkSummary) obj;
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
        return true;
    }

    
    
}
