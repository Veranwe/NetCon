package src.pre.NetCon.connection.host.packets.in;

import src.pre.NetCon.connection.host.packets.out.verification.HKeyExchange;
import src.pre.NetCon.connection.host.packets.out.verification.HVerifyConfirmed;
import src.pre.NetCon.connection.node.NetNode;
import src.pre.NetCon.packet.PacketIN;
import src.pre.NetCon.security.EncryptionType;
import src.pre.NetCon.security.KeyScrambler;

import java.io.IOException;
import java.util.Arrays;

public class CVerifyScramble extends PacketIN {

    private byte[] scrambledKey;
    private byte[] verifyKey;

    @Override
    public void execute(NetNode node) {
        boolean verified = Arrays.equals(KeyScrambler.scramble(verifyKey), scrambledKey);

        try {
            node.send(new HVerifyConfirmed(verified), EncryptionType.NONE);

            if (verified) {
                node.verified();
                node.send(new HKeyExchange(node.keyManager.getKeyPair().getPublic()), EncryptionType.NONE);

            } else {
                node.stop();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
