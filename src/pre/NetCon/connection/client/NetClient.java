package src.pre.NetCon.connection.client;

import src.pre.NetCon.connection.NetConnection;
import src.pre.NetCon.connection.client.packets.in.rollingKey.HUpdateRollingKey;
import src.pre.NetCon.connection.client.packets.in.verification.HKeyExchange;
import src.pre.NetCon.connection.client.packets.in.verification.HVerificationRequest;
import src.pre.NetCon.connection.client.packets.in.verification.HVerifyConfirmed;
import src.pre.NetCon.connection.client.packets.out.CKeyExchange;
import src.pre.NetCon.connection.client.packets.out.CNotifyHostReady;
import src.pre.NetCon.connection.client.packets.out.CVerifyScramble;
import src.pre.NetCon.connection.node.NetNode;
import src.pre.NetCon.exceptions.AlreadyRunning;
import src.pre.NetCon.packet.PacketOUT;

import java.io.IOException;

import static src.pre.NetCon.NetCon.registerSystem;

public class NetClient implements NetConnection {

    private final String address;
    private final int port;

    private final String group;

    private NetNode node;

    private boolean running = false;

    public NetClient(String address, int port, String group) {
        this.address = address;
        this.port = port;

        this.group = group;

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

        this.node = new NetNode(this.address, this.port, this.group);

        this.running = true;
    }

    @Override
    public void stop() throws IOException {
        this.node.stop();

        this.running = false;
    }

    public void send(PacketOUT packet) throws IOException {
        this.node.send(packet);
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }
}
