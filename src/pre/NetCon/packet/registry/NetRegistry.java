package src.pre.NetCon.packet.registry;

import src.pre.NetCon.packet.Packet;
import src.pre.NetCon.packet.PacketIN;
import src.pre.NetCon.packet.PacketOUT;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class NetRegistry {

    private final HashMap<Integer, RegisteredPacketIN> packetsIN = new HashMap<>();
    private final HashMap<Class<? extends PacketOUT>, RegisteredPacketOUT> packetsOUT = new HashMap<>();

    public void register(int id, Class<? extends Packet> packet) throws IllegalAccessException, NoSuchFieldException {
        MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(
                packet,
                MethodHandles.lookup()
        );

        ArrayList<PacketField> fields = new ArrayList<>();

        for (Field field : packet.getDeclaredFields()) {
            fields.add(new PacketField(
                    field.getType(),
                    field.getName(),
                    lookup.findVarHandle(
                            packet,
                            field.getName(),
                            field.getType()
                    )
            ));
        }

        if (PacketIN.class.isAssignableFrom(packet)) {
            Class<? extends PacketIN> packetClass = packet.asSubclass(PacketIN.class);
            this.packetsIN.put(id, new RegisteredPacketIN(packetClass, fields));
            return;
        }
        if (PacketOUT.class.isAssignableFrom(packet)) {
            Class<? extends PacketOUT> packetClass = packet.asSubclass(PacketOUT.class);
            this.packetsOUT.put(packetClass, new RegisteredPacketOUT(id, fields));
        }
    }

    public RegisteredPacketIN getIN(int id) {
        return this.packetsIN.get(id);
    }
    public RegisteredPacketOUT getOUT(Class<? extends PacketOUT> packet) {
        return this.packetsOUT.get(packet.asSubclass(PacketOUT.class));
    }
}
