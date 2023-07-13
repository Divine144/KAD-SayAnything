package com.divinity.anythingisay.cap;

import com.divinity.anythingisay.AnythingISay;
import dev._100media.capabilitysyncer.core.CapabilityAttacher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = AnythingISay.MODID)
public class PlayerHolderAttacher extends CapabilityAttacher {
    public static final Capability<PlayerHolder> EXAMPLE_CAPABILITY = getCapability(new CapabilityToken<>() {});
    public static final ResourceLocation EXAMPLE_RL = new ResourceLocation(AnythingISay.MODID, "example");
    private static final Class<PlayerHolder> CAPABILITY_CLASS = PlayerHolder.class;

    @Nullable
    public static PlayerHolder getPlayerHolderUnwrap(Player player) {
        return getPlayerHolder(player).orElse(null);
    }

    public static LazyOptional<PlayerHolder> getPlayerHolder(Player player) {
        return player.getCapability(EXAMPLE_CAPABILITY);
    }

    private static void attach(AttachCapabilitiesEvent<Entity> event, Player entity) {
        genericAttachCapability(event, new PlayerHolder(entity), EXAMPLE_CAPABILITY, EXAMPLE_RL);
    }

    public static void register() {
        CapabilityAttacher.registerCapability(CAPABILITY_CLASS);
        CapabilityAttacher.registerEntityAttacher(Player.class, PlayerHolderAttacher::attach, PlayerHolderAttacher::getPlayerHolder);
    }
}
