package src.pre.NetCon.connection.client.packets.in.verification;

import src.pre.NetCon.connection.client.packets.out.CVerifyScramble;
import src.pre.NetCon.connection.node.NetNode;
import src.pre.NetCon.packet.PacketIN;
import src.pre.NetCon.security.KeyScrambler;

import java.io.IOException;

public class HVerificationRequest extends PacketIN {

    private byte[] verifyKey;

    @Override
    public void execute(NetNode node) {
        try {
            node.send(new CVerifyScramble(
                    KeyScrambler.scramble(verifyKey),
                    verifyKey
            ));

            node.updateExpectedPacket(2);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
