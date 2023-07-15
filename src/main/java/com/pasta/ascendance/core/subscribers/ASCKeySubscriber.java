package com.pasta.ascendance.core.subscribers;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class ASCKeySubscriber {
    private static final long WINDOW = Minecraft.getInstance().getWindow().getWindow();



    public static boolean isHoldingShift() {
        return InputConstants.isKeyDown(WINDOW, GLFW.GLFW_KEY_LEFT_SHIFT) || InputConstants.isKeyDown(WINDOW, GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    public static boolean isHoldingControl() {
        return InputConstants.isKeyDown(WINDOW, GLFW.GLFW_KEY_LEFT_CONTROL) || InputConstants.isKeyDown(WINDOW, GLFW.GLFW_KEY_RIGHT_CONTROL);
    }

    public static boolean isHoldingSpace() {
        return InputConstants.isKeyDown(WINDOW, GLFW.GLFW_KEY_SPACE);
    }
    public static boolean isPlayerMoving() {
        return InputConstants.isKeyDown(WINDOW, GLFW.GLFW_KEY_W) ||
                InputConstants.isKeyDown(WINDOW, GLFW.GLFW_KEY_A) ||
                InputConstants.isKeyDown(WINDOW, GLFW.GLFW_KEY_S) ||
                InputConstants.isKeyDown(WINDOW, GLFW.GLFW_KEY_D) ||
                isHoldingShift() ||
                isHoldingControl() ||
                isHoldingSpace();
    }
}
