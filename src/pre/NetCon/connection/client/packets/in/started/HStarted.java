package src.pre.NetCon.connection.client.packets.in.started;

import src.pre.NetCon.NetCon;
import src.pre.NetCon.connection.node.NetNode;
import src.pre.NetCon.packet.PacketIN;

public class HStarted extends PacketIN {

    @Override
    public void execute(NetNode node) {
        NetCon.started();
    }
}
