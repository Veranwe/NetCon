package src.pre.NetCon.connection.node.in;

import net.aSysSync.ASysThread;
import net.bitBytes.BitBytes;
import net.bitBytes.BitUtil;
import net.bitBytes.converters.BitDecoder;
import net.bitBytes.util.Holder;
import src.net.TrackGaze.log.Log;
import src.net.TrackGaze.log.LogGroup;
import src.pre.NetCon.connection.node.NetNode;
import src.pre.NetCon.connection.node.keys.NetKeyManager;
import src.pre.NetCon.exceptions.InvalidKey;
import src.pre.NetCon.packet.PacketIN;
import src.pre.NetCon.packet.data.BracketData;
import src.pre.NetCon.packet.data.HeaderData;
import src.pre.NetCon.security.EncryptionType;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

public class PacketListener {

    private final NetNode node;

    private final InputStream inputStream;
    private final NetKeyManager keyManager;

    private final ASysThread listener = new ASysThread();

    private long packetCount = -1;

    public static final LogGroup packetListenGroup = new LogGroup(NetNode.nodeGroup, "packet_listener");

    public PacketListener(NetNode node, InputStream inputStream, NetKeyManager keyManager) {
        Log.debug(packetListenGroup, "Initialize", "Initialize the packet listener.");

        this.node = node;

        this.inputStream = inputStream;
        this.keyManager = keyManager;

        this.start();

        Log.info(packetListenGroup, "Initialized", "Initialized the packet listener.");
    }

    public void start() {
        Log.debug(packetListenGroup, "Starting", "Starting the packet listener.");

        this.listener.loop("ListeningForPackets", () -> {
            Holder<byte[]> data = new Holder<>(new byte[0]);

            while (true) {
                try {
                    int receivedBytes = inputStream.read();
                    if (receivedBytes == -1) break;

                    BitUtil.mergeArrays(data, new byte[]{(byte) receivedBytes});
                    if (process(new Holder<>(data.obj))) break;

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Log.info(packetListenGroup, "Started", "Started the packet listener.");
    }
    public void stop() throws IOException {
        Log.debug(packetListenGroup, "Stopping", "Stopping the packet listener.");

        this.listener.stop();
        this.inputStream.close();

        Log.info(packetListenGroup, "Stopped", "Stopped the packet listener.");
    }

    public boolean process(Holder<byte[]> data) {
        Holder<byte[]> packetBytes =
                new Holder<>(catchData(data, BracketData.NET_OPENING_BRACKET, BracketData.NET_CLOSING_BRACKET));

        if (packetBytes.obj.length == 0) return false;

        BitBytes headerBytes = BitDecoder.decode(
                catchData(packetBytes, BracketData.HEAD_OPENING_BRACKET, BracketData.HEAD_CLOSING_BRACKET)
        );

        HeaderData headerData = new HeaderData(
                (EncryptionType) headerBytes.get("EncryptionType"),
                (byte[]) headerBytes.get("initialVector"),
                (int) headerBytes.get("keyID"),
                (int) headerBytes.get("packetID"),
                (long) headerBytes.get("packetCount")
        );

        Log.debug(packetListenGroup, "process", "Processing packet: " + headerData.packetID());

        if (headerData.packetCount() != packetCount) return true;

        this.packetCount = headerData.packetCount() + 1;

        byte[] contentBytes = catchData(packetBytes, BracketData.CONTENT_OPENING_BRACKET, BracketData.CONTENT_CLOSING_BRACKET);

        try {
            PacketIN.process(contentBytes, headerData, this.node, this.keyManager);

        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException | InvalidKey e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    public byte[] catchData(Holder<byte[]> data, byte[] openingBracket, byte[] closingBracket) {
        int startPos = -1;
        int length = -1;

        for (int i = 0; i < data.obj.length - 8; i++) {
            boolean isOpenKey = true;
            boolean isCloseKey = true;

            for (int j = 0; j < 8; j++) {
                if (data.obj[i + j] == openingBracket[j]) continue;

                isOpenKey = false;
                break;
            }
            if (isOpenKey) {
                startPos = i + 8;
                continue;

            } else {
                for (int j = 1; j <= 8; j++) {
                    if (data.obj[i + j] == closingBracket[j - 1]) continue;

                    isCloseKey = false;
                    break;
                }
            }
            if (isCloseKey) {
                if (startPos == -1) continue;
                length = i - startPos + 1;
            }
            if (startPos != -1 && length != -1) break;
        }

        if (startPos != -1 && length != -1) {
            BitUtil.removeBytes(data, startPos);
            return BitUtil.getRemoveBytes(data, length);

        } else {
            return new byte[0];
        }
    }
}
