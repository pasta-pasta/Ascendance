package com.pasta.ascendance.blocks.entities;

import com.pasta.ascendance.Ascendance;
import com.pasta.ascendance.blocks.NanoInjector;
import com.pasta.ascendance.containers.NanoInjectorMenu;
import com.pasta.ascendance.core.reggers.BlockEntityRegger;
import com.pasta.ascendance.core.reggers.ItemRegger;
import com.pasta.ascendance.core.server.ASCServerSideHandler;
import com.pasta.ascendance.core.server.packets.EnergySyncS2CPacket;
import com.pasta.ascendance.core.server.packets.FuelSyncS2CPacket;
import com.pasta.ascendance.misc.ASCEnergyStorage;
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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class NanoInjectorEntity extends BlockEntity implements MenuProvider {

    private final ItemStackHandler inventory = new ItemStackHandler(5){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot){
                case 0 -> stack.getItem() == ItemRegger.NUTRIENT_MEDIUM.get();
                case 1 -> stack.getItem() == ItemRegger.ACIDIC_SUBSTATION.get();
                case 2 -> stack.getItem() == ItemRegger.AGGRESIVE_COLONY.get()
                        || stack.getItem() == ItemRegger.SMALLCOLONY.get()
                        || stack.getItem() == ItemRegger.GUARD_COLONY.get();
                case 3 -> stack.getItem() == Items.DIAMOND
                        || stack.getItem() == Items.REDSTONE
                        || stack.getItem() == Items.LAVA_BUCKET;
                case 4 -> false;
                default -> super.isItemValid(slot, stack);
            };
        }
    };

    private final ASCEnergyStorage ENERGY_STORAGE = new ASCEnergyStorage(1200000, 12000) {
        @Override
        public void onEnergyChanged() {
            setChanged();
            ASCServerSideHandler.sendToClients(new EnergySyncS2CPacket(this.energy, getBlockPos()));
        }
    };

    private static final int ENERGY_REQ = 320;

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();
    private final Map<Direction, LazyOptional<WrappedHandler>> directionWrappedHandlerMap =
            Map.of(Direction.DOWN, LazyOptional.of(() -> new WrappedHandler(inventory, (i) -> i == 4, (i, s) -> false)),
                    Direction.NORTH, LazyOptional.of(() -> new WrappedHandler(inventory, (index) -> index == 0,
                            (index, stack) -> inventory.isItemValid(0, stack))),
                    Direction.SOUTH, LazyOptional.of(() -> new WrappedHandler(inventory, (index) -> index == 1,
                            (index, stack) -> inventory.isItemValid(1, stack))),
                    Direction.EAST, LazyOptional.of(() -> new WrappedHandler(inventory, (index) -> index == 2,
                            (index, stack) -> inventory.isItemValid(2, stack))),
                    Direction.WEST, LazyOptional.of(() -> new WrappedHandler(inventory, (index) -> index == 3,
                            (index, stack) -> inventory.isItemValid(3, stack))));

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

    public IEnergyStorage getEnergyStorage() {
        return ENERGY_STORAGE;
    }

    public List<Component> getFuelTooltip(NanoInjectorEntity entity) {
        return List.of(Component.literal(entity.fuel+"/"+entity.maxFuel+" Biounits"));
    }

    public void setEnergyLevel(int energy) {
        this.ENERGY_STORAGE.setEnergy(energy);
    }

    public void setFuel(int fuel){
        this.fuel = fuel;
    }

    public void tick(Level level, BlockPos pos, BlockState state, NanoInjectorEntity entity){
        if(level.isClientSide()){
            return;
        }



        if (hasRecipe(entity) && hasEnergy(entity)){
            entity.progress++;
            entity.fuel--;
            entity.ENERGY_STORAGE.extractEnergy(ENERGY_REQ, false);
            setChanged(level, pos, state);

            if(entity.progress >= entity.maxProgress){
                craftItem(entity);
            }
        }
        else{
            entity.resetProgress();
            setChanged(level, pos, state);
        }
        
        if (hasFuel(entity) && entity.fuel <= entity.maxFuel-25){
            burn(entity);
        }

    }

    private boolean hasEnergy(NanoInjectorEntity entity) {
        return entity.ENERGY_STORAGE.getEnergyStored() >= ENERGY_REQ;
    }

    private void burn(NanoInjectorEntity entity) {
        entity.burnt++;
        if (entity.burnt >= burnLength){
            entity.inventory.extractItem(0, 1, false);
            entity.inventory.extractItem(1, 1, false);
            entity.fuel = Math.min(entity.fuel+25, entity.maxFuel);
            entity.burnt = 0;
        }
        ASCServerSideHandler.sendToClients(new FuelSyncS2CPacket(this.fuel, getBlockPos()));

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
        setEnergyLevel(nbt.getInt("nanoinjector.energy"));
        super.load(nbt);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("Inventory", this.inventory.serializeNBT());
        nbt.putInt("nanoinjector.energy", ENERGY_STORAGE.getEnergyStored());
        nbt.putInt("progress", this.progress);
        nbt.putInt("fuel", this.fuel);
        nbt.putInt("burnt", this.burnt);
        super.saveAdditional(nbt);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY){
            return lazyEnergyHandler.cast();
        }


        if (cap == ForgeCapabilities.ITEM_HANDLER){
            if (side == null){
                return this.lazyItemHandler.cast();
            }

            if (directionWrappedHandlerMap.containsKey(side)){
                Direction localDir = this.getBlockState().getValue(NanoInjector.FACING);

                if (side == Direction.UP || side == Direction.DOWN){
                    return directionWrappedHandlerMap.get(side).cast();
                }

                switch (localDir){
                    default:
                        return directionWrappedHandlerMap.get(side.getOpposite()).cast();
                    case EAST:
                        return directionWrappedHandlerMap.get(side.getClockWise()).cast();
                    case SOUTH:
                        return directionWrappedHandlerMap.get(side).cast();
                    case WEST:
                        return directionWrappedHandlerMap.get(side.getCounterClockWise()).cast();
                }
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
        ASCServerSideHandler.sendToClients(new FuelSyncS2CPacket(this.fuel, getBlockPos()));
        ASCServerSideHandler.sendToClients(new EnergySyncS2CPacket(ENERGY_STORAGE.getEnergyStored(), getBlockPos()));
        return new NanoInjectorMenu(id, inventory, this, this.data);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> inventory);
        lazyEnergyHandler = LazyOptional.of(() -> ENERGY_STORAGE);
    }



}
