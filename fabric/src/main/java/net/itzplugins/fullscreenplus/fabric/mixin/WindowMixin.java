package net.itzplugins.fullscreenplus.fabric.mixin;

import static org.lwjgl.glfw.GLFW.*;

import net.minecraft.client.util.Monitor;
import net.minecraft.client.util.MonitorTracker;
import net.minecraft.client.util.VideoMode;
import net.minecraft.client.util.Window;
import net.itzplugins.fullscreenplus.FullscreenPlusConstants;
import net.itzplugins.fullscreenplus.FullscreenMode;
import net.itzplugins.fullscreenplus.fabric.FullscreenPlusFabricCaching;
import net.itzplugins.fullscreenplus.fabric.config.FullscreenPlusConfigFabric;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public abstract class WindowMixin {

    @Shadow
    private boolean fullscreen;

    @Shadow
    @Final
    private MonitorTracker monitorTracker;

    @Shadow
    public abstract long getHandle();

    @Unique
    private boolean wasFullscreen = false;

    @Unique
    private int windowPosX;
    @Unique
    private int windowPosY;
    @Unique
    private int windowWidth;
    @Unique
    private int windowHeight;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        long handle = getHandle();
        int[] x = new int[1];
        int[] y = new int[1];
        glfwGetWindowPos(handle, x, y);
        windowPosX = x[0];
        windowPosY = y[0];

        int[] w = new int[1];
        int[] h = new int[1];
        glfwGetWindowSize(handle, w, h);
        windowWidth = w[0];
        windowHeight = h[0];

        FullscreenPlusConstants.LOGGER.info("WindowMixin initialized: pos={},{} size={}x{}", windowPosX, windowPosY, windowWidth, windowHeight);
    }

    @Inject(method = "updateWindowRegion", at = @At("HEAD"), cancellable = true)
    private void onUpdateWindowRegion(CallbackInfo ci) {
        ci.cancel();

        FullscreenPlusConstants.LOGGER.info("================= [Fullscreen+ Start] =================");

        long handle = getHandle();

        if (!wasFullscreen && !fullscreen) {
            int[] x = new int[1];
            int[] y = new int[1];
            glfwGetWindowPos(handle, x, y);
            windowPosX = x[0];
            windowPosY = y[0];

            int[] w = new int[1];
            int[] h = new int[1];
            glfwGetWindowSize(handle, w, h);
            windowWidth = w[0];
            windowHeight = h[0];

            FullscreenPlusConstants.LOGGER.info("Cached window state: pos={},{} size={}x{}", windowPosX, windowPosY, windowWidth, windowHeight);
        }

        FullscreenPlusConfigFabric config = FullscreenPlusConfigFabric.getInstance();

        if (fullscreen) {
            if (config.fullscreen == FullscreenMode.NATIVE) {
                FullscreenPlusConstants.LOGGER.info("Switching to NATIVE fullscreen");

                long monitor = glfwGetPrimaryMonitor();
                Monitor monitorInstance = monitorTracker.getMonitor(monitor);
                if (monitorInstance != null) {
                    VideoMode videoMode = monitorInstance.getCurrentVideoMode();
                    glfwSetWindowMonitor(handle, monitor, 0, 0, videoMode.getWidth(), videoMode.getHeight(), videoMode.getRefreshRate());
                }
            } else {
                FullscreenPlusConstants.LOGGER.info("Switching to BORDERLESS fullscreen");

                long monitor = glfwGetPrimaryMonitor();
                Monitor monitorInstance = monitorTracker.getMonitor(monitor);

                if (monitorInstance != null) {
                    VideoMode videoMode = monitorInstance.getCurrentVideoMode();
                    int monitorX = monitorInstance.getViewportX();
                    int monitorY = monitorInstance.getViewportY();
                    int monitorWidth = videoMode.getWidth();
                    int monitorHeight = videoMode.getHeight();

                    FullscreenPlusConstants.LOGGER.info("Monitor: pos={},{} size={}x{}", monitorX, monitorY, monitorWidth, monitorHeight);

                    int finalX, finalY, finalWidth, finalHeight;

                    if (config.customized) {
                        finalX = config.x + (config.related ? monitorX : 0);
                        finalY = config.y + (config.related ? monitorY : 0);
                        finalWidth = config.width;
                        finalHeight = config.height;
                    } else {
                        finalX = monitorX;
                        finalY = monitorY;
                        finalWidth = monitorWidth;
                        finalHeight = monitorHeight;
                    }

                    glfwSetWindowAttrib(handle, GLFW_DECORATED, GLFW_FALSE);
                    glfwSetWindowPos(handle, finalX, finalY);
                    glfwSetWindowSize(handle, finalWidth, finalHeight);

                    FullscreenPlusConstants.LOGGER.info("Borderless applied: pos={},{} size={}x{}", finalX, finalY, finalWidth, finalHeight);
                }
            }
        } else {
            FullscreenPlusConstants.LOGGER.info("Switching to WINDOWED mode");

            if (glfwGetWindowMonitor(handle) != 0L) {
                glfwSetWindowMonitor(handle, 0L, windowPosX, windowPosY, windowWidth, windowHeight, GLFW_DONT_CARE);
            }

            glfwSetWindowAttrib(handle, GLFW_DECORATED, GLFW_TRUE);
            glfwSetWindowPos(handle, windowPosX, windowPosY);
            glfwSetWindowSize(handle, windowWidth, windowHeight);

            FullscreenPlusConstants.LOGGER.info("Windowed applied: pos={},{} size={}x{}", windowPosX, windowPosY, windowWidth, windowHeight);
        }

        wasFullscreen = fullscreen;

        FullscreenPlusConstants.LOGGER.info("================= [Fullscreen+ End] =================");
    }
}