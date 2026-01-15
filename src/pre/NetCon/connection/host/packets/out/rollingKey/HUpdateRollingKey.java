package src.pre.NetCon.connection.host.packets.out.rollingKey;

import src.pre.NetCon.packet.PacketOUT;
import src.pre.NetCon.security.SymmetricCrypt;
import src.pre.NetCon.security.SymmetricKey;

public class HUpdateRollingKey extends PacketOUT {

    private final byte[] key;
    private final int keyID;

    public HUpdateRollingKey(SymmetricKey key) {
        this.key = SymmetricCrypt.encode(key.key());
        this.keyID = key.id();
    }
}
