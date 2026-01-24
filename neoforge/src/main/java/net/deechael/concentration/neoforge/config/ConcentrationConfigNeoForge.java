package net.itzplugins.fullscreenplus.neoforge.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.itzplugins.fullscreenplus.FullscreenPlusConstants;
import net.itzplugins.fullscreenplus.FullscreenMode;
import net.itzplugins.fullscreenplus.config.Config;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.nio.file.Path;

public final class FullscreenPlusConfigNeoForge implements Config {

    public final static FullscreenPlusConfigNeoForge INSTANCE = new FullscreenPlusConfigNeoForge();

    public static final ModConfigSpec SPECS;
    public static final ModConfigSpec.BooleanValue CUSTOMIZED;
    public static final ModConfigSpec.BooleanValue RELATED;
    public static final ModConfigSpec.IntValue X;
    public static final ModConfigSpec.IntValue Y;
    public static final ModConfigSpec.IntValue WIDTH;
    public static final ModConfigSpec.IntValue HEIGHT;
    public static final ModConfigSpec.EnumValue<FullscreenMode> FULLSCREEN;

    private static boolean loaded = false;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("fullscreenplus");

        CUSTOMIZED = builder.comment("Whether the window size and pos is customized")
                .define("customized", false);
        RELATED = builder.comment("Whether the window pos should related to the monitor")
                .define("related", false);

        X = builder.comment("X coordinate")
                .defineInRange("x", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        Y = builder.comment("Y coordinate")
                .defineInRange("y", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        WIDTH = builder.comment("Width")
                .defineInRange("width", 800, 1, Integer.MAX_VALUE);
        HEIGHT = builder.comment("Height")
                .defineInRange("height", 600, 1, Integer.MAX_VALUE);
        FULLSCREEN = builder.comment("Fullscreen mode")
                .defineEnum("fullscreen", FullscreenMode.BORDERLESS);

        builder.pop();

        SPECS = builder.build();
    }

    public static FullscreenPlusConfigNeoForge ensureLoaded() {/*
        if (!loaded) {
            FullscreenPlusConstants.LOGGER.info("Loading Fullscreen+ Config");

            Path path = FMLPaths.CONFIGDIR.get().resolve("fullscreenplus-client.toml");
            CommentedFileConfig config = CommentedFileConfig.builder(path)
                    .sync()
                    .autosave()
                    .writingMode(WritingMode.REPLACE)
                    .build();
            config.load();

            loaded = true;
        }*/
        return INSTANCE;
    }

    private FullscreenPlusConfigNeoForge() {
    }

    @Override
    public FullscreenMode getFullscreenMode() {
        return FULLSCREEN.get();
    }

    @Override
    public void setFullscreenMode(FullscreenMode fullscreenMode) {
        FULLSCREEN.set(fullscreenMode);
    }

    @Override
    public void save() {
        SPECS.save();
    }

}
