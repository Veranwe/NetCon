package src.pre.NetCon.packet.registry;

import java.lang.invoke.VarHandle;

public record PacketField(
        Class<?> type,
        String name,
        VarHandle handle
) {

    public Object getValue(Object instance) { return handle.get(instance); }

    public void setValue(Object instance, Object value) { handle.set(instance, value); }
}
