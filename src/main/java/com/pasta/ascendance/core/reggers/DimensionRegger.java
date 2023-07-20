package com.pasta.ascendance.core.reggers;

import com.pasta.ascendance.Ascendance;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public class DimensionRegger {

    public static final ResourceKey<Level> ASCDIM_KEY = ResourceKey.create(Registry.DIMENSION_REGISTRY,
            new ResourceLocation(Ascendance.MOD_ID, "compacted_dimension"));

    public static final ResourceKey<DimensionType> ASCDIM_TYPE = ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY,
            ASCDIM_KEY.location());

    public static void register(){
        Ascendance.LOGGER.info("Registering dimensions for " + Ascendance.MOD_ID);
    }

}
