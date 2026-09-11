package src.pre.NetCon.connection.client.packets.in.verification;

import src.pre.NetCon.connection.client.packets.out.CVerifyScramble;
import src.pre.NetCon.connection.node.NetNode;
import src.pre.NetCon.packet.PacketIN;
import src.pre.NetCon.security.EncryptionType;
import src.pre.NetCon.security.KeyScrambler;

public class HVerificationRequest extends PacketIN {

    private byte[] verifyKey;

    @Override
    public void execute(NetNode node) {
        node.send(new CVerifyScramble(
                KeyScrambler.scramble(verifyKey),
                verifyKey
        ), EncryptionType.NONE);

        node.updateExpectedPacket(2);

    }
}
