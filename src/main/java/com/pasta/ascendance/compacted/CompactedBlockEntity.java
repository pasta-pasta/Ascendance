package com.pasta.ascendance.compacted;

import com.pasta.ascendance.Ascendance;
import com.pasta.ascendance.core.reggers.BlockEntityRegger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CompactedBlockEntity extends BlockEntity {




    public static final Component TITLE = Component.translatable("container."+ Ascendance.MOD_ID+".nanitedamager");

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();
    private final ContainerData data;

    private int id;

    public CompactedBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegger.COMPACTEDBLOCKENTITY.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex){
                    case 0 -> CompactedBlockEntity.this.id;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex){
                    case 0 -> CompactedBlockEntity.this.id = pValue;
                }

            }

            @Override
            public int getCount() {
                return 1;
            }
        };

    }

    public void tick(Level level, BlockPos pos, BlockState state, CompactedBlockEntity entity){
        if(level.isClientSide()){
            return;
        }

    }

    public int getId(CompactedBlockEntity entity){
        return entity.data.get(0);
    }

    public void setId(CompactedBlockEntity entity, int value){
        entity.data.set(0, value);
        entity.setChanged();
    }

    // Override this method to write your ID to NBT when Minecraft saves the block entity
    @Override
    public void saveAdditional(CompoundTag compound) {

        compound.putInt("id", id);
        super.saveAdditional(compound);
    }

    // Override this method to read your ID from NBT when Minecraft loads the block entity
    @Override
    public void load(CompoundTag compound) {
        this.id = compound.getInt("id");
        super.load(compound);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {

        return LazyOptional.empty();
    }

    @Override
    public void invalidateCaps() {

    }



    @Override
    public void onLoad() {
        super.onLoad();

    }



}
