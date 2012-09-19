/**
 * Copyright (C) 2010 Cubeia Ltd <info@cubeia.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cubeia.poker.handhistory.storage;

import com.cubeia.firebase.api.service.ServiceContext;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Helper class for reading the mongo configuration. Will load the file "poker.properties" in the config directory of Firebase.
 */
public class DatabaseStorageConfiguration {

    private static final Logger log = Logger.getLogger(DatabaseStorageConfiguration.class);
    private String host;
    private int port;
    private String databaseName;

    public DatabaseStorageConfiguration load(ServiceContext context) {
        Properties properties = loadProperties(context);
        host = properties.getProperty("mongo.host", "localhost");
        port = Integer.parseInt(properties.getProperty("mongo.port", "27017"));
        databaseName = properties.getProperty("mongo.database-name", "hands");
        return this;
    }

    private Properties loadProperties(ServiceContext context) {
        String fileName = context.getServerConfigDirectory().getAbsolutePath();
        Properties properties = new Properties();
        try {
            FileInputStream stream = new FileInputStream(new File(fileName + "/poker.properties"));
            properties.load(stream);
        } catch (IOException e) {
            log.warn("Could not load properties from " + fileName + ". Using defaults.");
        }
        return properties;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getDatabaseName() {
        return databaseName;
    }
}
