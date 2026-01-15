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

    public void stop() throws IOException {
        this.outputStream.close();
    }

    public void send(PacketOUT packet) throws IOException {
        this.send(packet, EncryptionType.SYMMETRIC);
    }
    public void send(PacketOUT packet, EncryptionType encryptType) throws IOException {
        this.outputStream.write(packet.serialize(encryptType, keyManager));
    }
}
