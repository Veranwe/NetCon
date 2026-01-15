package src.pre.NetCon.connection.host.packets.out.verification;

import src.pre.NetCon.packet.PacketOUT;

public class HVerifyConfirmed extends PacketOUT {

    private final boolean confirmation;

    public HVerifyConfirmed(boolean confirmation) {
        this.confirmation = confirmation;
    }
}
