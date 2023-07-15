package com.pasta.ascendance.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.pasta.ascendance.Ascendance;
import com.pasta.ascendance.client.ClientInfectionData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class InfectionHudOverlay {
    private static final ResourceLocation INFECTION_FULL =
            new ResourceLocation(Ascendance.MOD_ID, "textures/hud/infection/infection_full.png");

    private static final ResourceLocation INFECTION_EMPTY =
            new ResourceLocation(Ascendance.MOD_ID, "textures/hud/infection/infection_empty.png");

    public static final IGuiOverlay HUD_INFECTION =  ((gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        int x = screenWidth / 2;
        int y = screenHeight;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, INFECTION_EMPTY);
        for (int i = 0; i < 10; i++) {
            GuiComponent.blit(poseStack, x - 2 + (i * 9), y - 48, 0, 0, 12, 12, 12, 12);
        }

        RenderSystem.setShaderTexture(0, INFECTION_FULL);
        for (int i = 0; i < 10; i++) {
            if (ClientInfectionData.getPlayerInfection() > i * 10) {
                GuiComponent.blit(poseStack, x - 2 + (i * 9), y - 48, 0, 0, 12, 12, 12, 12);
            } else {
                break;
            }

        }
        float scale = 0.7F; // Change this to the scale you want

        poseStack.pushPose(); // Create a new context to not affect other renderings
        poseStack.scale(scale, scale, scale); // Apply the scale

        int scaledX = (int) (x / scale);
        int scaledY = (int) (y / scale);

        Minecraft mc = Minecraft.getInstance();
        mc.font.draw(poseStack, String.valueOf(ClientInfectionData.getPlayerInfection()), x+222, y+37, 0x700000);

        poseStack.popPose();

    });

}
