package src.pre.NetCon.connection.host;

import net.aSysSync.ASysThread;
import src.pre.NetCon.connection.node.NetNode;

import java.io.IOException;
import java.net.ServerSocket;

public class NodeListener {

    private final NetHost host;
    private final ServerSocket socket;

    private final ASysThread listeningLoop = new ASysThread("NodeListeningLoop");

    public NodeListener(NetHost host, ServerSocket socket) {
        this.host = host;
        this.socket = socket;
    }

    public void start() {
        this.listeningLoop.loop("ListeningForNodes", () -> {
            try {
                this.connect(new NetNode(this.socket.accept()));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    public void stop() {
        this.listeningLoop.stop();
    }

    private void connect(NetNode node) {
        this.host.connectNode(node);
    }
}
