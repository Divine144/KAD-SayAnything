package com.divinity.anythingisay.event;

import com.divinity.anythingisay.AnythingISay;
import com.divinity.anythingisay.cap.PlayerHolderAttacher;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.EntitySummonArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.SummonCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = AnythingISay.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonForgeEvents {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        var dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("freeze")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> {
                            var holder = PlayerHolderAttacher.getPlayerHolderUnwrap(EntityArgument.getPlayer(context, "player"));
                            if (holder != null) {
                                holder.setFrozenTicks(30 * 20);
                            }
                            return Command.SINGLE_SUCCESS;
                        })
                )
        );
        dispatcher.register(Commands.literal("die")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> {
                            Player player = EntityArgument.getPlayer(context, "player");
                            player.kill();
                            return Command.SINGLE_SUCCESS;
                        })
                )
        );
        dispatcher.register(Commands.literal("walk")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> {
                            var holder = PlayerHolderAttacher.getPlayerHolderUnwrap(EntityArgument.getPlayer(context, "player"));
                            if (holder != null) {
                                holder.setNoWalkTicks(60 * 20);
                            }
                            return Command.SINGLE_SUCCESS;
                        })
                )
        );
        dispatcher.register(Commands.literal("rain")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> {
                            Player player = EntityArgument.getPlayer(context, "player");
                            BlockPos.betweenClosedStream(AABB.ofSize(player.position().add(0, 25, 0), 25, 1, 25))
                                    .forEach(pos -> {
                                        if (player.level.getBlockState(pos).is(Blocks.AIR)) {
                                            player.level.setBlock(pos, Fluids.WATER.defaultFluidState().createLegacyBlock(), 11);
                                        }
                                    });
                            return Command.SINGLE_SUCCESS;
                        })
                )
        );
        // Auto register all mobs to command thingy
        ForgeRegistries.ENTITY_TYPES.getKeys().stream()
                .map(s -> {
                    String[] st = s.toString().split(":");
                    return st[st.length - 1];
                }).forEach(s -> {
                    dispatcher.register(Commands.literal(s)
                            .then(Commands.argument("player", EntityArgument.player())
                                    .executes(context -> {
                                        Player player = EntityArgument.getPlayer(context, "player");
                                        EntityType.byString(s).ifPresent(t -> {
                                            for (int i = 0; i < 10; i++) {
                                                var entity = t.create(player.level);
                                                if (entity != null) {
                                                    entity.setPos(player.position());
                                                    player.level.addFreshEntity(entity);
                                                }
                                            }
                                        });
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                    );
                });
        dispatcher.register(Commands.literal("twist")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> {
                            var player = EntityArgument.getPlayer(context, "player");
                            var holder = PlayerHolderAttacher.getPlayerHolderUnwrap(player);
                            if (holder != null) {
                                holder.setTwisted(!holder.isTwisted());
                                if (!holder.isTwisted()) {
                                    player.removeEffect(MobEffects.CONFUSION);
                                }
                                else {
                                    player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, Integer.MAX_VALUE, 0, false, false, false));
                                }
                            }
                            return Command.SINGLE_SUCCESS;
                        })
                )
        );

        dispatcher.register(Commands.literal("small")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> {
                            var player = EntityArgument.getPlayer(context, "player");
                            var holder = PlayerHolderAttacher.getPlayerHolderUnwrap(player);
                            if (holder != null) {
                                holder.setSmall(!holder.isSmall());
                            }
                            return Command.SINGLE_SUCCESS;
                        })
                )
        );
        dispatcher.register(Commands.literal("explode")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> {
                            var player = EntityArgument.getPlayer(context, "player");
                            BlockPos.betweenClosedStream(AABB.ofSize(player.position().add(0, 0, 0), 5, 1, 5))
                                    .forEach(pos -> {
                                        var entity = EntityType.TNT.create(player.getLevel());
                                        if (entity != null) {
                                            entity.setPos(pos.getX(), pos.getY(), pos.getZ());
                                            player.level.addFreshEntity(entity);
                                        }
                                    });
                            return Command.SINGLE_SUCCESS;
                        })
                )
        );
        dispatcher.register(Commands.literal("lava")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> {
                            var player = EntityArgument.getPlayer(context, "player");
                            var holder = PlayerHolderAttacher.getPlayerHolderUnwrap(player);
                            if (holder != null) {
                                holder.setRainingLava(!holder.isRainingLava());
                            }
                            return Command.SINGLE_SUCCESS;
                        })
                )
        );

    }

    public static final EntityDimensions OLD_PLAYER_DIMENSIONS = new EntityDimensions(0.6f, 1.8f, false);
    public static final EntityDimensions SHRINK_DIMENSIONS = new EntityDimensions(0.6f, 0.8f, true);

    @SubscribeEvent
    public static void onTinyPlayer(EntityEvent.Size event) {
        if (event.getEntity().isAddedToWorld() && event.getEntity() instanceof Player player) {
            System.out.println(player.level);
            var cap = PlayerHolderAttacher.getPlayerHolderUnwrap(player);
            if (cap != null) {
                if (cap.isSmall()) {
                    event.setNewSize(SHRINK_DIMENSIONS, false);
                    event.setNewEyeHeight(player.isCrouching() ? 0.55F : 0.7F);
                }
                else {
                    if (event.getOldSize().height != 1.8f) {
                        event.setNewSize(OLD_PLAYER_DIMENSIONS, false);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player instanceof ServerPlayer player && event.phase == TickEvent.Phase.END) {
            var holder = PlayerHolderAttacher.getPlayerHolderUnwrap(player);
            if (holder != null) {
                if (holder.getFrozenTicks() > 0) {
                    holder.decrementFrozenTicks();
                    player.hurtMarked = true;
                    player.setDeltaMovement(player.getDeltaMovement().multiply(0, 0, 0));
                }
                if (holder.getNoWalkTicks() > 0) {
                    holder.decrementWalkTicks();
                }
            }
        }
    }
}
