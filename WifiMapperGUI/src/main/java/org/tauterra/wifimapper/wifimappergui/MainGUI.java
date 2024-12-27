/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tauterra.wifimapper.wifimappergui;

import com.tauterra.wifimapper.wigledb.WigleDBO;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.tauterra.wifimapper.wifimappergui.app.NetworkWiFiAnalyzer;
import org.tauterra.wifimapper.wifimappergui.app.NetworkWiFiAnalyzerJSHelper;
import org.tauterra.wifimapper.wifimappergui.data.Location;
import org.tauterra.wifimapper.wifimappergui.data.Network;

/**
 * FXML Controller class
 *
 * @author nickfolse
 */
public class MainGUI implements Initializable {

    public static MainGUI loadInstance() throws IOException {
        FXMLLoader loader = new FXMLLoader(MainGUI.class.getResource("/fxml/MainGUI.fxml"));
        loader.load();
        MainGUI controller = loader.getController();
        return controller;
    }
    @FXML
    private VBox root;

    private Stage stage;
    @FXML
    private ListView<Network> networkList;

    ObjectProperty<File> openFileProperty = new SimpleObjectProperty<>();

    ObservableList<Network> networks = FXCollections.observableArrayList();

    @FXML
    private TextArea detailsText;

    @FXML
    private WebView webview;
    @FXML
    private ChoiceBox<String> mapStyleChooser;
    @FXML
    private TextField hexSizeChooser;
    @FXML
    private Menu recentFilesMenu;
    @FXML
    private Label leftStatus;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        networkList.setCellFactory(row -> new ListCell<Network>() {
            @Override
            protected void updateItem(Network item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getBssid() == null) {
                    setText(null);
                } else {
                    setText(MessageFormat.format("{0} ({1}) [{2}]", new Object[]{item.getBssid(), item.getSsid(), item.getLocations().size()}));
                }
            }
        });

        setupRecentFiles();

        StringConverter<Double> doubleFormat = new StringConverter<Double>() {
            private final DecimalFormat fmt = new DecimalFormat("0.0#");

            @Override
            public String toString(Double object) {
                return object == null ? "2.5" : fmt.format(object);
            }

            @Override
            public Double fromString(String string) {
                try {
                    return fmt.parse(string).doubleValue();
                } catch (ParseException ex) {
                    return 2.5;
                }
            }

        };

        hexSizeChooser.setTextFormatter(new TextFormatter<>(doubleFormat));

        initMapStyleChooser();

        initializeWebView();

        leftStatus.textProperty().bind(openFileProperty.asString());

        networkList.itemsProperty().set(networks.filtered(n -> n.getLocations().size() > 0).sorted((a, b) -> {
            return Integer.compare(b.getLocations().size(), a.getLocations().size());
        }));

        InvalidationListener networkUpdater = (e) -> {
            Network selectedItem = networkList.getSelectionModel().getSelectedItem();
//            visualization.setText(createNetworkSummary(selectedItem));

            NetworkWiFiAnalyzer analy = new NetworkWiFiAnalyzer(selectedItem, Double.parseDouble(hexSizeChooser.getText()));
            NetworkWiFiAnalyzerJSHelper analyJs = new NetworkWiFiAnalyzerJSHelper(analy);

            String boundsScript = analyJs.setBounds("map", 50);
            webview.getEngine().executeScript(boundsScript);

            String geoJsonScript = analyJs.setDataGeoJSON("map", analy.geoJSON());
            webview.getEngine().executeScript(geoJsonScript);

            detailsText.setText(selectedItem.detailsSummary());
        };

        networkList.getSelectionModel().selectedIndexProperty().addListener(networkUpdater);
        hexSizeChooser.textProperty().addListener(networkUpdater);
    }

    private void setupRecentFiles() {

        Runnable menuUpdater = () -> {
            recentFilesMenu.getItems().clear();
            PersistentPreferences.getInstance().recentFiles().stream()
                    .forEach(rf -> {
                        Path p = Paths.get(rf);
                        MenuItem itm = new MenuItem(p.getFileName().toString());
                        itm.setOnAction(ee -> {
                            loadFile(p.toFile());
                        });
                        recentFilesMenu.getItems().add(itm);
                    });
        };

        PersistentPreferences.getInstance().recentFiles().addListener((Observable observable) -> {
            menuUpdater.run();
        });

        menuUpdater.run();
    }

    private void initMapStyleChooser() {
        mapStyleChooser.getItems().addAll(
                "Roadmap",
                "Satellite",
                "Hybrid",
                "Terrain"
        );
        mapStyleChooser.setValue("Roadmap");
        mapStyleChooser.valueProperty().addListener((e) -> {
            if (webview == null) {
                return;
            }
            String mapType = mapStyleChooser.getValue().toLowerCase();
            webview.getEngine().executeScript(MessageFormat.format(
                    "map.setMapTypeId(''{0}'');",
                    mapType));
        });
    }

    private void initializeWebView() {
        WebEngine webEngine = webview.getEngine();
        String html = new BufferedReader(new InputStreamReader(MainGUI.class.getResourceAsStream("/html/map.html"))).lines().collect(Collectors.joining("\n"));
        html = html.replace("$$GOOGLE_MAPS_API_KEY$$", PersistentPreferences.getInstance().getGoogleMapsAPIKey());
        webEngine.setJavaScriptEnabled(true);
        webEngine.setOnError((e) -> {
            System.out.println(e.toString());
        });
        webEngine.setOnAlert((e) -> {
            System.out.println(e.toString());
        });
        webEngine.loadContent(html);

//        Worker<Void> worker = webEngine.getLoadWorker();
//        worker.stateProperty().addListener((e) -> {
//            System.out.println("" + worker.getProgress() + " : " + worker.isRunning() + " : " + worker.getState());
//        });
    }

    public VBox root() {
        return root;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void onQuitAction(ActionEvent event) {
        stage.hide();
    }

    @FXML
    private void onNewAction(ActionEvent event) {
    }

    @FXML
    private void onOpenAction(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open WigleDB sqlite database");
        fc.getExtensionFilters().add(
                new ExtensionFilter("sqlite database", "*.sqlite")
        );
        File selectedFile = fc.showOpenDialog(stage);
        if (selectedFile != null) {
            loadFile(selectedFile);
        }
    }

    private void loadFile(File file) {
        try {
            WigleDBO wigle = WigleDBO.Open(file);

            networks.clear();

            wigle.listNetworks().forEach(n -> {
                Network network = new Network((List<Location> locations) -> {
                    try {
//                            System.out.println("finding locations: " + n.getBssid());
                        wigle.findLocations(n.getBssid()).forEach(loc -> {
                            Location l = new Location();
                            l.setBssid(loc.getBssid());
                            l.setLevel(loc.getLevel());
                            l.setLat(loc.getLat());
                            l.setLon(loc.getLon());
                            l.setAltitude(loc.getAltitude());
                            l.setAccuracy(loc.getAccuracy());
                            l.setTime(loc.getTime());
                            locations.add(l);
                        });
                    } catch (WigleDBO.WigleDBOException ex) {
                        Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
                network.setBssid(n.getBssid());
                network.setSsid(n.getSsid());
                network.setFrequency(n.getFrequency());
                network.setCapabilities(n.getCapabilities());
                network.setType(n.getType());
                networks.add(network);
            });

            openFileProperty.set(file);
            PersistentPreferences.getInstance().addRecentFile(file.getAbsolutePath());
        } catch (FileNotFoundException ex) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("File error");
            alert.setHeaderText("Unable to open file");
            alert.setContentText(ex.getLocalizedMessage());
        } catch (WigleDBO.WigleDBOException ex) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Wigle Database Error");
            alert.setHeaderText("Unable to read file");
            alert.setContentText(ex.getLocalizedMessage());
        }
    }

    @FXML
    private void onCloseAction(ActionEvent event) {
    }

    @FXML
    private void onAboutMenu(ActionEvent event) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setContentText("WiFi Mapper. Copyright tauTerra, LLC (c) 2018");
        alert.setTitle("About WiFiMapper");
        alert.setHeaderText("About");
        alert.showAndWait();
    }

    @FXML
    private void doGoogleMapsAPIDialog(ActionEvent event) {
        String oldKey = PersistentPreferences.getInstance().getGoogleMapsAPIKey();
        TextInputDialog d = new TextInputDialog(oldKey);
        d.setGraphic(new ImageView(MainGUI.class.getResource("/icons/key_48.png").toExternalForm()));
        d.setHeaderText("Google Maps API Key");
        d.setContentText("api key");
        d.setTitle("Google Maps API Key");
        d.setResizable(true);
        d.getDialogPane().setMinWidth(600);
        Optional<String> result = d.showAndWait();
        if (result.isPresent()) {
            PersistentPreferences.getInstance().setGoogleMapsAPIKey(result.get());
            if (!result.get().equals(oldKey)) {
                initializeWebView();
            }
        }
    }

}
