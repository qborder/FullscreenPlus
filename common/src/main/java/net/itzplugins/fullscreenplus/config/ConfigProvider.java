package net.itzplugins.fullscreenplus.config;

import java.util.ServiceLoader;

public interface ConfigProvider {

    ConfigProvider INSTANCE = get();

    Config ensureLoaded();

    static ConfigProvider get() {
        return ServiceLoader.load(ConfigProvider.class)
                .findFirst()
                .orElseThrow();
    }

}
