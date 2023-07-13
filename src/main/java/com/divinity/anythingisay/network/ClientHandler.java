package com.divinity.anythingisay.network;

import com.divinity.anythingisay.cap.PlayerHolderAttacher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class ClientHandler {

    public static void updateDimensions(UUID player) {
        Minecraft mc = Minecraft.getInstance();
        Player updatePlayer = mc.level != null ? mc.level.getPlayerByUUID(player) : null;
        if (updatePlayer != null) {
            updatePlayer.refreshDimensions();
        }
    }

    public static boolean isRainingLava() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            var holder = PlayerHolderAttacher.getPlayerHolderUnwrap(player);
            if (holder != null) {
                return holder.isRainingLava();
            }
        }
        return false;
    }
}
