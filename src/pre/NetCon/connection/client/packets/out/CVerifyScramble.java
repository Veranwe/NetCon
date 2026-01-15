package src.pre.NetCon.connection.client.packets.out;

import src.pre.NetCon.packet.PacketOUT;

public class CVerifyScramble extends PacketOUT {

    private final byte[] scrambledKey;
    private final byte[] verifyKey;

    public CVerifyScramble(byte[] scrambledKey, byte[] verifyKey) {
        this.scrambledKey = scrambledKey;
        this.verifyKey = verifyKey;
    }
}
