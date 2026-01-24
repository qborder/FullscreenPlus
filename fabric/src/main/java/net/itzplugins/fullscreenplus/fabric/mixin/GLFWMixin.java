package net.itzplugins.fullscreenplus.fabric.mixin;

import net.minecraft.client.util.Monitor;
import net.minecraft.client.util.VideoMode;
import net.minecraft.client.util.Window;
import net.itzplugins.fullscreenplus.FullscreenPlusConstants;
import net.itzplugins.fullscreenplus.FullscreenMode;
import net.itzplugins.fullscreenplus.fabric.FullscreenPlusFabricCaching;
import net.itzplugins.fullscreenplus.fabric.config.FullscreenPlusConfigFabric;
import net.itzplugins.fullscreenplus.mixin.accessor.WindowAccessor;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lwjgl.system.Checks.CHECKS;
import static org.lwjgl.system.Checks.check;
import static org.lwjgl.system.JNI.invokePPV;

@Mixin(value = GLFW.class, remap = false)
public class GLFWMixin {

    @Inject(method = "glfwSetWindowMonitor", at = @At("HEAD"), cancellable = true)
    private static void inject$glfwSetWindowMonitor(long window, long monitor, int xpos, int ypos, int width, int height, int refreshRate, CallbackInfo ci) {
        FullscreenPlusConstants.LOGGER.info("================= [Fullscreen+ Start] =================");
        FullscreenPlusConstants.LOGGER.info("Trying to modify window monitor");

        // Monitor is 0 means the game is windowed mode, so the expression means if is toggling to fullscreen
        if (monitor != 0L) {
            FullscreenPlusConstants.LOGGER.info("Modifying window size limits");
            GLFW.glfwSetWindowSizeLimits(window, 0, 0, width, height);
        }

        // Because whether in fullscreen mode or windowed mode
        // The final step is same
        // So I extracted the value then execute the final step
        int finalWidth;
        int finalHeight;

        int finalX;
        int finalY;

        Window windowInstance = MinecraftClient.getInstance().getWindow();
        WindowAccessor accessor = (WindowAccessor) (Object) windowInstance;

        if (windowInstance != null && windowInstance.isFullscreen()) {
            FullscreenPlusConfigFabric config = FullscreenPlusConfigFabric.getInstance();

            // If the game started with fullscreen mode, when switching to windowed mode, it will be forced to move to the primary monitor
            // Though size and position isn't be set at initialization, but I think the window should be at the initial monitor
            // So save the monitor and use the monitor value when the size isn't cached
            FullscreenPlusFabricCaching.lastMonitor = monitor;

            // Lock caching, because when switching back, the window will be once resized to the maximum value and the cache value will be wrong
            // Position won't be affected, so it doesn't need lock
            FullscreenPlusFabricCaching.cacheSizeLock = true;
            FullscreenPlusConstants.LOGGER.info("Locked size caching");

            if (config.fullscreen == FullscreenMode.NATIVE) {
                FullscreenPlusConstants.LOGGER.info("Fullscreen mode is native, apply now!");
                if (monitor == 0L)
                    monitor = windowInstance.getMonitor().getHandle();
                finalExecute(window, monitor, xpos, ypos, width, height, -1);
                FullscreenPlusConstants.LOGGER.info("================= [Fullscreen+ End] =================");
                return;
            }

            FullscreenPlusConstants.LOGGER.info("Trying to switch to borderless fullscreen mode");

            // Get the monitor the user want to use and get the relative position in the system
            // The monitor is always non-null because when switching fullscreen mode, there must be a monitor to put the window
            Monitor monitorInstance = accessor.getMonitorTracker().getMonitor(monitor);
            FullscreenPlusConstants.LOGGER.info("Current fullscreen monitor is {}", monitor);

            // Remove the title bar to prevent that user can see the title bar if they put their monitors vertically connected
            GLFW.glfwSetWindowAttrib(window, GLFW.GLFW_DECORATED, GLFW.GLFW_FALSE);
            FullscreenPlusConstants.LOGGER.info("Trying to remove the title bar");

            if (FullscreenPlusConfigFabric.getInstance().customized) {

                FullscreenPlusConstants.LOGGER.info("Customization enabled, so replace the fullscreen size with customized size");
                finalX = config.x + (config.related ? monitorInstance.getViewportX() : 0);
                finalY = config.y - (config.height == height ? 1 : 0) + (config.related ? monitorInstance.getViewportY() : 0);
                finalWidth = config.width;
                finalHeight = config.height + (config.height == height ? 1 : 0);
            } else {
                // If we make the window not decorated and set the window size exactly the same with the screen size, it will become native fullscreen mode
                // to prevent this, I enlarge the height by 1 pixel and move up the window by 1 pixel which won't affect anything (unless you have a screen
                // which is added above the monitor which holds the game) and will have a good experience
                // Actually this is a little bit dirty, needs to find a better way to solve it
                finalX = monitorInstance.getViewportX();
                finalY = monitorInstance.getViewportY() - 1;
                finalWidth = width;
                finalHeight = height + 1;
            }

            accessor.setX(finalX);
            accessor.setY(finalY);
            accessor.setWidth(finalWidth);
            accessor.setHeight(finalHeight);
        } else {
            FullscreenPlusConstants.LOGGER.info("Trying to switch to windowed mode");

            // Re-add the title bar so user can move the window and minimize, maximize and close the window
            FullscreenPlusConstants.LOGGER.info("Trying to add title bar back");
            GLFW.glfwSetWindowAttrib(window, GLFW.GLFW_DECORATED, GLFW.GLFW_TRUE);

            FullscreenPlusConstants.LOGGER.info("Trying to use cached value to resize the window");

            // Make sure that Concentration has cached position and size, because position size won't be cached when the game starting in fullscreen mode
            finalWidth = FullscreenPlusFabricCaching.cachedSize ? FullscreenPlusFabricCaching.cachedWidth : width;
            finalHeight = FullscreenPlusFabricCaching.cachedSize ? FullscreenPlusFabricCaching.cachedHeight : height;

            // To make sure that even starting with fullscreen mode can also make the window stay at the current monitor
            // So I set two ways to ensure the position
            if (FullscreenPlusFabricCaching.cachedPos) {
                // If Fullscreen+ cached the pos, use the cached value
                finalX = FullscreenPlusFabricCaching.cachedX;
                finalY = FullscreenPlusFabricCaching.cachedY;
            } else if (FullscreenPlusFabricCaching.lastMonitor != -1) {
                // or else maybe the game started with fullscreen mode, so I don't need to care about the size
                // only need to make sure that the position is in the correct monitor
                Monitor monitorInstance = accessor.getMonitorTracker().getMonitor(FullscreenPlusFabricCaching.lastMonitor);
                VideoMode videoMode = monitorInstance.getCurrentVideoMode();
                finalX = (videoMode.getWidth() - finalWidth) / 2;
                finalY = (videoMode.getHeight() - finalHeight) / 2;
            } else {
                // if both value are missed, use the default value to prevent errors
                finalX = xpos;
                finalY = ypos;
            }

            // Unlock caching, because user can change the window size now
            FullscreenPlusFabricCaching.cacheSizeLock = false;
            FullscreenPlusConstants.LOGGER.info("Unlocked size caching");
        }

        FullscreenPlusConstants.LOGGER.info("Window size: {}, {}, position: {}, {}", finalWidth, finalHeight, finalX, finalY);

        FullscreenPlusConstants.LOGGER.info("Trying to resize and reposition the window");
        finalExecute(window, 0L, finalX, finalY, finalWidth, finalHeight, -1);

        FullscreenPlusConstants.LOGGER.info("================= [Fullscreen+ End] =================");
        ci.cancel();
    }

    private static void finalExecute(long window, long monitor, int xpos, int ypos, int width, int height, int refreshRate) {
        long __functionAddress = GLFW.Functions.SetWindowMonitor;
        if (CHECKS) {
            check(window);
        }
        invokePPV(window, monitor, xpos, ypos, width, height, refreshRate, __functionAddress);
    }

}
