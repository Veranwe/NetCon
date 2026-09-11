package src.pre.NetCon.connection.host.packets.in;

import src.pre.NetCon.connection.host.packets.out.verification.HVerificationRequest;
import src.pre.NetCon.connection.node.NetNode;
import src.pre.NetCon.packet.PacketIN;
import src.pre.NetCon.security.EncryptionType;
import src.pre.NetCon.security.KeyScrambler;

public class CNotifyHostReady extends PacketIN {
    @Override
    public void execute(NetNode node) {
        node.send(new HVerificationRequest(KeyScrambler.genData()), EncryptionType.NONE);

        node.updateExpectedPacket(-2);
    }
}
