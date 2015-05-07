package ru.shishmakov.config;


public interface AppConfig {

    String getProfileId();

    String getConnectionHost();

    Integer getConnectionPort();

    String getConnectionUri();

    String getDatabaseHost();

    Integer getDatabasePort();

    String getDatabaseName();

    String getCollectionName();

    String getDatabaseUser();

    String getDatabasePassword();

    String getBindHost();

    Integer getBindPort();

}
