package net.itzplugins.fullscreenplus.neoforge.config;

import net.itzplugins.fullscreenplus.config.Config;
import net.itzplugins.fullscreenplus.config.ConfigProvider;

public class NeoForgeConfigProvider implements ConfigProvider {

    @Override
    public Config ensureLoaded() {
        return FullscreenPlusConfigNeoForge.ensureLoaded();
    }

}
