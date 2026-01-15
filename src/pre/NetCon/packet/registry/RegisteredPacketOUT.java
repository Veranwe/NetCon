package src.pre.NetCon.packet.registry;

import java.util.ArrayList;

public record RegisteredPacketOUT(
        int id,
        ArrayList<PacketField> fields
) {}
