package src.pre.NetCon;

import net.bitBytes.converters.types.enums.BitRegisteredEnums;
import src.net.TrackGaze.TrackGaze;
import src.net.TrackGaze.log.Log;
import src.net.TrackGaze.log.LogGroup;
import src.pre.NetCon.connection.NetConnection;
import src.pre.NetCon.connection.client.NetClient;
import src.pre.NetCon.connection.host.NetHost;
import src.pre.NetCon.exceptions.InvalidArgument;
import src.pre.NetCon.exceptions.InvalidConnection;
import src.pre.NetCon.exceptions.StillRunning;
import src.pre.NetCon.packet.Packet;
import src.pre.NetCon.packet.PacketOUT;
import src.pre.NetCon.packet.registry.NetRegistry;
import src.pre.NetCon.security.EncryptionType;

public class NetCon {

    private static NetConnection con = null;
    public static final NetRegistry registry = new NetRegistry();

    public static final LogGroup netRootGroup = new LogGroup(TrackGaze.rootGroup, "net_root");

    public static void init(NetConnection connection) {
        Log.debug(netRootGroup, "Initialize", "Attempt to initialize NetCon.");

        if (con != null && con.isRunning())
            throw new StillRunning("The connection you are attempting to initialize is still running.");

        con = connection;

        BitRegisteredEnums.registerEnums(EncryptionType.class);

        Log.info(netRootGroup, "Initialized", "NetCon has been initialized.");
    }

    public static void start() {
        Log.debug(netRootGroup, "Starting", "Attempt to start NetCon.");
        con.start();
        Log.info(netRootGroup, "Started", "Started NetCon.");
    }
    public static void stop() {
        Log.debug(netRootGroup, "Stopping", "Attempt to stop NetCon.");
        con.stop();
        Log.info(netRootGroup, "Stopped", "Stopped NetCon.");
    }

    public static void send(PacketOUT packet) {
        Log.debug(netRootGroup, "SendingNoGroup", "Attempt to send without a target group.");

        if (con == null)
            throw new InvalidConnection("The connection is not yet initialized. Use init() to initialize the connection.");

        switch (con) {
            case NetHost host -> host.send(packet);
            case NetClient client -> client.send(packet);

            default -> throw new InvalidConnection("Could not send packet due to an unexpected connection type.");
        }
    }
    public static void send(PacketOUT packet, String group) {
        Log.debug(netRootGroup, "SendingGroup", "Attempt to send with a target group.");

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
        Log.debug(netRootGroup, "RegisterSystem", "Attempt to register a system packet.");

        if (id >= 100 || id <= -100)
            throw new InvalidArgument("ID's within the bounds of -100 & 100 are reserved for system packets.");

        registry.register(id, packetClass);
    }
    public static void register(int id, Class<? extends Packet> packetClass) {
        Log.debug(netRootGroup, "Register", "Attempt to register a custom packet.");

        if (id < 100 && id > -100)
            throw new InvalidArgument("ID's within the bounds of -100 & 100 are reserved for system packets.");

        registry.register(id, packetClass);
    }
}
