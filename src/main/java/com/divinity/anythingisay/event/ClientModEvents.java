package com.divinity.anythingisay.event;

import com.divinity.anythingisay.AnythingISay;
import com.divinity.anythingisay.client.renderer.FakePlayerRenderer;
import com.divinity.anythingisay.init.EntityInit;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AnythingISay.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModEvents {

    @SubscribeEvent
    public static void init(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityInit.KIER.get(), FakePlayerRenderer::new);
        event.registerEntityRenderer(EntityInit.DEV.get(), FakePlayerRenderer::new);
    }
}
