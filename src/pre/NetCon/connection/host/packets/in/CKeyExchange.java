package src.pre.NetCon.connection.host.packets.in;

import src.pre.NetCon.connection.node.NetNode;
import src.pre.NetCon.packet.PacketIN;
import src.pre.NetCon.security.AsymmetricCrypt;

public class CKeyExchange extends PacketIN {

    private byte[] key;

    @Override
    public void execute(NetNode node) {
        node.keyManager.setTargetKey(
                AsymmetricCrypt.decodePublic(key)
        );
    }
}
