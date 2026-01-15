package src.pre.NetCon.packet;

import net.bitBytes.BitBytes;
import net.bitBytes.converters.BitDecoder;
import src.pre.NetCon.connection.node.NetNode;
import src.pre.NetCon.connection.node.keys.NetKeyManager;
import src.pre.NetCon.exceptions.InvalidArgument;
import src.pre.NetCon.exceptions.InvalidKey;
import src.pre.NetCon.packet.data.HeaderData;
import src.pre.NetCon.packet.registry.PacketField;
import src.pre.NetCon.packet.registry.RegisteredPacketIN;
import src.pre.NetCon.security.AsymmetricCrypt;
import src.pre.NetCon.security.SymmetricCrypt;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import static src.pre.NetCon.NetCon.registry;

public non-sealed abstract class PacketIN extends Packet {

    public static void process(byte[] data, HeaderData header, NetNode node, NetKeyManager keyManager) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, InvalidKey {
        if (!node.isVerified() && !node.isExpectedPacket(header.packetID())) return;

        byte[] decryptedData;

        switch (header.encryptionType()) {
            case SYMMETRIC -> decryptedData = SymmetricCrypt.decrypt(
                    data, keyManager.rollingKey.getKey(header.keyID()), header.initialVector()
            );
            case ASYMMETRIC -> decryptedData = AsymmetricCrypt.decrypt(data, keyManager.getKeyPair().getPrivate());

            default -> throw new InvalidArgument("Attempted to use an invalid encryption type.");
        }
        BitBytes packetData = BitDecoder.decode(decryptedData);

        RegisteredPacketIN savedPacket = registry.getIN(header.packetID());
        PacketIN packet = savedPacket.packet().getDeclaredConstructor().newInstance();

        ArrayList<PacketField> fields = savedPacket.fields();

        fields.forEach(field -> field.setValue(packet, packetData.get(field.name())));

        packet.execute(node);
    }

    public abstract void execute(NetNode node);
}
