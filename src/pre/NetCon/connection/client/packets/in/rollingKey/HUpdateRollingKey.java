package src.pre.NetCon.connection.client.packets.in.rollingKey;

import src.pre.NetCon.connection.node.NetNode;
import src.pre.NetCon.packet.PacketIN;
import src.pre.NetCon.security.SymmetricCrypt;

public class HUpdateRollingKey extends PacketIN {

    private byte[] key;
    private int keyID;

    @Override
    public void execute(NetNode node) {
        node.keyManager.rollingKey.inputKey(
                this.keyID,
                SymmetricCrypt.decode(key)
        );
    }
}
