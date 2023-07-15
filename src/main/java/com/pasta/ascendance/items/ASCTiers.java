package com.pasta.ascendance.items;


import com.pasta.ascendance.core.reggers.ItemRegger;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public enum ASCTiers implements Tier {
    MERALIUM_TIER(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 5, () -> {
        return Ingredient.of(ItemRegger.MERALIUM_INGOT.get());
    });

    private final int level;
    private final int uses;
    private final float speed;
    private final float damage;
    private final int enchantmentValue;
    private final Supplier<Ingredient> repairIngredient;

    ASCTiers(double level, double durability, double miningSpeed, double damage, int enchantability, Supplier<Ingredient> repairIngredient) {
        this.level = (int) level;
        this.uses = (int) durability;
        this.speed = (float) miningSpeed;
        this.damage = (float) damage;
        this.enchantmentValue = enchantability;
        this.repairIngredient = repairIngredient;
    }

    public int getUses() {
        return this.uses;
    }

    public float getSpeed() {
        return this.speed;
    }

    public float getAttackDamageBonus() {
        return this.damage;
    }

    public int getLevel() {
        return this.level;
    }

    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    public @NotNull Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }
}

