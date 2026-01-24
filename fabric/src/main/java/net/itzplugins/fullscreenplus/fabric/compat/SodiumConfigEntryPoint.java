package net.itzplugins.fullscreenplus.fabric.compat;
 
import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.itzplugins.fullscreenplus.FullscreenMode;
import net.itzplugins.fullscreenplus.FullscreenPlus;
import net.itzplugins.fullscreenplus.fabric.config.FullscreenPlusConfigFabric;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
 
public class SodiumConfigEntryPoint implements ConfigEntryPoint {
 
    private void onStorageChange() {
        FullscreenPlusConfigFabric.getInstance().save();
        if (MinecraftClient.getInstance().options.getFullscreen().getValue()) {
            FullscreenPlus.toggleFullScreenMode(MinecraftClient.getInstance().options, true);
        }
    }
 
    @Override
    public void registerConfigLate(ConfigBuilder builder) {
        builder.registerOwnModOptions()
                .addPage(builder.createOptionPage()
                        .setName(Text.translatable("fullscreenplus.options.title"))
                        .addOptionGroup(builder.createOptionGroup()
                                .setName(Text.translatable("fullscreenplus.options.group.display"))
                                .addOption(builder.createEnumOption(
                                                Identifier.of("fullscreenplus", "fullscreen_mode"),
                                                FullscreenMode.class)
                                        .setName(Text.translatable("fullscreenplus.option.fullscreen_mode"))
                                        .setTooltip(Text.translatable("fullscreenplus.option.fullscreen_mode.tooltip"))
                                        .setStorageHandler(this::onStorageChange)
                                        .setBinding(
                                                value -> FullscreenPlusConfigFabric.getInstance().fullscreen = value,
                                                () -> FullscreenPlusConfigFabric.getInstance().fullscreen)
                                        .setDefaultValue(FullscreenMode.BORDERLESS)
                                        .setElementNameProvider(mode -> mode.getCaption())
                                )
                        )
                );
    }
}
 