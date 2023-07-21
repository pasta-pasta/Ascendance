package com.pasta.ascendance.compacted.core;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EntityMapHolder extends SavedData {
    private static final String DATA_NAME = "entityMap";
    private Map<Integer, BlockPos> blockEntityMap = new HashMap<>();

    public void registerBlockEntity(int id, BlockEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        blockEntityMap.put(id, pos);
        setDirty();
    }

    public void unregisterBlockEntity(int id) {
        blockEntityMap.remove(id);
        setDirty();
    }

    public Optional<BlockPos> getBlockEntityPos(int id) {
        return Optional.ofNullable(blockEntityMap.get(id));
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        ListTag mapList = new ListTag();
        for (Map.Entry<Integer, BlockPos> entry : blockEntityMap.entrySet()) {
            CompoundTag entryNbt = new CompoundTag();
            entryNbt.putInt("id", entry.getKey());
            entryNbt.putLong("pos", entry.getValue().asLong());
            mapList.add(entryNbt);
        }
        nbt.put(DATA_NAME, mapList);
        return nbt;
    }

    public static EntityMapHolder load(CompoundTag nbt) {
        EntityMapHolder holder = new EntityMapHolder();
        ListTag mapList = nbt.getList(DATA_NAME, 10);  // 10 is the NBT type for CompoundTag
        for (int i = 0; i < mapList.size(); i++) {
            CompoundTag entryNbt = mapList.getCompound(i);
            int id = entryNbt.getInt("id");
            long posLong = entryNbt.getLong("pos");
            BlockPos pos = BlockPos.of(posLong);
            holder.blockEntityMap.put(id, pos);
        }
        return holder;
    }

    public static EntityMapHolder get(ServerLevel world) {
        return world.getDataStorage().computeIfAbsent(EntityMapHolder::load, EntityMapHolder::new, DATA_NAME);
    }

}
