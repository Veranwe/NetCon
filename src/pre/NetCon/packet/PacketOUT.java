package src.pre.NetCon.packet;

import net.bitBytes.BitBytes;
import net.bitBytes.BitUtil;
import net.bitBytes.converters.BitEncoder;
import net.bitBytes.util.Holder;
import src.net.TrackGaze.log.Log;
import src.net.TrackGaze.log.LogGroup;
import src.pre.NetCon.NetCon;
import src.pre.NetCon.connection.node.keys.NetKeyManager;
import src.pre.NetCon.exceptions.InvalidArgument;
import src.pre.NetCon.packet.data.BracketData;
import src.pre.NetCon.packet.registry.RegisteredPacketOUT;
import src.pre.NetCon.security.AsymmetricCrypt;
import src.pre.NetCon.security.EncryptionType;
import src.pre.NetCon.security.SymmetricCrypt;
import src.pre.NetCon.security.SymmetricKey;

import static src.pre.NetCon.NetCon.registry;

public non-sealed abstract class PacketOUT extends Packet {

    private static long packetCount = -1;

    public static final LogGroup outGroup = new LogGroup(NetCon.netRootGroup, "packet_out");

    public byte[] serialize(EncryptionType encryptionType, NetKeyManager keyManager) {
        RegisteredPacketOUT savedPacket = registry.getOUT(this.getClass());

        Log.debug(outGroup, "Sending", "Sending packet with id: " + savedPacket.id());

        BitBytes packetData = new BitBytes();
        savedPacket.fields().forEach(field -> packetData.put(field.name(), field.getValue(this)));

        byte[] encodedData = BitEncoder.encode(packetData);

        byte[] initialVector = SymmetricCrypt.genInitVector();
        byte[] encryptedData;
        int keyID;

        switch (encryptionType) {
            case SYMMETRIC -> {
                SymmetricKey key = keyManager.rollingKey.getLatestKey();
                keyID = key.id();

                encryptedData = SymmetricCrypt.encrypt(encodedData, key.key(), initialVector);
            }
            case ASYMMETRIC -> {
                keyID = -1;

                encryptedData = AsymmetricCrypt.encrypt(encodedData, keyManager.getTargetKey());
            }
            case NONE -> {
                keyID = -1;

                encryptedData = encodedData;
            }

            default -> throw new InvalidArgument("Attempted to use an invalid encryption type.");
        }

        BitBytes headerData = new BitBytes();
        headerData.put("EncryptionType", encryptionType);
        headerData.put("initialVector", initialVector);
        headerData.put("keyID", keyID);
        headerData.put("packetID", savedPacket.id());
        headerData.put("packetCount", packetCount++);

        Holder<byte[]> packet = new Holder<>(new byte[0]);
        BitUtil.mergeArrays(packet,
                BracketData.NET_OPENING_BRACKET,

                BracketData.HEAD_OPENING_BRACKET,
                BitEncoder.encode(headerData),
                BracketData.HEAD_CLOSING_BRACKET,

                BracketData.CONTENT_OPENING_BRACKET,
                encryptedData,
                BracketData.CONTENT_CLOSING_BRACKET,

                BracketData.NET_CLOSING_BRACKET
        );

        return packet.obj;
    }
}
