package src.pre.NetCon.connection.node;

import src.pre.NetCon.connection.client.packets.out.CNotifyHostReady;
import src.pre.NetCon.connection.node.in.NetNodeInbound;
import src.pre.NetCon.connection.node.keys.NetKeyManager;
import src.pre.NetCon.connection.node.out.NetNodeOutbound;
import src.pre.NetCon.packet.PacketOUT;

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

    public NetNode(String address, int port, String group) throws IOException {
        this.socket = new Socket(address, port);

        this.keyManager = new NetKeyManager(this, false);

        this.inbound = new NetNodeInbound(this, this.socket.getInputStream(), this.keyManager);
        this.outbound = new NetNodeOutbound(this.socket.getOutputStream(), this.keyManager);

        this.group = group;

        this.expectedPacketID = 1;

        this.send(new CNotifyHostReady());
    }
    public NetNode(Socket socket) throws IOException {
        this.socket = socket;

        this.keyManager = new NetKeyManager(this, true);

        this.inbound = new NetNodeInbound(this, this.socket.getInputStream(), this.keyManager);
        this.outbound = new NetNodeOutbound(this.socket.getOutputStream(), this.keyManager);

        this.expectedPacketID = -1;
    }

    public void stop() throws IOException {
        this.socket.close();

        this.keyManager.stop();

        this.inbound.stop();
        this.outbound.stop();
    }

    public void send(PacketOUT packet) throws IOException {
        this.outbound.send(packet);
    }

    public boolean isVerified() {
        return this.verified;
    }
    public void verified() {
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
