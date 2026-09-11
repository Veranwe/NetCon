package src.pre.NetCon.connection.host;

import src.net.TrackGaze.log.Log;
import src.net.TrackGaze.log.LogGroup;
import src.pre.NetCon.NetCon;
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

    private final ServerSocket socket;
    private final ArrayList<NetNode> connectedNodes = new ArrayList<>();

    private final NodeListener listener;

    private boolean running = false;

    public static final LogGroup hostGroup = new LogGroup(NetCon.netRootGroup, "host");

    public NetHost(int port) {
        Log.debug(hostGroup, "Initialize", "Initializing NetHost.");

        try {
            this.port = port;
            this.socket = new ServerSocket();

            this.listener = new NodeListener(this, this.socket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        registerSystem(1, HVerificationRequest.class);
        registerSystem(2, HVerifyConfirmed.class);
        registerSystem(3, HKeyExchange.class);

        registerSystem(11, HUpdateRollingKey.class);

        registerSystem(-1, CNotifyHostReady.class);
        registerSystem(-2, CVerifyScramble.class);
        registerSystem(-3, CKeyExchange.class);

        Log.info(hostGroup, "Initialized", "Initialized NetHost.");
    }

    @Override
    public void start() {
        Log.debug(hostGroup, "Starting", "Attempt to start NetHost.");

        if (this.running) throw new AlreadyRunning("NetHost is already running.");

        try {
            this.socket.bind(new InetSocketAddress(this.port));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.listener.start();

        this.running = true;

        Log.info(hostGroup, "Started", "Started NetHost.");
    }
    @Override
    public void stop() {
        Log.debug(hostGroup, "Stopping", "Attempt to stop NetHost.");

        try {
            this.socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.listener.stop();

        this.running = false;

        Log.info(hostGroup, "Stopped", "Stopped NetHost.");
    }

    public void connectNode(NetNode node) {
        Log.info(hostGroup, "NodeConnected", "A node has connected to the host.");

        this.connectedNodes.add(node);
    }

    public void send(PacketOUT packet) {
        for (NetNode node : this.connectedNodes) {
            if (!node.isVerified()) continue;

            node.send(packet);
        }
    }
    public void send(String targetGroup, PacketOUT packet) {
        for (NetNode node : this.connectedNodes) {
            if (!node.getGroup().equals(targetGroup) || !node.isVerified()) continue;

            node.send(packet);
        }
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }
}
