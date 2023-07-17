package com.pasta.ascendance.integration;

import com.pasta.ascendance.Ascendance;
import com.pasta.ascendance.recipe.NanoinjectorRecipy;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;
import java.util.Objects;

@JeiPlugin
public class JEIASCPlugin implements IModPlugin {
    public static final mezz.jei.api.recipe.RecipeType<NanoinjectorRecipy> INJECTION_TYPE =
            new mezz.jei.api.recipe.RecipeType<>(NanoinjectorRecipeCategory.UID, NanoinjectorRecipy.class);

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Ascendance.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new
                NanoinjectorRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
        List<NanoinjectorRecipy> recipesInjecion = rm.getAllRecipesFor(NanoinjectorRecipy.Type.INSTANCE);
        registration.addRecipes(INJECTION_TYPE, recipesInjecion);
    }
}
