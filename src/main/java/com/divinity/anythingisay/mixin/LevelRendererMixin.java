package com.divinity.anythingisay.mixin;

import com.divinity.anythingisay.AnythingISay;
import com.divinity.anythingisay.network.ClientHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Unique
    private static final ResourceLocation LAVA_RAIN = new ResourceLocation(AnythingISay.MODID, "textures/environment/lava_rain.png");

    @Inject(
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V", shift = At.Shift.AFTER),
            method = "renderSnowAndRain"
    )
    private void getRainTexture(LightTexture pLightTexture, float pPartialTick, double pCamX, double pCamY, double pCamZ, CallbackInfo ci) {
        if (ClientHandler.isRainingLava()) {
            RenderSystem.setShaderTexture(0, LAVA_RAIN);
        }
    }

    @Redirect(
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitation()Lnet/minecraft/world/level/biome/Biome$Precipitation;"),
            method = "renderSnowAndRain"
    )
    private Biome.Precipitation getLuckyRainPrecipitation(Biome instance) {
        if (ClientHandler.isRainingLava()) {
            return Biome.Precipitation.RAIN;
        }
        return instance.getPrecipitation();
    }

    @Redirect(
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;warmEnoughToRain(Lnet/minecraft/core/BlockPos;)Z"),
            method = "renderSnowAndRain"
    )
    private boolean getPrecipitation(Biome instance, BlockPos pPos) {
        if (ClientHandler.isRainingLava()) {
            return true;
        }
        return instance.warmEnoughToRain(pPos);
    }
}
