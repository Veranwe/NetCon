package src.pre.NetCon.connection.client.packets.in.verification;

import src.pre.NetCon.connection.client.packets.out.CKeyExchange;
import src.pre.NetCon.connection.node.NetNode;
import src.pre.NetCon.packet.PacketIN;
import src.pre.NetCon.security.EncryptionType;

import java.io.IOException;

public class HVerifyConfirmed extends PacketIN {

    private boolean confirmation;

    @Override
    public void execute(NetNode node) {
        try {
            if (confirmation) {
                node.verified();
                node.send(new CKeyExchange(node.keyManager.getKeyPair().getPublic()), EncryptionType.NONE);
            } else {
                node.stop();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
