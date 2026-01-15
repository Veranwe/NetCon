package src.pre.NetCon.security;

import javax.crypto.SecretKey;
import java.security.SecureRandom;

public class KeyScrambler {

    private static final SecretKey key = SymmetricCrypt.decode(new byte[] {
            -116, 45, -9, 65, -121, 96, 19, -82,
            119, 57, 94, -55, 54, -79, -120, 31,
            -28, 81, -90, 11, -40, -107, 39, 108,
            -6, 49, 125, 1, -94, -34, 67, 26
    });
    private static final byte[] iv = new byte[] {
            52, -91,  17,  48,
            -63, 14, -12,  7,
            59, -118,  33, -27,
            75,  -5, -123, -33
    };

    public static byte[] genData() {
        byte[] data = new byte[16];
        new SecureRandom().nextBytes(data);
        return data;
    }
    public static byte[] scramble(byte[] data) {
        return SymmetricCrypt.encrypt(data, key, iv);
    }
}
