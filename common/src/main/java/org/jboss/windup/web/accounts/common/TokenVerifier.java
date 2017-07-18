package org.jboss.windup.web.accounts.common;

/**
 * @author <a href="mailto:dklingenberg@gmail.com">David Klingenberg</a>
 */
public interface TokenVerifier
{
    /**
     * Verifies keycloak token
     *
     * @param token
     * @return
     * @throws Exception
     */
    AccessTokenData verify(String token) throws Exception;
}

