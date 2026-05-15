package src.pre.NetCon.connection.host;

import net.aSysSync.ASysThread;
import src.net.TrackGaze.log.Log;
import src.net.TrackGaze.log.LogGroup;
import src.pre.NetCon.connection.node.NetNode;

import java.io.IOException;
import java.net.ServerSocket;

public class NodeListener {

    private final NetHost host;
    private final ServerSocket socket;

    private final ASysThread listeningLoop = new ASysThread("NodeListeningLoop");

    public static final LogGroup nodeListenGroup = new LogGroup(NetHost.hostGroup, "node_listener");

    public NodeListener(NetHost host, ServerSocket socket) {
        Log.debug(nodeListenGroup, "Initialize", "Initializing the node listener.");

        this.host = host;
        this.socket = socket;

        Log.info(nodeListenGroup, "Initialized", "Initialized the node listener.");
    }

    public void start() {
        Log.debug(nodeListenGroup, "Starting", "Starting the node listener.");

        this.listeningLoop.loop("ListeningForNodes", () -> {
            try {
                this.connect(new NetNode(this.socket.accept()));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Log.info(nodeListenGroup, "Started", "Started the node listener.");
    }
    public void stop() {
        Log.debug(nodeListenGroup, "Stopping", "Stopping the node listener.");

        this.listeningLoop.stop();

        Log.info(nodeListenGroup, "Stopped", "Stopped the node listener.");
    }

    private void connect(NetNode node) {
        this.host.connectNode(node);
    }
}
