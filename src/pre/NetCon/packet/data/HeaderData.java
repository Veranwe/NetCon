package src.pre.NetCon.packet.data;

import src.pre.NetCon.security.EncryptionType;

public record HeaderData(
        EncryptionType encryptionType,
        byte[] initialVector,
        int keyID,
        int packetID,
        long packetCount
) {}
