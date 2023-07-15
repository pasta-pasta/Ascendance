package com.pasta.ascendance.items;

import com.pasta.ascendance.core.ASCTab;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.EquipmentSlot;

public class MeraliumHelmet extends ArmorItem {
    public MeraliumHelmet(ArmorMaterial material, EquipmentSlot slot) {
        super(material, slot, new Item.Properties().tab(ASCTab.instance));
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        if (slot == EquipmentSlot.LEGS) {
            return "ascendance:textures/models/armor/meralium_layer_2.png";
        } else {
            // This applies for the helmet, chestplate and boots
            return "ascendance:textures/models/armor/meralium_layer_1.png";
        }
    }

}
