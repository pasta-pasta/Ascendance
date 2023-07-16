package com.pasta.ascendance.blocks.entities;

import com.pasta.ascendance.Ascendance;
import com.pasta.ascendance.containers.NanoInjectorMenu;
import com.pasta.ascendance.recipe.NanoinjectorRecipy;
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
import com.pasta.ascendance.core.reggers.*;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class NanoInjectorEntity extends BlockEntity implements MenuProvider {

    private final ItemStackHandler inventory = new ItemStackHandler(5){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    private final ContainerData data;

    public static final Component TITLE = Component.translatable("container."+ Ascendance.MOD_ID+".nanoinjector");

    private int progress = 0;
    private int maxProgress = 90;
    private int fuel = 0;
    private int maxFuel = 1000;
    private int burnt = 0;
    private int burnLength = 10;

    public NanoInjectorEntity( BlockPos pos, BlockState state) {
        super(BlockEntityRegger.NANOINJECTORENTITY.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index){
                    case 0 -> NanoInjectorEntity.this.progress;
                    case 1 -> NanoInjectorEntity.this.maxProgress;
                    case 2 -> NanoInjectorEntity.this.fuel;
                    case 3 -> NanoInjectorEntity.this.maxFuel;
                    case 4 -> NanoInjectorEntity.this.burnt;
                    case 5 -> NanoInjectorEntity.this.burnLength;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index){
                    case 0 -> NanoInjectorEntity.this.progress = value;
                    case 1 -> NanoInjectorEntity.this.maxProgress = value;
                    case 2 -> NanoInjectorEntity.this.fuel = value;
                    case 3 -> NanoInjectorEntity.this.maxFuel = value;
                    case 4 -> NanoInjectorEntity.this.burnt = value;
                    case 5 -> NanoInjectorEntity.this.burnLength = value;
                }

            }

            @Override
            public int getCount() {
                return 6;
            }
        };
    }

    public void tick(Level level, BlockPos pos, BlockState state, NanoInjectorEntity entity){
        if(level.isClientSide()){
            return;
        }

        if (hasRecipe(entity)){
            entity.progress++;
            entity.fuel--;
            setChanged(level, pos, state);

            if(entity.progress >= entity.maxProgress){
                craftItem(entity);
            }
        }
        else{
            entity.resetProgress();
            setChanged(level, pos, state);
        }
        
        if (hasFuel(entity) && entity.fuel < entity.maxFuel){
            burn(entity);
        }

    }

    private void burn(NanoInjectorEntity entity) {
        entity.burnt++;
        if (entity.burnt >= burnLength){
            entity.inventory.extractItem(0, 1, false);
            entity.inventory.extractItem(1, 1, false);
            entity.fuel = Math.min(entity.fuel+25, entity.maxFuel);
            entity.burnt = 0;
        }

    }

    private boolean hasFuel(NanoInjectorEntity entity) {
        return entity.inventory.getStackInSlot(1).getItem() == ItemRegger.ACIDIC_SUBSTATION.get() &&
                entity.inventory.getStackInSlot(0).getItem() == ItemRegger.NUTRIENT_MEDIUM.get();
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private static void craftItem(NanoInjectorEntity entity) {
        Level level = entity.level;
        SimpleContainer inv = new SimpleContainer(entity.inventory.getSlots());
        for (int i = 0; i < entity.inventory.getSlots(); i++){
            inv.setItem(i, entity.inventory.getStackInSlot(i));
        }
        Optional<NanoinjectorRecipy> recipe = level.getRecipeManager()
                .getRecipeFor(NanoinjectorRecipy.Type.INSTANCE,inv,level);

        if (hasRecipe(entity)){
            entity.inventory.extractItem(2, 1, false);
            entity.inventory.extractItem(3, 1, false);
            entity.inventory.setStackInSlot(4, new ItemStack(recipe.get().getResultItem().getItem(),
                    entity.inventory.getStackInSlot(4).getCount()+1));

            entity.resetProgress();
        }
    }

    private static boolean hasRecipe(NanoInjectorEntity entity) {
        Level level = entity.level;
        SimpleContainer inv = new SimpleContainer(entity.inventory.getSlots());
        for (int i = 0; i < entity.inventory.getSlots(); i++){
            inv.setItem(i, entity.inventory.getStackInSlot(i));
        }
        Optional<NanoinjectorRecipy> recipe = level.getRecipeManager()
                .getRecipeFor(NanoinjectorRecipy.Type.INSTANCE,inv,level);

        return entity.fuel > 0 &&
                recipe.isPresent()&&
                canInsertAmountIntoOutput(inv) &&
                CanInsertItemIntoOutput(inv, recipe.get().getResultItem());

    }

    private static boolean CanInsertItemIntoOutput(SimpleContainer inv, ItemStack itemStack) {
        return inv.getItem(4).getItem() == itemStack.getItem() || inv.getItem(4).isEmpty();
    }

    private static boolean canInsertAmountIntoOutput(SimpleContainer inv) {
        return inv.getItem(4).getMaxStackSize() > inv.getItem(4).getCount();
    }


    @Override
    public void load(CompoundTag nbt) {
        this.inventory.deserializeNBT(nbt.getCompound("Inventory"));
        progress = nbt.getInt("progress");
        fuel = nbt.getInt("fuel");
        burnt = nbt.getInt("burnt");
        super.load(nbt);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("Inventory", this.inventory.serializeNBT());
        nbt.putInt("progress", this.progress);
        nbt.putInt("fuel", this.fuel);
        nbt.putInt("burnt", this.burnt);
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
