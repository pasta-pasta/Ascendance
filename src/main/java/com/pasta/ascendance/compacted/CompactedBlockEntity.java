package com.pasta.ascendance.compacted;

import com.pasta.ascendance.Ascendance;
import com.pasta.ascendance.containers.NaniteDamagerMenu;
import com.pasta.ascendance.core.reggers.BlockEntityRegger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class CompactedBlockEntity extends BlockEntity {




    public static final Component TITLE = Component.translatable("container."+ Ascendance.MOD_ID+".nanitedamager");



    public CompactedBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegger.COMPACTEDBLOCKENTITY.get(), pos, state);

    }

    public void tick(Level level, BlockPos pos, BlockState state, CompactedBlockEntity entity){
        if(level.isClientSide()){
            return;
        }

    }


    @Override
    public void load(CompoundTag nbt) {

        super.load(nbt);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {

        super.saveAdditional(nbt);
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
