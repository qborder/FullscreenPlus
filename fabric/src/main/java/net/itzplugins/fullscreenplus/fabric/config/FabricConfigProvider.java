package net.itzplugins.fullscreenplus.fabric.config;

import net.itzplugins.fullscreenplus.config.Config;
import net.itzplugins.fullscreenplus.config.ConfigProvider;

public class FabricConfigProvider implements ConfigProvider {

    @Override
    public Config ensureLoaded() {
        return FullscreenPlusConfigFabric.getInstance();
    }

}
