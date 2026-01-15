package src.pre.NetCon.connection.node.in;

import net.aSysSync.ASysThread;
import net.bitBytes.BitBytes;
import net.bitBytes.BitUtil;
import net.bitBytes.converters.BitDecoder;
import net.bitBytes.converters.types.BitLong;
import net.bitBytes.util.Holder;
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

    private final ASysThread listener = new ASysThread("PacketListener");

    private long packetCount = -1;

    public PacketListener(NetNode node, InputStream inputStream, NetKeyManager keyManager) {
        this.node = node;

        this.inputStream = inputStream;
        this.keyManager = keyManager;

        this.start();
    }

    public void start() {
        this.listener.loop("ListeningForPackets", () -> {
            Holder<byte[]> data = new Holder<>(new byte[0]);

            while (true) {
                try {
                    int receivedBytes = inputStream.read();
                    if (receivedBytes == -1) break;

                    BitUtil.mergeArrays(data, new byte[]{(byte) receivedBytes});
                    if (process(data)) break;

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    public void stop() throws IOException {
        this.listener.stop();
        this.inputStream.close();
    }

    public boolean process(Holder<byte[]> data) {
        Holder<byte[]> packetBytes =
                new Holder<>(catchData(data, BracketData.NET_OPENING_BRACKET, BracketData.NET_CLOSING_BRACKET));

        if (packetBytes.obj.length == 0) return false;

        BitBytes headerBytes = BitDecoder.decode(
                catchData(packetBytes, BracketData.HEAD_OPENING_BRACKET, BracketData.HEAD_CLOSING_BRACKET)
        );

        Holder<byte[]> initialVector = new Holder<>(new byte[0]);

        BitUtil.mergeArrays(initialVector,
                BitLong.encode((long) headerBytes.get("initialVector1")),
                BitLong.encode((long) headerBytes.get("initialVector2"))
        );

        HeaderData headerData = new HeaderData(
                (EncryptionType) headerBytes.get("EncryptionType"),
                initialVector.obj,
                (int) headerBytes.get("keyID"),
                (int) headerBytes.get("packetID"),
                (long) headerBytes.get("packetCount")
        );

        if (headerData.packetCount() == packetCount - 1) return true;
        this.packetCount = headerData.packetCount();

        byte[] contentBytes = catchData(packetBytes, BracketData.CONTENT_OPENING_BRACKET, BracketData.CONTENT_CLOSING_BRACKET);

        try {
            PacketIN.process(contentBytes, headerData, this.node, this.keyManager);

        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException | InvalidKey e) {
            return true;
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
                for (int j = 0; j < 8; j++) {
                    if (data.obj[i + j] == closingBracket[j]) continue;

                    isCloseKey = false;
                    break;
                }
            }
            if (isCloseKey) {
                if (startPos == -1) continue;
                length = i - startPos;
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
