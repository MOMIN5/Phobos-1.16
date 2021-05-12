package me.earth.phobos;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import me.earth.phobos.features.modules.misc.RPC;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;

public class DiscordPresence {
    private static final DiscordRPC rpc;
    public static DiscordRichPresence presence;
    private static Thread thread;
    private static int index;
    private static MinecraftClient mc;

    static {
        mc = MinecraftClient.getInstance();
        index = 1;
        rpc = DiscordRPC.INSTANCE;
        presence = new DiscordRichPresence();
    }

    public static void start() {
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        rpc.Discord_Initialize("737779695134834695", handlers, true, "");
        DiscordPresence.presence.startTimestamp = System.currentTimeMillis() / 1000L;
        DiscordPresence.presence.details = mc.currentScreen instanceof GameMenuScreen ? "In the main menu." : "Playing " + (mc.getCurrentServerEntry() != null ? (RPC.INSTANCE.showIP.getValue().booleanValue() ? "on " + mc.getCurrentServerEntry().address + "." : " multiplayer.") : " singleplayer.");
        DiscordPresence.presence.state = RPC.INSTANCE.state.getValue();
        DiscordPresence.presence.largeImageKey = "phobos";
        DiscordPresence.presence.largeImageText = "Phobos 1.16 by MOMIN5";
        rpc.Discord_UpdatePresence(presence);
        thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                rpc.Discord_RunCallbacks();
                DiscordPresence.presence.details = mc.currentScreen instanceof GameMenuScreen ? "In the main menu." : "Playing " + (mc.getCurrentServerEntry() != null ? (RPC.INSTANCE.showIP.getValue().booleanValue() ? "on " + mc.getCurrentServerEntry().address + "." : " multiplayer.") : " singleplayer.");
                DiscordPresence.presence.state = RPC.INSTANCE.state.getValue();
                if (RPC.INSTANCE.catMode.getValue().booleanValue()) {
                    if (index == 16) {
                        index = 1;
                    }
                    DiscordPresence.presence.largeImageKey = "cat" + index;
                    ++index;
                }
                rpc.Discord_UpdatePresence(presence);
                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException interruptedException) {
                }
            }
        }, "RPC-Callback-Handler");
        thread.start();
    }

    public static void stop() {
        if (thread != null && !thread.isInterrupted()) {
            thread.interrupt();
        }
        rpc.Discord_Shutdown();
    }
}
