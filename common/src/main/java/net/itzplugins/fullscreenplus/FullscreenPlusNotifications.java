package net.itzplugins.fullscreenplus;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

public class FullscreenPlusNotifications {

    public static void showModeChanged(FullscreenMode mode) {
        if (!shouldShowNotifications()) return;
        showToast(
            Text.translatable("fullscreenplus.name"),
            Text.translatable("fullscreenplus.notification.mode_changed", mode.getCaption())
        );
    }

    public static void showWindowedMode() {
        if (!shouldShowNotifications()) return;
        showToast(
            Text.translatable("fullscreenplus.name"),
            Text.translatable("fullscreenplus.notification.windowed")
        );
    }

    public static void showSettingsSaved() {
        if (!shouldShowNotifications()) return;
        showToast(
            Text.translatable("fullscreenplus.name"),
            Text.translatable("fullscreenplus.notification.settings_saved")
        );
    }

    private static void showToast(Text title, Text description) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.getToastManager() != null) {
            client.getToastManager().add(
                SystemToast.create(client, SystemToast.Type.PERIODIC_NOTIFICATION, title, description)
            );
        }
    }

    private static boolean shouldShowNotifications() {
        return true;
    }
}
