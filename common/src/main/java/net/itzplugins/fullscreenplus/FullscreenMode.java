package net.itzplugins.fullscreenplus;

import com.mojang.serialization.Codec;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

public enum FullscreenMode implements StringIdentifiable {
    NATIVE(0, "fullscreenplus.mode.native"),
    BORDERLESS(1, "fullscreenplus.mode.borderless");

    public static final Codec<FullscreenMode> CODEC = StringIdentifiable.createCodec(FullscreenMode::values);
    private static final IntFunction<FullscreenMode> BY_ID = ValueLists.createIndexToValueFunction(FullscreenMode::getId, values(), ValueLists.OutOfBoundsHandling.WRAP);

    private final int id;
    private final String key;

    FullscreenMode(int id, String key) {
        this.id = id;
        this.key = key;
    }

    public int getId() {
        return id;
    }

    public @NotNull String getKey() {
        return key;
    }

    public Text getCaption() {
        return Text.translatable(key);
    }

    @Override
    public @NotNull String asString() {
        return name().toLowerCase();
    }


    public static FullscreenMode byId(int id) {
        return BY_ID.apply(id);
    }
}
