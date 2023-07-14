package com.divinity.anythingisay.event;

import com.divinity.anythingisay.AnythingISay;
import com.divinity.anythingisay.cap.PlayerHolderAttacher;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.ResourceLocationException;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.EntitySummonArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
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

import java.util.Optional;

@Mod.EventBusSubscriber(modid = AnythingISay.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonForgeEvents {

    private static final EntityDimensions OLD_PLAYER_DIMENSIONS = new EntityDimensions(0.6f, 1.8f, false);
    private static final EntityDimensions SHRINK_DIMENSIONS = new EntityDimensions(0.6f, 0.8f, true);
    private static final DynamicCommandExceptionType ERROR_TEMPLATE_INVALID = new DynamicCommandExceptionType((p_214582_) -> Component.translatable("commands.place.template.invalid", p_214582_));
    private static final SimpleCommandExceptionType ERROR_TEMPLATE_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.place.template.failed"));
    private static final ResourceLocation MAZE = new ResourceLocation(AnythingISay.MODID, "maze");
    private static final ResourceLocation MOON = new ResourceLocation(AnythingISay.MODID, "moon");

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
        ForgeRegistries.ENTITY_TYPES.getKeys().forEach(s -> {
            String[] st = s.toString().split(":");
            dispatcher.register(Commands.literal(st[st.length - 1]) // Getting just the name of the mob without the modid prefix
                    .then(Commands.argument("player", EntityArgument.player())
                            .executes(context -> {
                                Player player = EntityArgument.getPlayer(context, "player");
                                EntityType.byString(s.toString()).ifPresent(t -> {
                                    int amount = (s.toString().contains("kier") || s.toString().contains("dev")) ? 20 : 10;
                                    for (int i = 0; i < amount; i++) {
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
                                holder.setSmallTicks(60 * 20);
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
        dispatcher.register(Commands.literal("maze")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> {
                            var player = EntityArgument.getPlayer(context, "player");
                            return placeTemplate(context.getSource(), player, MAZE, new BlockPos(context.getSource().getPosition()), 22, 1, 25, Rotation.NONE, Mirror.NONE, 1.0F, 0);
                        })
                )
        );
        dispatcher.register(Commands.literal("moon")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> {
                            var player = EntityArgument.getPlayer(context, "player");
                            var pos = context.getSource().getPosition();
                            return placeTemplate(context.getSource(), player, MOON, new BlockPos(pos.x, pos.y + 60, pos.z), 14, 40, 14, Rotation.NONE, Mirror.NONE, 1.0F, 0);
                        })
                )
        );
    }

    @SubscribeEvent
    public static void onTinyPlayer(EntityEvent.Size event) {
        if (event.getEntity().isAddedToWorld() && event.getEntity() instanceof Player player) {
            var cap = PlayerHolderAttacher.getPlayerHolderUnwrap(player);
            if (cap != null) {
                if (cap.getSmallTicks() > 0) {
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
                if (holder.getSmallTicks() > 0) {
                    holder.decrementSmallTicks();
                }
            }
        }
    }

    public static int placeTemplate(CommandSourceStack pSource, ServerPlayer player, ResourceLocation pTemplate, BlockPos pPos, int tpOffsetX, int tpOffsetY, int tpOffsetZ, Rotation pRotation, Mirror pMirror, float pIntegrity, int pSeed) throws CommandSyntaxException {
        ServerLevel serverlevel = pSource.getLevel();
        StructureTemplateManager structuretemplatemanager = serverlevel.getStructureManager();
        Optional<StructureTemplate> optional;
        try {
            optional = structuretemplatemanager.get(pTemplate);
        }
        catch (ResourceLocationException resourcelocationexception) {
            throw ERROR_TEMPLATE_INVALID.create(pTemplate);
        }

        if (optional.isEmpty()) {
            throw ERROR_TEMPLATE_INVALID.create(pTemplate);
        }
        else {
            StructureTemplate structuretemplate = optional.get();
            checkLoaded(serverlevel, new ChunkPos(pPos), new ChunkPos(pPos.offset(structuretemplate.getSize())));
            StructurePlaceSettings structureplacesettings = (new StructurePlaceSettings()).setMirror(pMirror).setRotation(pRotation);
            if (pIntegrity < 1.0F) {
                structureplacesettings.clearProcessors().addProcessor(new BlockRotProcessor(pIntegrity)).setRandom(StructureBlockEntity.createRandom((long)pSeed));
            }

            boolean flag = structuretemplate.placeInWorld(serverlevel, pPos, pPos, structureplacesettings, StructureBlockEntity.createRandom((long)pSeed), 2);
            if (!flag) {
                throw ERROR_TEMPLATE_FAILED.create();
            }
            else {
                player.teleportTo(pPos.getX() + tpOffsetX, pPos.getY() + tpOffsetY, pPos.getZ() + tpOffsetZ); // Position player to a specific place in the structure
                return 1;
            }
        }
    }

    private static void checkLoaded(ServerLevel pLevel, ChunkPos pStart, ChunkPos pEnd) throws CommandSyntaxException {
        if (ChunkPos.rangeClosed(pStart, pEnd).anyMatch((p_214542_) -> !pLevel.isLoaded(p_214542_.getWorldPosition()))) {
            throw BlockPosArgument.ERROR_NOT_LOADED.create();
        }
    }
}
