/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tauterra.wifimapper.wigledb;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 *
 * @author nickfolse
 */
public class WigleDBO implements AutoCloseable {

    private final Connection conn;

    public static WigleDBO Open(File file) throws FileNotFoundException, WigleDBOException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }

        String connectionString = createConnectionString(file);
        Connection conn = null;
        try {
            // load the sqlite-JDBC driver using the current class loader
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(connectionString);
        } catch (SQLException e) {
            throw new WigleDBOException("Unable to open database '" + connectionString + "'", e);
        } catch (ClassNotFoundException ee) {
            throw new WigleDBOException("Unable to initialize org.sqlite.JDBC driver", ee);
        }

        return new WigleDBO(conn);
    }

    private WigleDBO(Connection conn) {
        this.conn = conn;
    }

    private final QueryHelper<NetworkSummary> listNetworksQuery = new QueryHelper<>(
            "select distinct(network.bssid) as bssid, ssid, frequency, capabilities, type from network "
            + "group by network.bssid",
            () -> new NetworkSummary(),
            (rs, s) -> {
                s.setBssid(rs.getString(1));
                s.setSsid(rs.getString(2));
                s.setFrequency(rs.getInt(3));
                s.setCapabilities(rs.getString(4));
                s.setType(rs.getString(5));
            });

    public List<NetworkSummary> listNetworks() throws WigleDBOException {
        return listNetworksQuery.executeQuery();
    }

    private final QueryHelper<WigleNetwork> findNetworkQuery = new QueryHelper<>(
            "select bssid, ssid, frequency, capabilities, lasttime, lastlat, lastlon, "
            + "type, bestlevel, bestlat, bestlon from network where bssid = ?",
            () -> new WigleNetwork(),
            (rs, network) -> {
                int idx = 1;
                network.setBssid(rs.getString(idx++));
                network.setSsid(rs.getString(idx++));
                network.setFrequency(rs.getInt(idx++));
                network.setCapabilities(rs.getString(idx++));
                network.setLasttime(rs.getLong(idx++));
                network.setLastlat(rs.getDouble(idx++));
                network.setLastlon(rs.getDouble(idx++));
                network.setType(rs.getString(idx++));
                network.setBestlevel(rs.getInt(idx++));
                network.setBestlat(rs.getDouble(idx++));
                network.setBestlon(rs.getDouble(idx++));
            });

    public Optional<WigleNetwork> findNetwork(String bssid) throws WigleDBOException {
        return findNetworkQuery.executeQuerySingle(bssid);
    }

    private final QueryHelper<WigleLocation> findLocationsQuery = new QueryHelper<>(
            "select _id, bssid, level, lat, lon, altitude, accuracy, time "
            + "from location where bssid = ?",
            () -> new WigleLocation(),
            (rs, loc) -> {
                int idx = 1;
                loc.setId(rs.getLong(idx++));
                loc.setBssid(rs.getString(idx++));
                loc.setLevel(rs.getInt(idx++));
                loc.setLat(rs.getDouble(idx++));
                loc.setLon(rs.getDouble(idx++));
                loc.setAltitude(rs.getFloat(idx++));
                loc.setAccuracy(rs.getDouble(idx++));
                loc.setTime(rs.getLong(idx++));
            }
    );
    
    public List<WigleLocation> findLocations(String bssid) throws WigleDBOException {
        return findLocationsQuery.executeQuery(bssid);
    }

    private static String createConnectionString(File file) {
        MessageFormat fmt = new MessageFormat("jdbc:sqlite:{0}");
        return fmt.format(new Object[]{file.getAbsolutePath()});
    }

    @Override
    public void close() throws Exception {
        if (!conn.isClosed()) {
            conn.close();
        }
    }

    @FunctionalInterface
    public interface QueryHandler<T> {

        public void accept(ResultSet rs, T item) throws SQLException;
    }

    private class QueryHelper<T> {

        private final String query;
        private final Supplier<T> supplier;
        private final QueryHandler<T> handler;

        public QueryHelper(String query, Supplier<T> supplier, QueryHandler<T> handler) {
            this.query = query;
            this.supplier = supplier;
            this.handler = handler;
        }

        public List<T> executeQuery(Object... args) throws WigleDBOException {
            final List<T> result = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                int idx = 1;
                for (Object arg : args) {
                    if (arg instanceof Long) {
                        stmt.setLong(idx, (Long) arg);
                    } else if (arg instanceof Integer) {
                        stmt.setInt(idx, (Integer) arg);
                    } else if (arg instanceof Double) {
                        stmt.setDouble(idx, (Double) arg);
                    } else if (arg instanceof Float) {
                        stmt.setFloat(idx, (Float) arg);
                    } else if (arg instanceof String) {
                        stmt.setString(idx, (String) arg);
                    } else if (arg instanceof Boolean) {
                        stmt.setBoolean(idx, (Boolean) arg);
                    } else if (arg instanceof Date) {
                        stmt.setDate(idx, (Date) arg);
                    } else {
                        stmt.setObject(idx, arg);
                    }
                    idx++;
                }
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        T item = supplier.get();
                        handler.accept(rs, item);
                        result.add(item);
                    }
                }
            } catch (SQLException ex) {
                throw new WigleDBOException("Unable to query database", ex);
            }
            return result;
        }

        public List<T> executeQuery() throws WigleDBOException {
            final List<T> result = new ArrayList<>();
            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery(query)) {
                    while (rs.next()) {
                        T item = supplier.get();
                        handler.accept(rs, item);
                        result.add(item);
                    }
                }
            } catch (SQLException ex) {
                throw new WigleDBOException("Unable to query database", ex);
            }
            return result;
        }

        public Optional<T> executeQuerySingle(Object... args) throws WigleDBOException {
            T result = supplier.get();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                int idx = 1;
                for (Object arg : args) {
                    if (arg instanceof Long) {
                        stmt.setLong(idx, (Long) arg);
                    } else if (arg instanceof Integer) {
                        stmt.setInt(idx, (Integer) arg);
                    } else if (arg instanceof Double) {
                        stmt.setDouble(idx, (Double) arg);
                    } else if (arg instanceof Float) {
                        stmt.setFloat(idx, (Float) arg);
                    } else if (arg instanceof String) {
                        stmt.setString(idx, (String) arg);
                    } else if (arg instanceof Boolean) {
                        stmt.setBoolean(idx, (Boolean) arg);
                    } else if (arg instanceof Date) {
                        stmt.setDate(idx, (Date) arg);
                    } else {
                        stmt.setObject(idx, arg);
                    }
                    idx++;
                }
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        return Optional.empty();
                    }
                    handler.accept(rs, result);
                }
            } catch (SQLException ex) {
                throw new WigleDBOException(ex.getMessage(), ex);
            }
            return Optional.of(result);
        }
    }

    public static class WigleDBOException extends Exception {

        public WigleDBOException() {
        }

        public WigleDBOException(String message) {
            super(message);
        }

        public WigleDBOException(String message, Throwable cause) {
            super(message, cause);
        }

    }

}
