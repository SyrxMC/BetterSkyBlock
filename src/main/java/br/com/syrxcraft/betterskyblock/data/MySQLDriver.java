package br.com.syrxcraft.betterskyblock.data;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MySQLDriver {

    private final String host;
    private final String username;
    private final String password;
    private final String database;

    public MySQLDriver(@Nonnull String host, @Nonnull String username, @Nonnull String password, @Nonnull String database){

        this.host = host;
        this.username = username;
        this.password = password;
        this.database = database;

    }

    public String getDatabase() {
        return database;
    }

    public String getHost() {
        return host;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public boolean isDriverAvailable(){
        try{ Class.forName("com.mysql.jdbc.Driver"); return true; } catch (ClassNotFoundException ignored) { } return false;
    }

    public String getTargetConnection(){
        return "jdbc:mysql://" + host + "/" + database;
    }

    public synchronized Connection connect() throws SQLException {

        if(!isDriverAvailable()){
            throw new SQLException("Driver is not available.");
        }

        Properties properties = new Properties();

        properties.setProperty("user", username);
        properties.setProperty("password", password);

        return DriverManager.getConnection(getTargetConnection(), properties);

    }

}
