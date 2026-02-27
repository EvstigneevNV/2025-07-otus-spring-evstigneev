package ru.otus.bookverse.auth.service;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Component
public class KeyPairProvider {

    @Getter
    private final RSAKey rsaKey;

    @Getter
    private final JWKSet jwkSet;

    public KeyPairProvider() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair kp = kpg.generateKeyPair();

            this.rsaKey = new RSAKey.Builder((RSAPublicKey) kp.getPublic())
                    .privateKey((RSAPrivateKey) kp.getPrivate())
                    .keyID(UUID.randomUUID().toString())
                    .build();
            this.jwkSet = new JWKSet(this.rsaKey.toPublicJWK());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to init RSA key", e);
        }
    }
}
