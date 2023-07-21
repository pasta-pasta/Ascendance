package com.pasta.ascendance.compacted.blocks.entities;

import com.pasta.ascendance.core.reggers.BlockEntityRegger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

public class CompactedDimBlockEntity extends BlockEntity {
    private static final int NUM_SLOTS = 1;

    // Track the mode for each side of the block. The default is Mode.BLOCKED for safety.
    private final Map<Direction, Mode> directionModes = new EnumMap<>(Direction.class);

    private final ItemStackHandler inventory = new ItemStackHandler(NUM_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyHandler = LazyOptional.empty();

    public CompactedDimBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegger.COMPACTEDDIMBLOCKENTITY.get(), pos, state);

        // Initialize all sides to Mode.BLOCKED
        for (Direction direction : Direction.values()) {
            directionModes.put(direction, Mode.BLOCKED);
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyHandler = LazyOptional.of(() -> inventory);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyHandler.invalidate();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (side == null || side == Direction.UP || side == Direction.DOWN) {
            return LazyOptional.empty();
        }

        Mode mode = directionModes.get(side);
        if (mode == Mode.BLOCKED || cap != ForgeCapabilities.ITEM_HANDLER) {
            return LazyOptional.empty();
        }

        return lazyHandler.cast();
    }

    public void setMode(Direction direction, Mode mode) {
        directionModes.put(direction, mode);
        setChanged();
    }

    public Mode getMode(Direction direction) {
        return directionModes.get(direction);
    }

    public String cycleSideMode(Direction side) {
        Mode mode = directionModes.get(side);
        switch (mode) {
            case INPUT:
                directionModes.put(side, Mode.OUTPUT);
                return "Mode has been changed to OUTPUT!";
            case OUTPUT:
                directionModes.put(side, Mode.BLOCKED);
                return "Mode has been changed to BLOCKED!";
            case BLOCKED:
                directionModes.put(side, Mode.INPUT);
                return "Mode has been changed to INPUT!";
        }
        setChanged();
        return "Something went wrong!";
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        inventory.deserializeNBT(compound.getCompound("inventory"));

        CompoundTag directionModesTag = compound.getCompound("directionModes");
        for (String key : directionModesTag.getAllKeys()) {
            directionModes.put(Direction.valueOf(key), Mode.valueOf(directionModesTag.getString(key)));
        }
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.put("inventory", inventory.serializeNBT());

        CompoundTag directionModesTag = new CompoundTag();
        for (Map.Entry<Direction, Mode> entry : directionModes.entrySet()) {
            directionModesTag.putString(entry.getKey().name(), entry.getValue().name());
        }
        compound.put("directionModes", directionModesTag);
    }

    public void tick(Level level, BlockPos blockPos, BlockState state, CompactedDimBlockEntity compactedBlockEntity) {

    }

    public enum Mode {
        INPUT,
        OUTPUT,
        BLOCKED
    }
}