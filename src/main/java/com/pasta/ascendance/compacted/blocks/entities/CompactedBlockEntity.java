package com.pasta.ascendance.compacted.blocks.entities;

import com.pasta.ascendance.Ascendance;
import com.pasta.ascendance.blocks.entities.WrappedHandler;
import com.pasta.ascendance.compacted.core.ASCCompactedFunctions;
import com.pasta.ascendance.core.reggers.BlockEntityRegger;
import com.pasta.ascendance.core.reggers.DimensionRegger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerData;
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

import java.util.EnumMap;
import java.util.Map;

public class CompactedBlockEntity extends BlockEntity {




    public static final Component TITLE = Component.translatable("container."+ Ascendance.MOD_ID+".compacted_block");

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    private final ItemStackHandler inventory = new ItemStackHandler(1176) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return true;
        }
    };
    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();
    private final ContainerData data;

    private int id;

    //INVENTORY HANDLING
    private final Map<Direction, Mode> directionModes = new EnumMap<>(Direction.class);

    private final Map<Direction, LazyOptional<WrappedHandler>> directionWrappedHandlerMap = new EnumMap<>(Direction.class);

    public enum Mode {
        INPUT,
        OUTPUT,
        BLOCKED
    }

    public void setMode(Direction direction, Mode mode) {
        directionModes.put(direction, mode);
        setChanged();
    }

    public Mode getMode(Direction direction) {
        return directionModes.get(direction);
    }

    public String cycleSideMode(Direction side) {
        CompactedBlockEntity.Mode mode = directionModes.get(side);
        switch (mode) {
            case INPUT:
                directionModes.put(side, CompactedBlockEntity.Mode.OUTPUT);
                return "Mode has been changed to OUTPUT!";
            case OUTPUT:
                directionModes.put(side, CompactedBlockEntity.Mode.BLOCKED);
                return "Mode has been changed to BLOCKED!";
            case BLOCKED:
                directionModes.put(side, CompactedBlockEntity.Mode.INPUT);
                return "Mode has been changed to INPUT!";
        }
        setChanged();
        return "Something went wrong!";
    }

    public IItemHandler getInventory(){
        return this.inventory;
    }

    public void setItemInSlot(CompactedBlockEntity entity, int slotId, ItemStack stack){
        SimpleContainer inv = new SimpleContainer(entity.inventory.getSlots());
        if (canInsertAmountIntoSlot(inv, slotId) && canInsertItemIntoSlot(inv, stack, slotId)){

            if (entity.inventory.getStackInSlot(slotId).isEmpty()){
                entity.inventory.setStackInSlot(slotId,
                        new ItemStack(stack.getItem(), entity.inventory.getStackInSlot(slotId).getCount() + 1));
            }
            else{
                entity.inventory.setStackInSlot(slotId,
                        new ItemStack(entity.inventory.getStackInSlot(slotId).getItem(), entity.inventory.getStackInSlot(slotId).getCount() + 1));
            }
            entity.setChanged();

        }
    }

    public ItemStack getItemInSlot(CompactedBlockEntity entity, int slotId){
        return entity.inventory.getStackInSlot(slotId);
    }

    private boolean canInsertItemIntoSlot(SimpleContainer inv, ItemStack itemStack, int index) {
        return inv.getItem(index).getItem() == itemStack.getItem() || inv.getItem(index).isEmpty();
    }

    private boolean canInsertAmountIntoSlot(SimpleContainer inv, int index) {
        return inv.getItem(index).getMaxStackSize() > inv.getItem(index).getCount();
    }

    //EVERYTHING ELSE

    public CompactedBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegger.COMPACTEDBLOCKENTITY.get(), pos, state);

        for (Direction direction : Direction.values()) {
            directionModes.put(direction, Mode.BLOCKED);
        }

        Map<Direction, Integer> directionToStartSlotMap = new EnumMap<>(Direction.class);
        directionToStartSlotMap.put(Direction.DOWN, 0);
        directionToStartSlotMap.put(Direction.UP, 196);
        directionToStartSlotMap.put(Direction.NORTH, 2 * 196);
        directionToStartSlotMap.put(Direction.SOUTH, 3 * 196);
        directionToStartSlotMap.put(Direction.WEST, 4 * 196);
        directionToStartSlotMap.put(Direction.EAST, 5 * 196);

        for (Direction direction : Direction.values()) {
            int startSlot = directionToStartSlotMap.get(direction);
            directionWrappedHandlerMap.put(direction, LazyOptional.of(() -> new WrappedHandler(inventory,
                    slot -> slot >= startSlot && slot < startSlot + 196,
                    (slot, stack) -> directionModes.get(direction) == Mode.BLOCKED)
            ));
        }


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

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER && side != null) {
            return directionWrappedHandlerMap.get(side).cast();
        }
        return LazyOptional.empty();
    }

    public void tick(Level level, BlockPos pos, BlockState state, CompactedBlockEntity entity){
        if(level.isClientSide()){
            return;
        }
        ServerLevel serverWorld = (ServerLevel) level;
        ServerLevel compactedWorld = serverWorld.getServer().getLevel(DimensionRegger.ASCDIM_KEY);
        ASCCompactedFunctions.updateChunks(compactedWorld, ASCCompactedFunctions.getValidBoxPos(entity.getId(), false), 2);

    }

    public int getId(){
        return this.data.get(0);
    }

    public void setId(CompactedBlockEntity entity, int value){
        this.data.set(0, value);
        setChanged();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }
    @Override
    public void load(CompoundTag nbt) {
        this.inventory.deserializeNBT(nbt.getCompound("Inventory"));
        this.id = nbt.getInt("compactedId");
        CompoundTag directionModesTag = nbt.getCompound("directionModes");
        for (String key : directionModesTag.getAllKeys()) {
            directionModes.put(Direction.valueOf(key), Mode.valueOf(directionModesTag.getString(key)));
        }
        super.load(nbt);
    }


    @Override
    public void saveAdditional(CompoundTag nbt) {
        nbt.put("Inventory", this.inventory.serializeNBT());
        nbt.putInt("compactedId", this.id);
        CompoundTag directionModesTag = new CompoundTag();
        for (Map.Entry<Direction, Mode> entry : directionModes.entrySet()) {
            directionModesTag.putString(entry.getKey().name(), entry.getValue().name());
        }
        nbt.put("directionModes", directionModesTag);
        super.saveAdditional(nbt);
    }


    @Override
    public void invalidateCaps() {

    }


    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> inventory);
    }


}
