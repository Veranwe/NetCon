package src.pre.NetCon.connection.node;

import src.net.TrackGaze.log.Log;
import src.net.TrackGaze.log.LogGroup;
import src.pre.NetCon.NetCon;
import src.pre.NetCon.connection.client.NetClient;
import src.pre.NetCon.connection.client.packets.out.CNotifyHostReady;
import src.pre.NetCon.connection.host.NetHost;
import src.pre.NetCon.connection.node.in.NetNodeInbound;
import src.pre.NetCon.connection.node.keys.NetKeyManager;
import src.pre.NetCon.connection.node.out.NetNodeOutbound;
import src.pre.NetCon.packet.PacketOUT;
import src.pre.NetCon.security.EncryptionType;

import java.io.IOException;
import java.net.Socket;

public class NetNode {

    private final Socket socket;

    public final NetKeyManager keyManager;

    private final NetNodeInbound inbound;
    private final NetNodeOutbound outbound;

    private String group;
    private boolean verified = false;

    private int expectedPacketID;

    public static LogGroup nodeGroup = new LogGroup(NetCon.netRootGroup, "node");

    public NetNode(String address, int port, String group) throws IOException {
        nodeGroup = new LogGroup(NetClient.clientGroup, "node");

        Log.debug(nodeGroup, "Initialize", "Initializing the node.");

        this.socket = new Socket(address, port);

        this.keyManager = new NetKeyManager(this, false);

        this.inbound = new NetNodeInbound(this, this.socket.getInputStream(), this.keyManager);
        this.outbound = new NetNodeOutbound(this.socket.getOutputStream(), this.keyManager);

        this.group = group;

        this.expectedPacketID = 1;

        this.send(new CNotifyHostReady(), EncryptionType.NONE);

        Log.info(nodeGroup, "Initialized", "Initialized the node.");
    }
    public NetNode(Socket socket) throws IOException {
        nodeGroup = new LogGroup(NetHost.hostGroup, "node");

        Log.debug(nodeGroup, "Initialize", "Initializing the node.");

        this.socket = socket;

        this.keyManager = new NetKeyManager(this, true);

        this.inbound = new NetNodeInbound(this, this.socket.getInputStream(), this.keyManager);
        this.outbound = new NetNodeOutbound(this.socket.getOutputStream(), this.keyManager);

        this.expectedPacketID = -1;

        Log.info(nodeGroup, "Initialized", "Initialized the node.");
    }

    public void stop() throws IOException {
        Log.info(nodeGroup, "Stopping", "Stopping the node.");

        this.socket.close();

        this.keyManager.stop();

        this.inbound.stop();
        this.outbound.stop();

        Log.info(nodeGroup, "Stopped", "Stopped the node.");
    }

    public void send(PacketOUT packet) throws IOException {
        this.outbound.send(packet);
    }
    public void send(PacketOUT packet, EncryptionType encryptType) throws IOException { // FIXME
        this.outbound.send(packet, encryptType);
    }

    public boolean isVerified() {
        return this.verified;
    }
    public void verified() {
        Log.info(nodeGroup, "Verified", "The node has been verified.");
        this.verified = true;
    }

    public void updateExpectedPacket(int expectedPacketID) {
        this.expectedPacketID = expectedPacketID;
    }
    public boolean isExpectedPacket(int packetID) {
        return this.expectedPacketID == packetID;
    }

    public String getGroup() {
        return this.group;
    }
}
