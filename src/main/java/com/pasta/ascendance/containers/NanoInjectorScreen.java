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

    private ExtendedButton beanButton;
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
    }

    @Override
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY) {
        drawString(stack,font,title, 6,5, 0xc0c0c0);
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
        this.beanButton = addRenderableWidget(new ExtendedButton(this.leftPos+47, this.topPos+60, 72, 18, Component.literal("Inject"), btn -> {
            Objects.requireNonNull(Minecraft.getInstance().player).displayClientMessage(Component.literal("beans. what did you expect?"), false);
        }));
    }
}
