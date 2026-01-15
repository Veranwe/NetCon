package src.pre.NetCon.connection.client.packets.out;

import src.pre.NetCon.packet.PacketOUT;
import src.pre.NetCon.security.AsymmetricCrypt;

import java.security.PublicKey;

public class CKeyExchange extends PacketOUT {

    private final byte[] key;

    public CKeyExchange(PublicKey key) {
        this.key = AsymmetricCrypt.encodePublic(key);
    }
}
