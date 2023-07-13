package com.divinity.anythingisay.network.clientbound;

import com.divinity.anythingisay.network.ClientHandler;
import dev._100media.capabilitysyncer.network.IPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.UUID;

public class UpdateDimensionsPacket implements IPacket {

    private final UUID playerUUID;

    public UpdateDimensionsPacket(UUID player) {
        this.playerUUID = player;
    }

    @Override
    public void write(FriendlyByteBuf packetBuf) {
        packetBuf.writeUUID(playerUUID);
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            context.enqueueWork(() -> ClientHandler.updateDimensions(playerUUID));
        }
    }

    public static UpdateDimensionsPacket read(FriendlyByteBuf packetBuf) {
        return new UpdateDimensionsPacket(packetBuf.readUUID());
    }

    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_CLIENT, UpdateDimensionsPacket.class, UpdateDimensionsPacket::read);
    }
}
