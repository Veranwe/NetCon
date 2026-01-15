package src.pre.NetCon.connection.host.packets.out.verification;

import src.pre.NetCon.packet.PacketOUT;

public class HVerificationRequest extends PacketOUT {

    private final byte[] verifyKey;

    public HVerificationRequest(byte[] verifyKey) {
        this.verifyKey = verifyKey;
    }
}
