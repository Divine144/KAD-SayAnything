package com.divinity.anythingisay.mixin;

import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LocalPlayer.class)
public interface LocalPlayerAccessor {
    @Accessor("sprintTriggerTime")
    void setSprintTriggerTime(int sprintTriggerTime);
}
