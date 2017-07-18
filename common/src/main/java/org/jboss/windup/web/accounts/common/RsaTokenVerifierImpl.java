package org.jboss.windup.web.accounts.common;

import org.keycloak.RSATokenVerifier;
import org.keycloak.representations.AccessToken;

import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * @author <a href="mailto:dklingenberg@gmail.com">David Klingenberg</a>
 */
@ApplicationScoped
public class RsaTokenVerifierImpl implements TokenVerifier
{
    @Inject
    @AuthServerUrl
    private String baseUrl;

    @Inject
    @RealmName
    private String realm;

    @Inject @RealmPublicKey
    private PublicKey publicKey;

    protected PublicKey getPublicKey(String stringKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] publicBytes = Base64.getDecoder().decode(stringKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey pubKey = keyFactory.generatePublic(keySpec);

        return pubKey;
    }

    @Override
    public AccessTokenData verify(String token) throws Exception
    {
        String url = baseUrl + "/realms/" + URLEncoder.encode(realm, "UTF-8");
        AccessToken accessToken = RSATokenVerifier.verifyToken(token, publicKey, url);

        return new AccessTokenData(
                accessToken.getId(),
                accessToken.getSubject(),
                accessToken.getPreferredUsername(),
                accessToken.getIssuedAt(),
                accessToken.getExpiration()
        );
    }
}
