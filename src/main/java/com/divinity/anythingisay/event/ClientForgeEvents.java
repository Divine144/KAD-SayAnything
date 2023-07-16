package com.divinity.anythingisay.event;

import com.divinity.anythingisay.cap.PlayerHolderAttacher;
import com.divinity.anythingisay.mixin.LocalPlayerAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientForgeEvents {

    @SubscribeEvent
    public static void onInputUpdate(MovementInputUpdateEvent event) {
        if (event.getEntity() instanceof LocalPlayer player) {
            var cap = PlayerHolderAttacher.getPlayerHolderUnwrap(player);
            if (cap != null) {
                if (cap.getFrozenTicks() > 0) {
                    player.input.leftImpulse = 0.0F;
                    player.input.forwardImpulse = 0.0F;
                    player.input.shiftKeyDown = false;
                    player.input.jumping = false;
                }
                else if (cap.getNoWalkTicks() > 0) {
                    if (player instanceof LocalPlayerAccessor accessor) {
                        accessor.setSprintTriggerTime(0);
                    }
                    player.setSprinting(false);
                }
                if (cap.isTwisted()) {
                    player.input.leftImpulse = calculateImpulse(player.input.right, player.input.left);
                    player.input.forwardImpulse = calculateImpulse(player.input.down, player.input.up);
                    player.input.jumping = Minecraft.getInstance().options.keyShift.isDown();
                    player.input.shiftKeyDown = Minecraft.getInstance().options.keyJump.isDown();
                }
            }
        }
    }

    private static float calculateImpulse(boolean pInput, boolean pOtherInput) {
        if (pInput == pOtherInput) {
            return 0.0F;
        }
        else {
            return pInput ? 1.0F : -1.0F;
        }
    }
}
