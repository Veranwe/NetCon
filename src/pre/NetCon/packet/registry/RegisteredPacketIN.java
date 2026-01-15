package src.pre.NetCon.packet.registry;

import src.pre.NetCon.packet.PacketIN;

import java.util.ArrayList;

public record RegisteredPacketIN(
        Class<? extends PacketIN> packet,
        ArrayList<PacketField> fields
) {}
