package com.divinity.anythingisay.mixin;

import com.divinity.anythingisay.cap.PlayerHolderAttacher;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @Shadow protected abstract void move(double pDistanceOffset, double pVerticalOffset, double pHorizontalOffset);

    @Shadow protected abstract double getMaxZoom(double pStartingDistance);

    @Redirect(
            method = "setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;move(DDD)V", ordinal = 0)
    )
    public void setup(Camera instance, double pDistanceOffset, double pVerticalOffset, double pHorizontalOffset) {
        if (instance.getEntity() instanceof Player player) {
            var cap = PlayerHolderAttacher.getPlayerHolderUnwrap(player);
            if (cap != null) {
                if (cap.getBigTicks() > 0) {
                    move(-this.getMaxZoom(25.0D), -5.0D, 0D);
                    return;
                }
            }
        }
        move(-this.getMaxZoom(4.0D), 0.0D, 0.0D);
    }
}
