package com.pasta.ascendance.compacted.core;

import com.pasta.ascendance.Ascendance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class IDManager extends SavedData {
    private int currentMaxID = 0;

    public int getNextID() {
        currentMaxID++;
        this.setDirty();
        this.save(new CompoundTag());
        Ascendance.LOGGER.info("Got dimension ID: " + currentMaxID);
        return currentMaxID;
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        nbt.putInt("currentMaxID", currentMaxID);
        return nbt;
    }

    public static IDManager load(CompoundTag nbt) {
        int maxId = nbt.getInt("currentMaxID");
        IDManager manager = new IDManager();
        manager.currentMaxID = maxId;
        return manager;
    }

    public static IDManager get(ServerLevel world) {
        return world.getDataStorage().computeIfAbsent(IDManager::load, IDManager::new, "currentMaxId");
    }


}