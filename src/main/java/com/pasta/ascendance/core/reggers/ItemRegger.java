package com.pasta.ascendance.core.reggers;

import com.pasta.ascendance.Ascendance;
import com.pasta.ascendance.core.ASCTab;
import com.pasta.ascendance.items.*;
import com.pasta.ascendance.items.curios.AggressiveInjection;
import com.pasta.ascendance.items.curios.AgressiveSmallColony;
import com.pasta.ascendance.items.curios.GuardInjection;
import com.pasta.ascendance.items.curios.SleepingInjection;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.pasta.ascendance.core.reggers.BlockRegger.*;

public class ItemRegger {
    public static final DeferredRegister<Item> ITEMS
            = DeferredRegister.create(ForgeRegistries.ITEMS, Ascendance.MOD_ID);

    public static final RegistryObject<Item> SPEAR = ITEMS.register("spear",
            () -> new Spear(new Item.Properties().tab(ASCTab.instance).stacksTo(1)));

    public static final RegistryObject<Item> MERALIUM_INGOT = ITEMS.register("meralium_ingot",
            () -> new Item(new Item.Properties().tab(ASCTab.instance)));

    public static final RegistryObject<Item> EMPTY_INJECTION = ITEMS.register("empty_injection",
            () -> new Item(new Item.Properties().tab(ASCTab.instance)));

    public static final RegistryObject<Item> GUARD_COLONY = ITEMS.register("guard_colony",
            () -> new Item(new Item.Properties().tab(ASCTab.instance)));

    public static final RegistryObject<Item> SLEEPING_INJECTION = ITEMS.register("sleeping_injection",
            () -> new SleepingInjection(new Item.Properties().tab(ASCTab.instance).stacksTo(1).durability(1000)));

    public static final RegistryObject<Item> GUARD_INJECTION = ITEMS.register("guard_injection",
            () -> new GuardInjection(new Item.Properties().tab(ASCTab.instance).stacksTo(1)));

    public static final RegistryObject<Item> AGGRESSIVE_INJECTION = ITEMS.register("aggressive_injection",
            () -> new AggressiveInjection(new Item.Properties().tab(ASCTab.instance).stacksTo(1)));

    public static final RegistryObject<Item> SMALLCOLONY = ITEMS.register("small_colony",
            () -> new Item(new Item.Properties().tab(ASCTab.instance)));

    public static final RegistryObject<Item> AGGRESIVE_COLONY = ITEMS.register("aggressive_colony",
            () -> new AgressiveSmallColony(new Item.Properties().tab(ASCTab.instance)));

    public static final RegistryObject<Item> NANITE_EXTRACTOR = ITEMS.register("nanite_extractor",
            () -> new Item(new Item.Properties().tab(ASCTab.instance).stacksTo(1).durability(100)));

    public static final RegistryObject<Item> ACIDIC_SUBSTATION = ITEMS.register("acid",
            () -> new Item(new Item.Properties().tab(ASCTab.instance)));

    public static final RegistryObject<Item> NUTRIENT_MEDIUM = ITEMS.register("nutrient_medium",
            () -> new Item(new Item.Properties().tab(ASCTab.instance)));

    public static final RegistryObject<Item> NANITE_KILLER = ITEMS.register("nanite_killer",
            () -> new NaniteKiller(new Item.Properties().tab(ASCTab.instance).stacksTo(1)));

    public static final RegistryObject<Item> MERALIUM_HELMET = ITEMS.register("meralium_helmet", () ->
            new MeraliumArmorGecko(Meralium.MERALIUM, EquipmentSlot.HEAD)
    );

    public static final RegistryObject<Item> MERALIUM_CHESTPLATE = ITEMS.register("meralium_chestplate", () ->
            new MeraliumArmorGecko(Meralium.MERALIUM, EquipmentSlot.CHEST)
    );

    public static final RegistryObject<Item> MERALIUM_LEGGINS = ITEMS.register("meralium_leggins", () ->
            new MeraliumArmorGecko(Meralium.MERALIUM, EquipmentSlot.LEGS)
    );

    public static final RegistryObject<Item> MERALIUM_BOOTS = ITEMS.register("meralium_boots", () ->
            new MeraliumArmorGecko(Meralium.MERALIUM, EquipmentSlot.FEET)
    );

    public static final RegistryObject<Item> NANOBREAKER = ITEMS.register("nanobreaker", () ->
            new Nanobreaker(ASCTiers.MERALIUM_TIER, Integer.MAX_VALUE, Integer.MAX_VALUE, new Item.Properties().tab(ASCTab.instance)));

    public static final RegistryObject<Item> NANOCOLONY_ITEM = ITEMS.register("nanocolony_item", () ->
            new BlockItem(NANOCOLONY.get(), new Item.Properties().tab(ASCTab.instance))
    );

    public static final RegistryObject<Item> NANITE_DOOR_ITEM = ITEMS.register("nanite_door_item", () ->
            new BlockItem(NANITE_DOOR.get(), new Item.Properties().tab(ASCTab.instance))
    );

    public static final RegistryObject<Item> GUARDCOLONY_ITEM = ITEMS.register("guardcolony_item", () ->
            new BlockItem(GUARD_COLONY_BLOCK.get(), new Item.Properties().tab(ASCTab.instance))
    );

    public static final RegistryObject<Item> AGGRESSIVECOLONY_ITEM = ITEMS.register("aggressivecolony_item", () ->
            new BlockItem(AGGRESSIVE_COLONY_BLOCK.get(), new Item.Properties().tab(ASCTab.instance))
    );

    public static final RegistryObject<Item> NANOINJECTOR_ITEM = ITEMS.register("nanoinjector_item", () ->
            new BlockItem(NANOINJECTOR.get(), new Item.Properties().tab(ASCTab.instance))
    );
}
