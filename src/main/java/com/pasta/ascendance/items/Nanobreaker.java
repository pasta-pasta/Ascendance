package com.pasta.ascendance.items;

import com.google.common.collect.Sets;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraftforge.common.ToolActions.*;

public class Nanobreaker extends PickaxeItem {
    public Nanobreaker(Tier tier, int attackDamageIn, float attackSpeedIn, Properties builder) {
        super(tier, attackDamageIn, attackSpeedIn, builder);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return (float) Double.POSITIVE_INFINITY; // A very high number ensures instant block breaking.
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return true;
    }

    private static final Set<ToolAction> TOOL_ACTIONS =  Stream.of(AXE_DIG, PICKAXE_DIG, SHOVEL_DIG).collect(Collectors.toCollection(Sets::newIdentityHashSet));
    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction)
    {
        return TOOL_ACTIONS.contains(toolAction);
    }
}
