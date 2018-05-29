/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tauterra.wifimapper.wifimappergui;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author nickfolse
 */
public final class PersistentPreferences {

    public static final String PROP_FILE = "wifi_mapper.prefs";
    public static final String PROP_FOLDER = ".config";

    private final ObservableList<String> recentFiles = FXCollections.observableArrayList();

    private final Properties props;

    public PersistentPreferences() {
        this.props = new Properties();
        load();
    }

    public static PersistentPreferences getInstance() {
        return INSTANCE_HOLDER.INSTANCE;
    }

    private static class INSTANCE_HOLDER {

        private static final PersistentPreferences INSTANCE = new PersistentPreferences();
    }

    private void load() {
        try {
            String user_home = System.getProperty("user.home");
            Path prop_folder_path = Paths.get(user_home, PROP_FOLDER);
            Path prop_file_path = prop_folder_path.resolve(PROP_FILE);
            Files.createDirectories(prop_folder_path);
            try {
                Files.createFile(prop_file_path);
            } catch (FileAlreadyExistsException e) {
                // ignore
            }
            props.load(new FileInputStream(prop_file_path.toFile()));
        } catch (IOException ex) {
        }

        MessageFormat recentProp = new MessageFormat("recent{0}");
        for (int idx = 0; idx < 10; idx++) {
            String value = props.getProperty(recentProp.format(new Object[]{idx}));
            if (value == null) {
                break;
            }
            recentFiles.add(value);
        }
    }

    public void persist() {
        try {
            String user_home = System.getProperty("user.home");
            Path prop_folder_path = Paths.get(user_home, PROP_FOLDER);
            Path prop_file_path = prop_folder_path.resolve(PROP_FILE);
            Files.createDirectories(prop_folder_path);
            props.store(new FileOutputStream(prop_file_path.toFile()), "user preferences");
            Logger.getLogger(PersistentPreferences.class.getName()).log(Level.INFO, "Stored user preferences: {0}", prop_file_path.toString());
        } catch (IOException ex) {
            Logger.getLogger(PersistentPreferences.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ObservableList<String> recentFiles() {
        return recentFiles;
    }

    public void addRecentFile(String file_name) {
        if (recentFiles.contains(file_name)) {
            return;
        }
        if (recentFiles.size() > 10) {
            recentFiles.remove(recentFiles.size() - 1);
        }
        recentFiles.add(0, file_name);
        updateRecentFileProps();
    }

    public void clearRecentFiles() {
        recentFiles.clear();
        updateRecentFileProps();
    }

    private void updateRecentFileProps() {
        MessageFormat recentProp = new MessageFormat("recent{0}");
        for (int idx = 0; idx < 10; idx++) {
            String propId = recentProp.format(new Object[]{idx});
            if (idx < recentFiles.size()) {
                props.setProperty(propId, recentFiles.get(idx));
            } else {
                props.remove(propId);
            }
        }
    }

    public String getGoogleMapsAPIKey() {
        return props.getProperty("GoogleMapsAPIKey", "");
    }

    public void setGoogleMapsAPIKey(String value) {
        props.setProperty("GoogleMapsAPIKey", value);
    }

}
