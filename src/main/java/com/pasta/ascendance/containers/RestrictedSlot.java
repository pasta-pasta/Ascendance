package com.pasta.ascendance.containers;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class RestrictedSlot extends SlotItemHandler {
    private final Predicate<ItemStack> validator;
    public RestrictedSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition,
                          Predicate<ItemStack> validator) {
        super(itemHandler, index, xPosition, yPosition);
        this.validator = validator;
    }

    public RestrictedSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        this(itemHandler, index, xPosition, yPosition, itemStack -> false);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return this.validator.test(stack);
    }
}
