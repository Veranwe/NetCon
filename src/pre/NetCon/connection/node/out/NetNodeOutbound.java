package src.pre.NetCon.connection.node.out;

import src.pre.NetCon.connection.node.keys.NetKeyManager;
import src.pre.NetCon.packet.PacketOUT;
import src.pre.NetCon.security.EncryptionType;

import java.io.IOException;
import java.io.OutputStream;

public class NetNodeOutbound {

    private final OutputStream outputStream;

    private final NetKeyManager keyManager;

    public NetNodeOutbound(OutputStream outputStream, NetKeyManager keyManager) {
        this.outputStream = outputStream;
        this.keyManager = keyManager;
    }

    public void stop() {
        try {
            this.outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(PacketOUT packet, long packetCount) {
        this.send(packet, EncryptionType.SYMMETRIC, packetCount);
    }
    public void send(PacketOUT packet, EncryptionType encryptType, long packetCount) {
        try {
            this.outputStream.write(packet.serialize(encryptType, keyManager, packetCount));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
