package src.pre.NetCon.connection.host.packets.in;

import net.aSysSync.ASysThread;
import net.aSysSync.ASysUtil;
import src.pre.NetCon.NetCon;
import src.pre.NetCon.connection.host.packets.out.started.HStarted;
import src.pre.NetCon.connection.node.NetNode;
import src.pre.NetCon.packet.PacketIN;
import src.pre.NetCon.security.AsymmetricCrypt;

public class CKeyExchange extends PacketIN {

    private byte[] key;

    @Override
    public void execute(NetNode node) {
        node.keyManager.setTargetKey(
                AsymmetricCrypt.decodePublic(key)
        );
        node.keyManager.start();

        new ASysThread().run(() -> {
            ASysUtil.threadSleep(1000);
            node.send(new HStarted());
            NetCon.started();
        });

    }
}
