package com.example.cheatinv;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record GiveItemPayload(Identifier itemId, int count) implements CustomPayload {
    public static final CustomPayload.Id<GiveItemPayload> ID =
            new CustomPayload.Id<>(Identifier.of("cheatinv", "give_item"));

    public static final PacketCodec<RegistryByteBuf, GiveItemPayload> CODEC = PacketCodec.tuple(
            Identifier.PACKET_CODEC, GiveItemPayload::itemId,
            PacketCodecs.VAR_INT, GiveItemPayload::count,
            GiveItemPayload::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
