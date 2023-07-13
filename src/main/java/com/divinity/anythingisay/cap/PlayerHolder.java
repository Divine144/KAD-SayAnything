package com.divinity.anythingisay.cap;

import com.divinity.anythingisay.network.NetworkHandler;
import com.divinity.anythingisay.network.clientbound.UpdateDimensionsPacket;
import dev._100media.capabilitysyncer.core.PlayerCapability;
import dev._100media.capabilitysyncer.network.EntityCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PlayerHolder extends PlayerCapability {
    private int frozenTicks;
    private int noWalkTicks;
    private boolean twisted;
    private boolean small;
    private boolean isRainingLava;

    protected PlayerHolder(Player entity) {
        super(entity);
        this.frozenTicks = 0;
        this.noWalkTicks = 0;
        this.twisted = false;
        this.small = false;
        this.isRainingLava = false;
    }

    @Override
    public CompoundTag serializeNBT(boolean savingToDisk) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("frozen", this.frozenTicks);
        tag.putInt("noWalk", this.noWalkTicks);
        tag.putBoolean("twist", this.twisted);
        tag.putBoolean("small", this.small);
        tag.putBoolean("lava", this.isRainingLava);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
        this.frozenTicks = nbt.getInt("frozen");
        this.noWalkTicks = nbt.getInt("noWalk");
        this.twisted = nbt.getBoolean("twist");
        this.small = nbt.getBoolean("small");
        this.isRainingLava = nbt.getBoolean("lava");
    }

    @Override
    public EntityCapabilityStatusPacket createUpdatePacket() {
        return new SimpleEntityCapabilityStatusPacket(this.entity.getId(), PlayerHolderAttacher.EXAMPLE_RL, this);
    }

    @Override
    public SimpleChannel getNetworkChannel() {
        return NetworkHandler.INSTANCE;
    }


    public int getFrozenTicks() {
        return frozenTicks;
    }

    public void decrementFrozenTicks() {
        if (--this.frozenTicks < 0) {
            setFrozenTicks(0);
        }
        if (frozenTicks == 0) {
            updateTracking();
        }
    }

    public void setFrozenTicks(int frozenTicks) {
        this.frozenTicks = frozenTicks;
        updateTracking();
    }

    public void syncTracking() {
        updateTracking();
    }

    public int getNoWalkTicks() {
        return noWalkTicks;
    }

    public void decrementWalkTicks() {
        if (--this.noWalkTicks < 0) {
            setNoWalkTicks(0);
        }
        if (noWalkTicks == 0) {
            updateTracking();
        }
    }

    public void setNoWalkTicks(int noWalkTicks) {
        this.noWalkTicks = noWalkTicks;
        updateTracking();
    }

    public boolean isTwisted() {
        return twisted;
    }

    public void setTwisted(boolean twisted) {
        this.twisted = twisted;
        updateTracking();
    }

    public boolean isSmall() {
        return small;
    }

    public void setSmall(boolean small) {
        this.small = small;
        updateTracking();
        updateDimensions();
    }

    public void updateDimensions() {
        if (this.entity.level.isClientSide)
            return;
        this.entity.refreshDimensions();
        getNetworkChannel().send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this.entity), new UpdateDimensionsPacket(this.entity.getUUID()));
    }

    public boolean isRainingLava() {
        return isRainingLava;
    }

    public void setRainingLava(boolean rainingLava) {
        isRainingLava = rainingLava;
        updateTracking();
    }
}