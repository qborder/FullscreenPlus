package net.itzplugins.fullscreenplus.mixin.accessor;

import net.minecraft.client.util.MonitorTracker;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Window.class)
public interface WindowAccessor {

    @Accessor
    MonitorTracker getMonitorTracker();

    @Accessor("fullscreenVideoModeDirty")
    void setFullscreenVideoModeDirty(boolean value);

    @Accessor("x")
    void setX(int x);

    @Accessor("y")
    void setY(int y);

    @Accessor("width")
    void setWidth(int width);

    @Accessor("height")
    void setHeight(int height);

}
