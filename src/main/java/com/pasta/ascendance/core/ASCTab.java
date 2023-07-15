package com.pasta.ascendance.core;

import com.pasta.ascendance.core.reggers.ItemRegger;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ASCTab extends CreativeModeTab {
    private ASCTab(int index, String label) {
        super(index, label);
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(ItemRegger.SPEAR.get());
    }

    public static final ASCTab instance = new ASCTab(CreativeModeTab.TABS.length, "asctab");
}