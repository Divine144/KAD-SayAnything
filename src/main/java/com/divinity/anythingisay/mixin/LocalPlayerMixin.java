package com.divinity.anythingisay.mixin;

import com.divinity.anythingisay.cap.PlayerHolderAttacher;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends LivingEntity {

    protected LocalPlayerMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(
            method = "setSprinting(Z)V",
            at = @At("HEAD"),
            cancellable = true
    )
    public void setSprinting(boolean pSprinting, CallbackInfo ci) {
        LocalPlayer instance = (LocalPlayer) (Object) this;
        if (instance != null) {
            var cap = PlayerHolderAttacher.getPlayerHolderUnwrap(instance);
            if (cap != null) {
                if (cap.getNoWalkTicks() > 0) {
                    super.setSprinting(false);
                    ci.cancel();
                }
            }
        }
    }
}
