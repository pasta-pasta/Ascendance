package com.pasta.ascendance.containers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.pasta.ascendance.Ascendance;
import com.pasta.ascendance.blocks.entities.NanoInjectorEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import org.w3c.dom.Text;

import java.awt.*;
import java.util.List;
import java.util.Objects;

public class NanoInjectorScreen extends AbstractContainerScreen<NanoInjectorMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Ascendance.MOD_ID, "textures/gui/nanoinjector.png");

    public NanoInjectorScreen(NanoInjectorMenu menu, Inventory playerInv, Component title){
        super(menu,playerInv, title);
    }


    @Override
    protected void renderBg(PoseStack stack, float mouseX, int mouseY, int partialTicks) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f,1.0f,1.0f,1.0f);
        RenderSystem.setShaderTexture(0,TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.blit(stack, x, y, 0, 0, imageWidth, imageHeight);

        blit(stack,x+55, y+76 - menu.getScaledFuel(), 215, 0, 16, menu.getScaledFuel());
        renderProgressArrow(stack, x, y);
        renderBurningProgres(stack, x, y);
    }

    private void renderProgressArrow(PoseStack poseStack, int x, int y){
        if (menu.isCrafting()){
            blit(poseStack,x+105, y+33, 176, 0, 8, menu.getScaledProgress());
        }
    }

    private void renderBurningProgres(PoseStack poseStack, int x, int y){
        if (menu.isBurning()){
            blit(poseStack,x+25, y+58 - menu.getScaledBurning(), 189, 0, 18, menu.getScaledBurning());
        }
    }

    @Override
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY) {

    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, delta);
        renderTooltip(poseStack,mouseX,mouseY);
    }

    @Override
    protected void init() {
        super.init();
    }
}
