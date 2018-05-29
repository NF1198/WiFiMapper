/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tauterra.wifimapper.wifimappergui.data;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author nickfolse
 */
public class Network {

    private final Consumer<List<Location>> locationsLoader;
    private String bssid;
    private String ssid;
    private Integer frequency;
    private String capabilities;
    private String type;
    private List<Location> locations = null;

    public Network(Consumer<List<Location>> locationsLoader) {
        this.locationsLoader = locationsLoader;
    }

    public List<Location> getLocations() {
        loadLocations();
        return locations;
    }

    private void loadLocations() {
        if (locations == null) {
            locations = new ArrayList<>();
            locationsLoader.accept(locations);
            locations.forEach(l -> l.setNetwork(this));
        }
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

    public String detailsSummary() {
        MessageFormat fmt = new MessageFormat(""
                + "bssid: {0}\n"
                + "ssid: {1}\n"
                + "type: {2}\n"
                + "frequency: {3}\n"
                + "capabilities: {4}\n"
                + "# of locations: {5}\n"
        );
        return fmt.format(new Object[]{
            getBssid(),
            getSsid(),
            getType(),
            getFrequency(),
            getCapabilities(),
            getLocations().size()
        });

    }
}
