/*
 * Copyright 2015-2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.windup.web.accounts.common;

import java.io.StringReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.ServletContext;

import org.jboss.windup.web.accounts.common.internal.MsgLogger;

/**
 * @author Juraci Paixão Kröhling
 */
@ApplicationScoped
public class ApplicationResources {
    MsgLogger logger = MsgLogger.LOGGER;

    private static final String REALM_CONFIG_KEY = "org.keycloak.json.adapterConfig";
    private String realmConfiguration = null;
    private ServletContext servletContext;

    private boolean realmConfigurationParsed = false;

    private String realmName;
    private String serverUrl;
    private String resourceName;
    private PublicKey publicKey;
    private String secret;

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Produces @RealmConfiguration
    public String getRealmConfiguration() {
        if (null == realmConfiguration) {
            realmConfiguration = servletContext.getInitParameter(REALM_CONFIG_KEY);
            logger.realmConfiguration(realmConfiguration);
        }
        return realmConfiguration;
    }

    @Produces @RealmName
    public String getRealmName() {
        if (!realmConfigurationParsed) {
            parseRealmConfiguration();
        }
        return realmName;
    }

    @Produces @AuthServerUrl
    public String getServerUrl() {
        if (!realmConfigurationParsed) {
            parseRealmConfiguration();
        }
        return serverUrl;
    }

    @Produces @RealmResourceName
    public String getResourceName() {
        if (!realmConfigurationParsed) {
            parseRealmConfiguration();
        }
        return resourceName;
    }

    @Produces @RealmResourceSecret
    public String getResourceNameSecret() {
        if (!realmConfigurationParsed) {
            parseRealmConfiguration();
        }
        return secret;
    }

    @Produces @RealmPublicKey
    public PublicKey getPublicKey() {
        if (!realmConfigurationParsed) {
            parseRealmConfiguration();
        }

        return publicKey;
    }


    protected PublicKey getPublicKey(String stringKey)  {
        PublicKey pubKey = null;

        try {
            byte[] publicBytes = Base64.getDecoder().decode(stringKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            pubKey = keyFactory.generatePublic(keySpec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return pubKey;
    }

    private void parseRealmConfiguration() {
        logger.parsingRealmConfiguration();
        JsonReader jsonReader = Json.createReader(new StringReader(getRealmConfiguration()));
        JsonObject configurationJson = jsonReader.readObject();
        //JsonObject credentials = configurationJson.getJsonObject("credentials");

        realmName = configurationJson.getString("realm");
        resourceName = configurationJson.getString("resource");
        //secret = credentials.getString("secret");
        String publicKeyString = configurationJson.getString("realm-public-key");
        publicKey = this.getPublicKey(publicKeyString);

        if (configurationJson.containsKey("auth-server-url-for-backend-requests")) {
            serverUrl = configurationJson.getString("auth-server-url-for-backend-requests");
            logger.backendUrlIsSet(serverUrl);
        } else {
            logger.backendUrlIsNotSet();
            String authContextPath = "/auth";
            if (configurationJson.containsKey("auth-server-url")) {
                authContextPath = configurationJson.getString("auth-server-url");
                logger.authServerUrlIsSet(authContextPath);
            }

            if (authContextPath.toLowerCase().startsWith("http")) {
                serverUrl = authContextPath;
            } else {
                int portOffset = Integer.parseInt(System.getProperty("jboss.socket.binding.port-offset", "0"));
                int defaultPort = Integer.parseInt(System.getProperty("jboss.http.port", "8080"));
                String host = System.getProperty("jboss.bind.address", "127.0.0.1");

                serverUrl = "http://" + host + ":" + (defaultPort+portOffset) + authContextPath;
            }

            logger.settingAuthServerUrl(serverUrl);
        }

        realmConfigurationParsed = true;
    }
}
