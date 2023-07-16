package com.pasta.ascendance.client.armor;

import com.pasta.ascendance.items.MeraliumArmorGecko;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public class MeraliumArmorRenderer extends GeoArmorRenderer<MeraliumArmorGecko> {

    public MeraliumArmorRenderer() {
        super(new MeraliumArmorModel());

        this.headBone = "armorHead";
        this.bodyBone = "armorBody";
        this.rightArmBone = "armorRightArm";
        this.leftArmBone = "armorLeftArm";
        this.rightLegBone = "armorLeftLeg";
        this.leftLegBone = "armorRightLeg";
        this.rightBootBone = "armorLeftBoot";
        this.leftBootBone = "armorRightBoot";
    }
}
