package src.pre.NetCon.security;

import javax.crypto.SecretKey;

public record SymmetricKey(
        int id,
        SecretKey key
) {}
