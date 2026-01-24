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
