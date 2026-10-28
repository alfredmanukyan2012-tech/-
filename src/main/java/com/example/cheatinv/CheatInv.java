package com.example.cheatinv;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;

public class CheatInv implements ModInitializer {
    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playC2S().register(GiveItemPayload.ID, GiveItemPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(GiveItemPayload.ID, (payload, context) -> {
            var player = context.player();
            var server = context.server();

            // Защита: на выделенных серверах мод ничего не выдаёт.
            if (server.isDedicated()) return;

            Item item = Registries.ITEM.get(payload.itemId());
            if (item == Items.AIR) return;

            int count = Math.max(1, Math.min(payload.count(), item.getMaxCount()));
            player.getInventory().offerOrDrop(new ItemStack(item, count));
        });
    }
}
