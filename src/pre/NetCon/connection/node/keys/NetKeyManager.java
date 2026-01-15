package src.pre.NetCon.connection.node.keys;

import src.pre.NetCon.connection.node.NetNode;
import src.pre.NetCon.security.AsymmetricCrypt;

import java.security.KeyPair;
import java.security.PublicKey;

public class NetKeyManager {

    private final NetNode node;

    private final KeyPair keyPair = AsymmetricCrypt.genAsymKeys();
    private PublicKey targetKey;

    public final RollingKey rollingKey;

    private final boolean hosting;

    public NetKeyManager(NetNode node, boolean hosting) {
        this.node = node;

        this.rollingKey = new RollingKey(this.node, hosting);

        this.hosting = hosting;

        this.start();
    }

    public void start() {
        this.rollingKey.start();
    }
    public void stop() {
        this.rollingKey.stop();
    }

    public void setTargetKey(PublicKey key) {
        this.targetKey = key;
    }
    public PublicKey getTargetKey() {
        return this.targetKey;
    }
    public KeyPair getKeyPair() {
        return this.keyPair;
    }
}
