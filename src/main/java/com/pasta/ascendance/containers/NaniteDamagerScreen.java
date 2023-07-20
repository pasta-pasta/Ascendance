package com.pasta.ascendance.containers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.pasta.ascendance.Ascendance;
import com.pasta.ascendance.containers.renderer.EnergyInfoArea;
import com.pasta.ascendance.core.server.ASCServerSideHandler;
import com.pasta.ascendance.core.server.packets.NaniteDamagerC2SPacket;
import com.pasta.ascendance.misc.ASCMouseUtil;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.awt.*;
import java.util.Optional;

public class NaniteDamagerScreen extends AbstractContainerScreen<NaniteDamagerMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Ascendance.MOD_ID, "textures/gui/nanitedamager.png");
    private EditBox textField;
    private Button submitButton;

    public NaniteDamagerScreen(NaniteDamagerMenu menu, Inventory playerInv, Component title){
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
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, delta);
        renderTooltip(poseStack,mouseX,mouseY);
    }

    @Override
    protected void init() {
        super.init();
        int textFieldWidth = 80;
        int textFieldHeight = 14;
        int textFieldX = (this.width - textFieldWidth) / 2;
        int textFieldY = (this.height - textFieldHeight) / 2 - 60;

        // Creating text field
        this.textField = new EditBox(this.font, textFieldX, textFieldY, textFieldWidth, textFieldHeight, Component.literal(""));
        this.textField.setMaxLength(16);  // Limit the maximum length of input.
        this.textField.setValue("0");  // Set default value.

        int buttonWidth = 50;
        int buttonHeight = 16;
        int buttonX = (this.width - buttonWidth) / 2;
        int buttonY = (this.height - buttonHeight) / 2 - 22;

        // Creating button
        this.submitButton = new Button(buttonX, buttonY, buttonWidth, buttonHeight, Component.literal("Damage!"), button -> {
            // Get the int value from the text field
            try {
                int value = Integer.parseInt(this.textField.getValue());
                ASCServerSideHandler.sendToServer(new NaniteDamagerC2SPacket(value, menu.getBlockEntity().getBlockPos(), menu.getBlockEntity().getLevel().dimension()));
            } catch (NumberFormatException e) {

            }
        });

        this.addRenderableWidget(submitButton);
        this.addRenderableWidget(textField);
    }

}
