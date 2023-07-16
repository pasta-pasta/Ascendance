package com.pasta.ascendance.client.armor;

import com.pasta.ascendance.Ascendance;
import com.pasta.ascendance.items.MeraliumArmorGecko;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class MeraliumArmorModel extends AnimatedGeoModel<MeraliumArmorGecko> {
    @Override
    public ResourceLocation getModelResource(MeraliumArmorGecko meraliumArmorGecko) {
        return new ResourceLocation(Ascendance.MOD_ID, "geo/meralium_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MeraliumArmorGecko meraliumArmorGecko) {
        return new ResourceLocation(Ascendance.MOD_ID, "textures/models/armor/meralium_armor_texture.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MeraliumArmorGecko meraliumArmorGecko) {
        return new ResourceLocation(Ascendance.MOD_ID, "animations/meralium_armor_animation.json");
    }
}
