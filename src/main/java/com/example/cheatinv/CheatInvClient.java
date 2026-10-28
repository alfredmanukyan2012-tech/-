package com.example.cheatinv;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class CheatInvClient implements ClientModInitializer {
    private static KeyBinding openKey;
    /** true только если мы в мире хоста с этим модом (не на чужом сервере). */
    private static boolean menuEnabled = false;

    @Override
    public void onInitializeClient() {
        openKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.cheatinv.open",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "key.categories.cheatinv"
        ));

        ClientPlayNetworking.registerGlobalReceiver(EnableMenuPayload.ID,
                (payload, context) -> menuEnabled = true);

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> menuEnabled = false);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openKey.wasPressed()) {
                if (client.player != null && menuEnabled && client.currentScreen == null) {
                    client.setScreen(new ItemMenuScreen());
                }
            }
        });
    }
}
