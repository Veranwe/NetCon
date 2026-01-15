package src.pre.NetCon.connection.node.in;

import src.pre.NetCon.connection.node.NetNode;
import src.pre.NetCon.connection.node.keys.NetKeyManager;

import java.io.IOException;
import java.io.InputStream;

public class NetNodeInbound {

    private final NetNode node;

    private final InputStream inputStream;

    private final NetKeyManager keyManager;
    private PacketListener listener;

    public NetNodeInbound(NetNode node, InputStream inputStream, NetKeyManager keyManager) {
        this.node = node;

        this.inputStream = inputStream;
        this.keyManager = keyManager;

        this.start();
    }

    public void start() {
        this.listener = new PacketListener(node, inputStream, keyManager);
    }
    public void stop() throws IOException {
        this.listener.stop();
    }
}
