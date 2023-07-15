package com.divinity.anythingisay.mixin;

import com.divinity.anythingisay.cap.PlayerHolderAttacher;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin<T extends AbstractClientPlayer> extends EntityRenderer<T> {

    public PlayerRendererMixin(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Inject(
            method = "scale(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;F)V",
            at = @At("HEAD"),
            cancellable = true
    )
    public void scale(AbstractClientPlayer pLivingEntity, PoseStack pMatrixStack, float pPartialTickTime, CallbackInfo ci) {
        if (pLivingEntity != null) {
            var cap = PlayerHolderAttacher.getPlayerHolderUnwrap(pLivingEntity);
            if (cap != null) {
                if (cap.getSmallTicks() > 0) {
                    float f = 0.9375F;
                    pMatrixStack.scale(f / 3, f / 3, f / 3);
                    shadowRadius = 0.5F / 3;
                    ci.cancel();
                }
                else {
                    shadowRadius = 0.5F;
                }
            }
        }
    }
}
