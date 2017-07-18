/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates
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
package org.jboss.windup.web.accounts.websocket.internal;

import org.jboss.windup.web.accounts.common.AccessTokenData;

/**
 * @author Juraci Paixão Kröhling
 */
public class CachedSession {
    private final String authToken;
    private final AccessTokenData parsedToken;
    private long expiresAt;

    public CachedSession(String authToken, AccessTokenData parsedToken, long expiresAt) {
        this.authToken = authToken;

        if (null == parsedToken) {
            throw new IllegalStateException("Token cannot be null for a session");
        }

        if (expiresAt <= 0) {
            throw new IllegalStateException("Invalid expiration date/time for session");
        }

        this.parsedToken = parsedToken;
        this.expiresAt = expiresAt;
    }

    public AccessTokenData getParsedToken() {
        return parsedToken;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public String getAuthToken() {
        return authToken;
    }
}
