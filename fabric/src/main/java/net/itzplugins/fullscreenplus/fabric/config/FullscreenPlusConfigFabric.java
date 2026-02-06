package net.itzplugins.fullscreenplus.fabric.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.itzplugins.fullscreenplus.FullscreenMode;
import net.itzplugins.fullscreenplus.config.Config;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class FullscreenPlusConfigFabric implements Config {

    private static final Path configFile = FabricLoader.getInstance().getConfigDir().resolve("fullscreenplus.json");

    private FullscreenPlusConfigFabric() {
    }

    private static FullscreenPlusConfigFabric INSTANCE = null;

    public static FullscreenPlusConfigFabric getInstance() {
        if (INSTANCE == null) {
            Gson gson = new Gson();
            try (FileReader reader = new FileReader(configFile.toFile())) {
                INSTANCE = gson.fromJson(reader, FullscreenPlusConfigFabric.class);
            } catch (IOException ignored) {
            }
            if (INSTANCE == null) {
                INSTANCE = new FullscreenPlusConfigFabric();
                INSTANCE.save();
            }
        }
        return INSTANCE;
    }

    public boolean customized = false;
    public boolean related = false;
    public int x = 0;
    public int y = 0;
    public int width = 800;
    public int height = 600;
    public FullscreenMode fullscreen = FullscreenMode.BORDERLESS;

    public boolean dynamicResolution = false;
    public int renderScale = 100;

    public boolean ultrawideSupport = false;

    public boolean hdrPassthrough = false;

    public boolean refreshRateOverride = false;
    public int refreshRate = 60;

    public boolean windowedBorderless = true;
    public int windowedBorderlessWidth = 1280;
    public int windowedBorderlessHeight = 720;

    public String windowPositionPreset = "default";
    public String windowTitle = "";

    public boolean lowLatencyMode = false;

    public boolean showNotifications = false;

    public int preferredMonitor = 0;

    public boolean unfocusedFpsLimit = true;
    public int unfocusedFpsMax = 15;

    public boolean cycleModesKeybind = false;

    public boolean cursorConfinement = false;

    public int lastWindowX = 0;
    public int lastWindowY = 0;
    public int lastWindowWidth = 854;
    public int lastWindowHeight = 480;
    public boolean hasLastWindowState = false;

    @Override
    public FullscreenMode getFullscreenMode() {
        return this.fullscreen;
    }

    @Override
    public void setFullscreenMode(FullscreenMode fullscreenMode) {
        this.fullscreen = fullscreenMode;
    }

    @Override
    public void save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(configFile.toFile())) {
            gson.toJson(this, FullscreenPlusConfigFabric.class, writer);
        } catch (IOException ignored) {
        }
    }

}
