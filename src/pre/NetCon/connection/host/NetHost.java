package src.pre.NetCon.connection.host;

import src.pre.NetCon.connection.NetConnection;
import src.pre.NetCon.connection.host.packets.in.CKeyExchange;
import src.pre.NetCon.connection.host.packets.in.CNotifyHostReady;
import src.pre.NetCon.connection.host.packets.in.CVerifyScramble;
import src.pre.NetCon.connection.host.packets.out.rollingKey.HUpdateRollingKey;
import src.pre.NetCon.connection.host.packets.out.verification.HKeyExchange;
import src.pre.NetCon.connection.host.packets.out.verification.HVerificationRequest;
import src.pre.NetCon.connection.host.packets.out.verification.HVerifyConfirmed;
import src.pre.NetCon.connection.node.NetNode;
import src.pre.NetCon.exceptions.AlreadyRunning;
import src.pre.NetCon.packet.PacketOUT;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.ArrayList;

import static src.pre.NetCon.NetCon.registerSystem;

public class NetHost implements NetConnection {

    private final int port;

    private final ServerSocket socket = new ServerSocket();
    private final ArrayList<NetNode> connectedNodes = new ArrayList<>();

    private final NodeListener listener = new NodeListener(this, this.socket);

    private boolean running = false;

    public NetHost(int port) throws IOException {
        this.port = port;

        registerSystem(1, HVerificationRequest.class);
        registerSystem(2, HVerifyConfirmed.class);
        registerSystem(3, HKeyExchange.class);

        registerSystem(11, HUpdateRollingKey.class);

        registerSystem(-1, CNotifyHostReady.class);
        registerSystem(-2, CVerifyScramble.class);
        registerSystem(-3, CKeyExchange.class);
    }

    @Override
    public void start() throws IOException, AlreadyRunning {
        if (this.running) throw new AlreadyRunning("NetHost is already running.");

        this.socket.bind(new InetSocketAddress(this.port));
        this.listener.start();

        this.running = true;
    }
    @Override
    public void stop() throws IOException {
        this.socket.close();

        this.listener.stop();

        this.running = false;
    }

    public void connectNode(NetNode node) {
        this.connectedNodes.add(node);
    }

    public void send(PacketOUT packet) throws IOException {
        for (NetNode node : this.connectedNodes) {
            node.send(packet);
        }
    }
    public void send(String targetGroup, PacketOUT packet) throws IOException {
        for (NetNode node : this.connectedNodes) {
            if (!node.getGroup().equals(targetGroup)) continue;

            node.send(packet);
        }
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }
}
