package com.pasta.ascendance.blocks.entities;

import com.pasta.ascendance.Ascendance;
import com.pasta.ascendance.containers.NanoInjectorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.pasta.ascendance.core.reggers.*;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NanoInjectorEntity extends BlockEntity implements MenuProvider {

    private final ItemStackHandler inventory = new ItemStackHandler(4){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    private final ContainerData data;

    public static final Component TITLE = Component.translatable("container."+ Ascendance.MOD_ID+".nanoinjector");

    public NanoInjectorEntity( BlockPos pos, BlockState state) {
        super(BlockEntityRegger.NANOINJECTORENTITY.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return 0;
            }

            @Override
            public void set(int index, int value) {

            }

            @Override
            public int getCount() {
                return 0;
            }
        };
    }

    public void tick(Level level, BlockPos pos, BlockState state, NanoInjectorEntity entity){
        if(level.isClientSide()){
            return;
        }


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
        return cap == ForgeCapabilities.ITEM_HANDLER ? this.lazyItemHandler.cast() : super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        this.lazyItemHandler.invalidate();
    }

    public ItemStackHandler getInventory(){
        return inventory;
    }

    public ContainerData getContainerData(){
        return this.data;
    }


    @Override
    public Component getDisplayName() {
        return Component.literal("Nanoinjector");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new NanoInjectorMenu(id, inventory, this, this.data);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> inventory);
    }
}
