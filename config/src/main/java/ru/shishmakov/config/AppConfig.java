package ru.shishmakov.config;


public interface AppConfig {

    String getSessionId();

    String getConnectionHost();

    Integer getConnectionPort();

    String getConnectionUri();

    String getDatabaseHost();

    String getDatabasePort();

    String getDatabaseName();

    String getDatabaseUser();

    String getDatabasePassword();

    String getBindHost();

    Integer getBindPort();

}
