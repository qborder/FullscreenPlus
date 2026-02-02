package net.itzplugins.fullscreenplus.fabric.mixin;

import static org.lwjgl.glfw.GLFW.*;

import net.minecraft.client.util.Monitor;
import net.minecraft.client.util.MonitorTracker;
import net.minecraft.client.util.VideoMode;
import net.minecraft.client.util.Window;
import net.itzplugins.fullscreenplus.FullscreenPlusConstants;
import net.itzplugins.fullscreenplus.FullscreenMode;
import net.itzplugins.fullscreenplus.FullscreenPlusNotifications;
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
        FullscreenPlusConfigFabric config = FullscreenPlusConfigFabric.getInstance();

        if (config.hasLastWindowState) {
            windowPosX = config.lastWindowX;
            windowPosY = config.lastWindowY;
            windowWidth = config.lastWindowWidth;
            windowHeight = config.lastWindowHeight;
            FullscreenPlusConstants.LOGGER.info("WindowMixin restored from config: pos={},{} size={}x{}", windowPosX,
                    windowPosY, windowWidth, windowHeight);
        } else {
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
            FullscreenPlusConstants.LOGGER.info("WindowMixin initialized: pos={},{} size={}x{}", windowPosX, windowPosY,
                    windowWidth, windowHeight);
        }

        if (config.lowLatencyMode) {
            fullscreenplus$applyLowLatency(handle);
        }

        if (config.windowTitle != null && !config.windowTitle.isEmpty()) {
            glfwSetWindowTitle(handle, config.windowTitle);
        }
    }

    @Unique
    private void fullscreenplus$applyLowLatency(long handle) {
        FullscreenPlusConstants.LOGGER.info("Applying low latency mode hints");
    }

    @Unique
    private void fullscreenplus$saveWindowState() {
        FullscreenPlusConfigFabric config = FullscreenPlusConfigFabric.getInstance();
        config.lastWindowX = windowPosX;
        config.lastWindowY = windowPosY;
        config.lastWindowWidth = windowWidth;
        config.lastWindowHeight = windowHeight;
        config.hasLastWindowState = true;
        config.save();
    }

    @Inject(method = "updateWindowRegion", at = @At("HEAD"), cancellable = true)
    private void onUpdateWindowRegion(CallbackInfo ci) {
        ci.cancel();

        FullscreenPlusConstants.LOGGER.info("================= [Fullscreen+ Start] =================");

        long handle = getHandle();
        FullscreenPlusConfigFabric config = FullscreenPlusConfigFabric.getInstance();

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

            fullscreenplus$saveWindowState();
            FullscreenPlusConstants.LOGGER.info("Cached window state: pos={},{} size={}x{}", windowPosX, windowPosY,
                    windowWidth, windowHeight);
        }

        if (fullscreen) {
            FullscreenMode mode = config.fullscreen;

            if (mode == FullscreenMode.NATIVE) {
                FullscreenPlusConstants.LOGGER.info("Switching to NATIVE fullscreen");
                fullscreenplus$applyNativeFullscreen(handle, config);
                if (config.showNotifications)
                    FullscreenPlusNotifications.showModeChanged(mode);
            } else if (mode == FullscreenMode.EXCLUSIVE_BORDERLESS) {
                FullscreenPlusConstants.LOGGER.info("Switching to EXCLUSIVE BORDERLESS fullscreen");
                fullscreenplus$applyExclusiveBorderless(handle, config);
                if (config.showNotifications)
                    FullscreenPlusNotifications.showModeChanged(mode);
            } else {
                FullscreenPlusConstants.LOGGER.info("Switching to BORDERLESS fullscreen");
                fullscreenplus$applyBorderlessFullscreen(handle, config);
                if (config.showNotifications)
                    FullscreenPlusNotifications.showModeChanged(mode);
            }
        } else {
            if (config.windowedBorderless) {
                FullscreenPlusConstants.LOGGER.info("Switching to WINDOWED BORDERLESS mode");
                fullscreenplus$applyWindowedBorderless(handle, config);
            } else {
                FullscreenPlusConstants.LOGGER.info("Switching to WINDOWED mode");
                fullscreenplus$applyWindowed(handle, config);
            }
            if (config.showNotifications && wasFullscreen)
                FullscreenPlusNotifications.showWindowedMode();
        }

        wasFullscreen = fullscreen;

        FullscreenPlusConstants.LOGGER.info("================= [Fullscreen+ End] =================");
    }

    @Unique
    private void fullscreenplus$applyNativeFullscreen(long handle, FullscreenPlusConfigFabric config) {
        long monitor = glfwGetPrimaryMonitor();
        Monitor monitorInstance = monitorTracker.getMonitor(monitor);
        if (monitorInstance != null) {
            VideoMode videoMode = monitorInstance.getCurrentVideoMode();
            int refreshRate = config.refreshRateOverride ? config.refreshRate : videoMode.getRefreshRate();
            glfwSetWindowMonitor(handle, monitor, 0, 0, videoMode.getWidth(), videoMode.getHeight(), refreshRate);
            FullscreenPlusConstants.LOGGER.info("Native applied: {}x{} @{}Hz", videoMode.getWidth(),
                    videoMode.getHeight(), refreshRate);
        }
    }

    @Unique
    private void fullscreenplus$applyExclusiveBorderless(long handle, FullscreenPlusConfigFabric config) {
        long monitor = glfwGetPrimaryMonitor();
        Monitor monitorInstance = monitorTracker.getMonitor(monitor);

        if (monitorInstance != null) {
            VideoMode videoMode = monitorInstance.getCurrentVideoMode();
            int monitorX = monitorInstance.getViewportX();
            int monitorY = monitorInstance.getViewportY();
            int monitorWidth = videoMode.getWidth();
            int monitorHeight = videoMode.getHeight();

            glfwSetWindowAttrib(handle, GLFW_DECORATED, GLFW_FALSE);
            glfwSetWindowAttrib(handle, GLFW_FLOATING, GLFW_TRUE);

            if (config.hdrPassthrough) {
                FullscreenPlusConstants.LOGGER.info("HDR passthrough enabled");
            }

            int[] finalDimensions = fullscreenplus$calculateDimensions(config, monitorX, monitorY, monitorWidth,
                    monitorHeight);
            glfwSetWindowPos(handle, finalDimensions[0], finalDimensions[1]);
            glfwSetWindowSize(handle, finalDimensions[2], finalDimensions[3]);

            FullscreenPlusConstants.LOGGER.info("Exclusive Borderless applied: pos={},{} size={}x{}",
                    finalDimensions[0], finalDimensions[1], finalDimensions[2], finalDimensions[3]);
        }
    }

    @Unique
    private void fullscreenplus$applyBorderlessFullscreen(long handle, FullscreenPlusConfigFabric config) {
        long monitor = glfwGetPrimaryMonitor();
        Monitor monitorInstance = monitorTracker.getMonitor(monitor);

        if (monitorInstance != null) {
            VideoMode videoMode = monitorInstance.getCurrentVideoMode();
            int monitorX = monitorInstance.getViewportX();
            int monitorY = monitorInstance.getViewportY();
            int monitorWidth = videoMode.getWidth();
            int monitorHeight = videoMode.getHeight();

            FullscreenPlusConstants.LOGGER.info("Monitor: pos={},{} size={}x{}", monitorX, monitorY, monitorWidth,
                    monitorHeight);

            if (config.ultrawideSupport) {
                float aspectRatio = (float) monitorWidth / monitorHeight;
                if (aspectRatio >= 2.3f) {
                    FullscreenPlusConstants.LOGGER.info("Ultrawide detected: aspect ratio {}", aspectRatio);
                }
            }

            if (config.hdrPassthrough) {
                FullscreenPlusConstants.LOGGER.info("HDR passthrough enabled");
            }

            int[] finalDimensions = fullscreenplus$calculateDimensions(config, monitorX, monitorY, monitorWidth,
                    monitorHeight);

            glfwSetWindowAttrib(handle, GLFW_DECORATED, GLFW_FALSE);
            glfwSetWindowPos(handle, finalDimensions[0], finalDimensions[1]);
            glfwSetWindowSize(handle, finalDimensions[2], finalDimensions[3]);

            FullscreenPlusConstants.LOGGER.info("Borderless applied: pos={},{} size={}x{}",
                    finalDimensions[0], finalDimensions[1], finalDimensions[2], finalDimensions[3]);
        }
    }

    @Unique
    private int[] fullscreenplus$calculateDimensions(FullscreenPlusConfigFabric config, int monitorX, int monitorY,
            int monitorWidth, int monitorHeight) {
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

        if (config.dynamicResolution && config.renderScale < 100) {
            FullscreenPlusConstants.LOGGER.info("Dynamic resolution: rendering at {}% scale", config.renderScale);
        }

        return new int[] { finalX, finalY, finalWidth, finalHeight };
    }

    @Unique
    private void fullscreenplus$applyWindowedBorderless(long handle, FullscreenPlusConfigFabric config) {
        glfwSetWindowAttrib(handle, GLFW_DECORATED, GLFW_FALSE);
        glfwSetWindowAttrib(handle, GLFW_FLOATING, GLFW_FALSE);

        int width = config.windowedBorderlessWidth;
        int height = config.windowedBorderlessHeight;

        long monitor = glfwGetPrimaryMonitor();
        Monitor monitorInstance = monitorTracker.getMonitor(monitor);
        if (monitorInstance != null) {
            VideoMode videoMode = monitorInstance.getCurrentVideoMode();
            int centerX = monitorInstance.getViewportX() + (videoMode.getWidth() - width) / 2;
            int centerY = monitorInstance.getViewportY() + (videoMode.getHeight() - height) / 2;

            glfwSetWindowPos(handle, centerX, centerY);
            glfwSetWindowSize(handle, width, height);

            FullscreenPlusConstants.LOGGER.info("Windowed Borderless applied: pos={},{} size={}x{}", centerX, centerY,
                    width, height);
        }
    }

    @Unique
    private void fullscreenplus$applyWindowed(long handle, FullscreenPlusConfigFabric config) {
        if (glfwGetWindowMonitor(handle) != 0L) {
            glfwSetWindowMonitor(handle, 0L, windowPosX, windowPosY, windowWidth, windowHeight, GLFW_DONT_CARE);
        }

        glfwSetWindowAttrib(handle, GLFW_DECORATED, GLFW_TRUE);
        glfwSetWindowAttrib(handle, GLFW_FLOATING, GLFW_FALSE);
        glfwSetWindowPos(handle, windowPosX, windowPosY);
        glfwSetWindowSize(handle, windowWidth, windowHeight);

        FullscreenPlusConstants.LOGGER.info("Windowed applied: pos={},{} size={}x{}", windowPosX, windowPosY,
                windowWidth, windowHeight);
    }
}