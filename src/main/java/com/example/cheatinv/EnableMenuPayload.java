package com.example.cheatinv;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/** Сервер (мир хоста, не выделенный сервер) говорит клиенту: меню разрешено. */
public record EnableMenuPayload() implements CustomPayload {
    public static final CustomPayload.Id<EnableMenuPayload> ID =
            new CustomPayload.Id<>(Identifier.of("cheatinv", "enable_menu"));

    public static final PacketCodec<RegistryByteBuf, EnableMenuPayload> CODEC =
            PacketCodec.unit(new EnableMenuPayload());

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
