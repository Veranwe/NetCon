package src.pre.NetCon.connection.node.keys;

import net.aSysSync.ASysThread;
import net.aSysSync.ASysUtil;
import src.pre.NetCon.connection.host.packets.out.rollingKey.HUpdateRollingKey;
import src.pre.NetCon.connection.node.NetNode;
import src.pre.NetCon.exceptions.InvalidKey;
import src.pre.NetCon.security.SymmetricCrypt;
import src.pre.NetCon.security.SymmetricKey;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.ArrayList;

public class RollingKey {

    private final NetNode node;

    private int nextID = 0;
    private final ASysThread keyRollingLoop = new ASysThread("keyRollingLoop");

    private final ArrayList<SymmetricKey> keys = new ArrayList<>();

    private final boolean hosting;

    public RollingKey(NetNode node, boolean hosting) {
        this.node = node;

        this.hosting = hosting;
    }

    public void start() {
        if (!hosting) return;

        this.keyRollingLoop.loop("keyGenerator", () -> {
            this.keys.add(new SymmetricKey(nextID++, SymmetricCrypt.genSymKey()));
            this.shareKey();

            if (this.keys.size() > 1) this.keys.removeFirst();

            ASysUtil.threadSleep(60 * 1000);
        });
    }
    public void stop() {
        this.keyRollingLoop.stop();
    }

    private void shareKey() {
        try {
            this.node.send(new HUpdateRollingKey(keys.getLast()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void inputKey(int id, SecretKey key) {
        if (hosting) return;

        this.keys.add(new SymmetricKey(id, key));

        if (this.keys.size() > 1) this.keys.removeFirst();
    }

    public SecretKey getKey(int id) throws InvalidKey {
        SecretKey key = null;

        for (SymmetricKey currentKey : this.keys) {
            if (currentKey.id() != id) continue;

            key = currentKey.key();
            break;
        }
        if (key == null) throw new InvalidKey("Could not find a key with that id. It may be outdated.");

        return key;
    }
    public SymmetricKey getLatestKey() {
        return this.keys.getLast();
    }
}
