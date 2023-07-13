package com.divinity.anythingisay.mixin;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraftforge.network.ICustomPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientboundCustomPayloadPacket.class)
public abstract class ClientboundCustomPayloadPacketMixin implements ICustomPacket<ClientboundCustomPayloadPacket> {
    @Shadow
    @Final
    private FriendlyByteBuf data;

    @Inject(method = "write", at = @At("HEAD"))
    private void onWriteHead(FriendlyByteBuf pBuffer, CallbackInfo ci) {
        // Forge bug
        this.data.readerIndex(0);
    }
}
