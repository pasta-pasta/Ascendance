package com.pasta.ascendance.core.reggers;

import com.pasta.ascendance.Ascendance;
import com.pasta.ascendance.recipe.NanoinjectorRecipy;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RecipesRegger {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Ascendance.MOD_ID);

    public static final RegistryObject<RecipeSerializer<NanoinjectorRecipy>> NANOINJECTOR_SERIALIZER =
            SERIALIZERS.register("nanoinjector_recipe", () -> NanoinjectorRecipy.Serializer.INSTANCE);
}
