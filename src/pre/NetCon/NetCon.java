package src.pre.NetCon;

import net.bitBytes.converters.types.enums.BitRegisteredEnums;
import src.pre.NetCon.connection.NetConnection;
import src.pre.NetCon.connection.client.NetClient;
import src.pre.NetCon.connection.host.NetHost;
import src.pre.NetCon.exceptions.AlreadyRunning;
import src.pre.NetCon.exceptions.InvalidArgument;
import src.pre.NetCon.exceptions.InvalidConnection;
import src.pre.NetCon.exceptions.StillRunning;
import src.pre.NetCon.packet.Packet;
import src.pre.NetCon.packet.PacketOUT;
import src.pre.NetCon.packet.registry.NetRegistry;
import src.pre.NetCon.security.EncryptionType;

import java.io.IOException;

public class NetCon {

    private static NetConnection con = null;
    public static final NetRegistry registry = new NetRegistry();

    public static void init(NetConnection connection) throws StillRunning, InvalidConnection {
        if (con != null && con.isRunning())
            throw new StillRunning("The connection you are attempting to initialize is still running.");

        con = connection;

        BitRegisteredEnums.registerEnums(EncryptionType.class);
    }

    public static void start() throws AlreadyRunning, IOException {
        con.start();
    }
    public static void stop() throws IOException {
        con.stop();
    }

    public static void send(PacketOUT packet) throws InvalidConnection, IOException {
        if (con == null)
            throw new InvalidConnection("The connection is not yet initialized. Use init() to initialize the connection.");

        switch (con) {
            case NetHost host -> host.send(packet);
            case NetClient client -> client.send(packet);

            default -> throw new InvalidConnection("Could not send packet due to an unexpected connection type.");
        }
    }
    public static void send(PacketOUT packet, String group) throws InvalidConnection, IOException {
        if (con == null)
            throw new InvalidConnection("The connection is not yet initialized. Use init() to initialize the connection.");

        switch (con) {
            case NetHost host -> host.send(group, packet);
            case NetClient ignore ->
                    throw new InvalidArgument("The client does not have the ability to send to a group of targets.");

            default -> throw new InvalidConnection("Could not send packet due to an unexpected connection type.");
        }
    }

    public static void registerSystem(int id, Class<? extends Packet> packetClass) {
        if (id >= 100 || id <= -100)
            throw new InvalidArgument("ID's within the bounds of -100 & 100 are reserved for system packets.");

        try {
            registry.register(id, packetClass);

        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException("Failed to register packet, id: " + id, e);
        }
    }
    public static void register(int id, Class<? extends Packet> packetClass) {
        if (id < 100 && id > -100)
            throw new InvalidArgument("ID's within the bounds of -100 & 100 are reserved for system packets.");

        try {
            registry.register(id, packetClass);

        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException("Failed to register packet, id: " + id, e);
        }
    }
}
