package ru.shishmakov.config;

import ru.shishmakov.config.helper.ProtocolType;

/**
 * @author Dmitriy Shishmakov
 */
public interface AppConfig {

    String getProfileId();

    String getConnectionHost();

    Integer getConnectionPort();

    String getConnectionUri();

    String getDatabaseHost();

    Integer getDatabasePort();

    String getDatabaseName();

    String getDatabaseUser();

    String getDatabasePassword();

    String getBindHost();

    Integer getBindPort();

    ProtocolType getProtocolType();

}
