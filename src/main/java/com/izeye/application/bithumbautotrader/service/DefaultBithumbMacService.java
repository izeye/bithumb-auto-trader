package com.izeye.application.bithumbautotrader.service;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Default {@link BithumbMacService}.
 *
 * @author Johnny Lim
 */
@Service
public class DefaultBithumbMacService implements BithumbMacService {

    private static final String MAC_ALGORITHM = "HmacSHA512";

    private final SecretKeySpec keySpec;

    public DefaultBithumbMacService(BithumbProperties properties) {
        this.keySpec = new SecretKeySpec(properties.getSecretKey().getBytes(), MAC_ALGORITHM);
    }

    @Override
    public String createMac(String value) {
        return toHexBase64String(doCreateMac(value));
    }

    private byte[] doCreateMac(String value) {
        try {
            Mac mac = Mac.getInstance(MAC_ALGORITHM);
            mac.init(this.keySpec);

            return mac.doFinal( value.getBytes( ) );
        }
        catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
        catch (InvalidKeyException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String toHexBase64String(byte[] bytes) {
        byte[] hexEncoded =  new Hex().encode(bytes);
        return Base64.encodeBase64String(hexEncoded);
    }

}
