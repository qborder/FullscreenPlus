package net.itzplugins.fullscreenplus.config;

import net.itzplugins.fullscreenplus.FullscreenMode;

public interface Config {

    FullscreenMode getFullscreenMode();

    void setFullscreenMode(FullscreenMode fullscreenMode);

    void save();

}
