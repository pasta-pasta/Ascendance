package com.pasta.ascendance.compacted.blocks.entities;

import com.pasta.ascendance.Ascendance;
import com.pasta.ascendance.compacted.core.ASCCompactedFunctions;
import com.pasta.ascendance.compacted.core.EntityMapHolder;
import com.pasta.ascendance.core.reggers.BlockEntityRegger;
import com.pasta.ascendance.core.reggers.DimensionRegger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
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
import java.util.Optional;

public class CompactedDimBlockEntity extends BlockEntity {
    private static final int NUM_SLOTS = 1;

    private int overworldId = -1;
    private int slotId = -1;

    // Track the mode for each side of the block. The default is Mode.BLOCKED for safety.
    private final Map<Direction, Mode> directionModes = new EnumMap<>(Direction.class);

    private final ContainerData data;

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

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex){
                    case 0 -> CompactedDimBlockEntity.this.overworldId;
                    case 1 -> CompactedDimBlockEntity.this.slotId;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex){
                    case 0 -> CompactedDimBlockEntity.this.overworldId = pValue;
                    case 1 -> CompactedDimBlockEntity.this.slotId = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
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
        if (side == null) {
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
        Mode mode = directionModes.computeIfAbsent(side, k -> Mode.INPUT);

        switch (mode) {
            case INPUT:
                directionModes.put(side, Mode.OUTPUT);
                setChanged();
                return "Mode has been changed to OUTPUT for direction: " + side.getName();
            case OUTPUT:
                directionModes.put(side, Mode.BLOCKED);
                setChanged();
                return "Mode has been changed to BLOCKED for direction: " + side.getName();
            case BLOCKED:
                directionModes.put(side, Mode.INPUT);
                setChanged();
                return "Mode has been changed to INPUT for direction: " + side.getName();
        }
        setChanged();
        return "Something went wrong for direction: " + side.getName();
    }

    public void setOverworldId(int id) {
        this.overworldId = id;
    }

    public void setSlotId(int id){
        this.slotId = id;
    }

    public int getSlotId(){
        return this.slotId;
    }

    public int getOverworldId() {
        return this.overworldId;
    }

    @Override
    public void load(CompoundTag compound) {

        inventory.deserializeNBT(compound.getCompound("inventory"));
        this.overworldId = compound.getInt("overworldId");
        this.slotId = compound.getInt("slotId");
        CompoundTag directionModesTag = compound.getCompound("directionModes");
        for (String key : directionModesTag.getAllKeys()) {
            directionModes.put(Direction.valueOf(key), Mode.valueOf(directionModesTag.getString(key)));
        }
        super.load(compound);
    }

    @Override
    public void saveAdditional(CompoundTag compound) {

        compound.put("inventory", this.inventory.serializeNBT());
        compound.putInt("overworldId", this.overworldId);
        compound.putInt("slotId", this.slotId);
        CompoundTag directionModesTag = new CompoundTag();
        for (Map.Entry<Direction, Mode> entry : directionModes.entrySet()) {
            directionModesTag.putString(entry.getKey().name(), entry.getValue().name());
        }
        compound.put("directionModes", directionModesTag);
        super.saveAdditional(compound);
    }

    public void tick(Level level, BlockPos blockPos, BlockState state, CompactedDimBlockEntity compactedBlockEntity) {
        if (compactedBlockEntity.slotId > 1175){
            return;
        }

        ServerLevel serverWorld = (ServerLevel) level;

        ServerLevel overWorldLevel = ((ServerLevel) level).getServer().getLevel(Level.OVERWORLD);
        EntityMapHolder holder = EntityMapHolder.get(overWorldLevel);
        Optional<BlockPos> optionalPos = holder.getBlockEntityPos(compactedBlockEntity.getOverworldId());
        if (optionalPos.isPresent()) {
            BlockPos pos = optionalPos.get();
            ASCCompactedFunctions.updateChunks(serverWorld.getServer().getLevel(Level.OVERWORLD),pos, 1 );

            if (canSendItem(compactedBlockEntity)){
                compactedBlockEntity.sendItem(compactedBlockEntity);
            }

        } else {
            return;
        }
   }

   // Handling inventory sync with overworld. Also in the tick method ^

    private CompactedBlockEntity getSyncedEntity(CompactedDimBlockEntity entity){
        ServerLevel serverWorld = (ServerLevel) entity.getLevel();
        Level overworld = serverWorld.getServer().getLevel(Level.OVERWORLD);
        EntityMapHolder holder = EntityMapHolder.get(serverWorld);
        Optional<BlockPos> optionalPos = holder.getBlockEntityPos(entity.getOverworldId());
        BlockPos pos = optionalPos.get();
        CompactedBlockEntity connectedBlock = (CompactedBlockEntity) overworld.getBlockEntity(pos);

        return connectedBlock;

    }

    private boolean canSendItem(CompactedDimBlockEntity entity){
        CompactedBlockEntity connectedBlock = getSyncedEntity(entity);
        SimpleContainer inv = new SimpleContainer(connectedBlock.getInventory().getSlots());
        SimpleContainer this_inv = new SimpleContainer(entity.inventory.getSlots());
        ItemStack currentItems = this_inv.getItem(0);
        ItemStack itemsInOverworld = inv.getItem(entity.slotId);
        return (((currentItems == itemsInOverworld && itemsInOverworld.getCount() < itemsInOverworld.getMaxStackSize()) || itemsInOverworld.isEmpty())
                && entity.inventory.getStackInSlot(0).getCount() > 0);
    }

    private void sendItem(CompactedDimBlockEntity entity){
        CompactedBlockEntity connectedBlock = getSyncedEntity(entity);
        SimpleContainer inv = new SimpleContainer(connectedBlock.getInventory().getSlots());
        SimpleContainer this_inv = new SimpleContainer(entity.inventory.getSlots());
        connectedBlock.setItemInSlot(connectedBlock, entity.slotId, entity.inventory.getStackInSlot(0));
        entity.inventory.extractItem(0, 1, false);
        entity.setChanged();
    }

    public enum Mode {
        INPUT,
        OUTPUT,
        BLOCKED
    }
}