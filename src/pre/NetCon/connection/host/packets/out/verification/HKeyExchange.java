package src.pre.NetCon.connection.host.packets.out.verification;

import src.pre.NetCon.packet.PacketOUT;
import src.pre.NetCon.security.AsymmetricCrypt;

import java.security.PublicKey;

public class HKeyExchange extends PacketOUT {

    private final byte[] key;

    public HKeyExchange(PublicKey key) {
        this.key = AsymmetricCrypt.encodePublic(key);
    }
}
