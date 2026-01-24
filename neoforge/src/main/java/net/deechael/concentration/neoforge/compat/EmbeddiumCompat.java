package net.itzplugins.fullscreenplus.neoforge.compat;

import net.itzplugins.fullscreenplus.FullscreenPlus;
import net.itzplugins.fullscreenplus.FullscreenPlusConstants;
import net.itzplugins.fullscreenplus.FullscreenMode;
import net.itzplugins.fullscreenplus.neoforge.config.FullscreenPlusConfigNeoForge;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.embeddedt.embeddium.api.OptionGroupConstructionEvent;
import org.embeddedt.embeddium.api.options.control.CyclingControl;
import org.embeddedt.embeddium.api.options.control.TickBoxControl;
import org.embeddedt.embeddium.api.options.storage.MinecraftOptionsStorage;
import org.embeddedt.embeddium.api.options.structure.OptionImpl;
import org.embeddedt.embeddium.api.options.structure.StandardOptions;

/**
 * Make Embedddium fullscreen option follow Concentration function
 *
 * @author DeeChael
 */
public class EmbeddiumCompat {

    public static void init() {
        OptionGroupConstructionEvent.BUS.addListener(event -> {
            if (event.getId() != null && event.getId().toString().equals(StandardOptions.Group.WINDOW.toString())) {
                var options = event.getOptions();
                for (int i = 0; i < options.size(); i++) {
                    if (options.get(i).getId().toString().equals(StandardOptions.Option.FULLSCREEN.toString())) {
                        options.add(i, OptionImpl.createBuilder(FullscreenMode.class, MinecraftOptionsStorage.INSTANCE)
                                .setId(ResourceLocation.fromNamespaceAndPath(FullscreenPlusConstants.MOD_ID, "fullscreen_mode"))
                                .setName(Component.translatable("fullscreenplus.option.fullscreen_mode"))
                                .setTooltip(Component.translatable("fullscreenplus.option.fullscreen_mode.tooltip"))
                                .setControl((opt) -> new CyclingControl<>(opt, FullscreenMode.class, new Component[]{
                                        Component.translatable("fullscreenplus.option.fullscreen_mode.borderless"),
                                        Component.translatable("fullscreenplus.option.fullscreen_mode.native")
                                }))
                                .setBinding((vanillaOpts, value) -> {
                                            FullscreenPlusConfigNeoForge.ensureLoaded().setFullscreenMode(value);
                                            FullscreenPlusConfigNeoForge.ensureLoaded().save();
                                            if (vanillaOpts.fullscreen().get()) {
                                                // If fullscreen turns on, re-toggle to changing the fullscreen mode instantly
                                                FullscreenPlus.toggleFullScreenMode(vanillaOpts, true);
                                            }
                                        },
                                        (vanillaOpts) -> FullscreenPlusConfigNeoForge.ensureLoaded().getFullscreenMode()
                                )
                                .build());
                        options.set(
                                i + 1,
                                OptionImpl.createBuilder(Boolean.TYPE, MinecraftOptionsStorage.INSTANCE)
                                        .setId(StandardOptions.Option.FULLSCREEN)
                                        .setName(Component.translatable("options.fullscreen"))
                                        .setTooltip(Component.translatable("sodium.options.fullscreen.tooltip"))
                                        .setControl(TickBoxControl::new)
                                        .setBinding(FullscreenPlus::toggleFullScreenMode, (opts) -> opts.fullscreen().get()).build()
                        );
                        break;
                    }
                }
            }
        });
    }

}
