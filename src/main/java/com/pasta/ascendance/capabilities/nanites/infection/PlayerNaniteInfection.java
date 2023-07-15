package com.pasta.ascendance.capabilities.nanites.infection;

import net.minecraft.nbt.CompoundTag;

public class PlayerNaniteInfection {
    private int infection;
    private final int MIN_INFECTION = 0;
    private int MAX_INFECTION = 100;

    public int getInfection(){
        return infection;
    }

    public int getMAX_INFECTION(){
        return MAX_INFECTION;
    }

    public void changeInfection(int change){
        this.infection = change > 0 ? Math.min(infection + change, MAX_INFECTION) : Math.max(infection+change, MIN_INFECTION);
    }

    public void copyFrom(PlayerNaniteInfection source){
        this.infection = source.infection;
    }

    public void saveNBTData(CompoundTag nbt){
        nbt.putInt("infection", infection);
    }

    public void loadNBTData(CompoundTag nbt){
        infection = nbt.getInt("infection");
    }

}
