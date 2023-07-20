package com.pasta.ascendance.blocks.entities;

import com.pasta.ascendance.Ascendance;
import com.pasta.ascendance.containers.NaniteDamagerMenu;
import com.pasta.ascendance.core.reggers.BlockEntityRegger;
import com.pasta.ascendance.core.reggers.ItemRegger;
import com.pasta.ascendance.core.server.ASCServerSideHandler;
import com.pasta.ascendance.core.server.packets.EnergySyncS2CPacket;
import com.pasta.ascendance.core.server.packets.FuelSyncS2CPacket;
import com.pasta.ascendance.misc.ASCEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class NaniteDamagerEntity extends BlockEntity implements MenuProvider {

    private final ItemStackHandler inventory = new ItemStackHandler(1){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            ItemStack existing = super.insertItem(slot, stack, simulate);
            if (!simulate && !existing.isEmpty()) {
                existing.setDamageValue(stack.getDamageValue());
            }
            return existing;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack existingStack = super.getStackInSlot(slot);
            ItemStack copyStack = existingStack.copy();
            copyStack.setDamageValue(existingStack.getDamageValue());

            ItemStack extracted = super.extractItem(slot, amount, simulate);
            if (!simulate && !extracted.isEmpty()) {
                return copyStack;
            }
            return extracted;
        }
    };


    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();



    public static final Component TITLE = Component.translatable("container."+ Ascendance.MOD_ID+".nanitedamager");



    public NaniteDamagerEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegger.NANITEDAMAGERENTITY.get(), pos, state);

    }

    public void tick(Level level, BlockPos pos, BlockState state, NaniteDamagerEntity entity){
        if(level.isClientSide()){
            return;
        }

    }

    public void damage(NaniteDamagerEntity entity, int value){
        if (value < 0){
            return;
        }
        ItemStack stack = entity.inventory.getStackInSlot(0).copy();
        stack.setDamageValue(stack.getDamageValue() + value);
        entity.inventory.setStackInSlot(0, stack);
        if (entity.inventory.getStackInSlot(0).getDamageValue() > entity.inventory.getStackInSlot(0).getMaxDamage()){
            entity.inventory.setStackInSlot(0, ItemStack.EMPTY);
        }
        entity.setChanged();
    }



    @Override
    public void load(CompoundTag nbt) {
        this.inventory.deserializeNBT(nbt.getCompound("Inventory"));
        super.load(nbt);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("Inventory", this.inventory.serializeNBT());
        super.saveAdditional(nbt);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER){
            if (side == null){
                return this.lazyItemHandler.cast();
            }

        }
        return LazyOptional.empty();
    }

    @Override
    public void invalidateCaps() {
        this.lazyItemHandler.invalidate();
        this.lazyEnergyHandler.invalidate();
    }

    public ItemStackHandler getInventory(){
        return inventory;
    }
    


    @Override
    public Component getDisplayName() {
        return Component.literal("Nanite damager");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new NaniteDamagerMenu(id, inventory, this);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> inventory);
    }



}
