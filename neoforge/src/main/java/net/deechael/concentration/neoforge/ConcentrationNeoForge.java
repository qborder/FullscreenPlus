package net.itzplugins.fullscreenplus.neoforge;

import net.itzplugins.fullscreenplus.FullscreenPlus;
import net.itzplugins.fullscreenplus.FullscreenPlusConstants;
import net.itzplugins.fullscreenplus.config.FullscreenPlusConfigScreen;
import net.itzplugins.fullscreenplus.neoforge.compat.EmbeddiumCompat;
import net.itzplugins.fullscreenplus.neoforge.config.FullscreenPlusConfigNeoForge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.jetbrains.annotations.NotNull;

/**
 * Mod entrance for NeoForge of Concentration
 *
 * @author DeeChael
 */
@Mod(value = FullscreenPlusConstants.MOD_ID, dist = Dist.CLIENT)
public class FullscreenPlusNeoForge {

    public FullscreenPlusNeoForge(ModContainer container, IEventBus eventBus) {
        FullscreenPlus.init();

        container.registerConfig(ModConfig.Type.CLIENT, FullscreenPlusConfigNeoForge.SPECS, "fullscreenplus-client.toml");

        if (ModList.get().isLoaded("embeddium")) {
            EmbeddiumCompat.init();
        }

        if (FMLEnvironment.dist.isClient()) {
            ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> new IConfigScreenFactory() {
                @Override
                public @NotNull Screen createScreen(@NotNull ModContainer modContainer, @NotNull Screen parent) {
                    return new FullscreenPlusConfigScreen(Component.literal(FullscreenPlusConstants.MOD_NAME), parent) {

                        @Override
                        public void save() {
                            FullscreenPlusConfigNeoForge.SPECS.save();
                            FullscreenPlus.toggleFullScreenMode(minecraft.options, minecraft.options.fullscreen().get());
                        }

                        @Override
                        public void addElements() {
                            addOption(OptionInstance.createBoolean("fullscreenplus.config.customization.enabled",
                                    FullscreenPlusConfigNeoForge.CUSTOMIZED.get(),
                                    FullscreenPlusConfigNeoForge.CUSTOMIZED::set));
                            addOption(OptionInstance.createBoolean("fullscreenplus.config.customization.related",
                                    FullscreenPlusConfigNeoForge.RELATED.get(),
                                    FullscreenPlusConfigNeoForge.RELATED::set));

                            addIntField(Component.translatable("fullscreenplus.config.customization.x"),
                                    FullscreenPlusConfigNeoForge.X,
                                    FullscreenPlusConfigNeoForge.X::set);
                            addIntField(Component.translatable("fullscreenplus.config.customization.y"),
                                    FullscreenPlusConfigNeoForge.Y,
                                    FullscreenPlusConfigNeoForge.Y::set);
                            addIntField(Component.translatable("fullscreenplus.config.customization.width"),
                                    FullscreenPlusConfigNeoForge.WIDTH,
                                    FullscreenPlusConfigNeoForge.WIDTH::set);
                            addIntField(Component.translatable("fullscreenplus.config.customization.height"),
                                    FullscreenPlusConfigNeoForge.HEIGHT,
                                    FullscreenPlusConfigNeoForge.HEIGHT::set);
                        }
                    };
                }
            });
        }
    }

}