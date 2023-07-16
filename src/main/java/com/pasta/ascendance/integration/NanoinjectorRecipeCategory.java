package com.pasta.ascendance.integration;

import com.mojang.blaze3d.vertex.PoseStack;
import com.pasta.ascendance.Ascendance;
import com.pasta.ascendance.core.reggers.BlockRegger;
import com.pasta.ascendance.recipe.NanoinjectorRecipy;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class NanoinjectorRecipeCategory implements IRecipeCategory<NanoinjectorRecipy> {

    public final static ResourceLocation UID = new ResourceLocation(Ascendance.MOD_ID, "nanoinjector_recipe");
    public static final ResourceLocation TEXTURE =
            new ResourceLocation(Ascendance.MOD_ID, "textures/gui/nanoinjector.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated progress;
    private final IDrawableAnimated fuel;

    public NanoinjectorRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockRegger.NANOINJECTOR.get()));
        IDrawableStatic progressDrawable = helper.drawableBuilder(TEXTURE,176, 0, 8, 26)
                .addPadding(34, 0, 105, 0)
                .build();
        IDrawableStatic fuelDrawable = helper.drawableBuilder(TEXTURE,215, 0, 16, 40)
                .addPadding(36, 0, 55, 0)
                .build();
        this.progress = helper.createAnimatedDrawable(progressDrawable,90,IDrawableAnimated.StartDirection.TOP, false);
        this.fuel = helper.createAnimatedDrawable(fuelDrawable,1000,IDrawableAnimated.StartDirection.TOP, true);
    }

    @Override
    public void draw(NanoinjectorRecipy recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        this.progress.draw(stack);
        this.fuel.draw(stack);
    }

    @Override
    public RecipeType<NanoinjectorRecipy> getRecipeType() {
        return JEIASCPlugin.INJECTION_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal("Nanite Injection");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, NanoinjectorRecipy recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 73, 11).addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.INPUT, 99, 11).addIngredients(recipe.getIngredients().get(1));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 86, 60).addItemStack(recipe.getResultItem());
    }
}
