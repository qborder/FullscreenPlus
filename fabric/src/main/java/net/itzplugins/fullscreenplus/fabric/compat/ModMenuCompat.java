package net.itzplugins.fullscreenplus.fabric.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.itzplugins.fullscreenplus.FullscreenMode;
import net.itzplugins.fullscreenplus.FullscreenPlus;
import net.itzplugins.fullscreenplus.FullscreenPlusNotifications;
import net.itzplugins.fullscreenplus.fabric.config.FullscreenPlusConfigFabric;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ModMenuCompat implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            FullscreenPlusConfigFabric config = FullscreenPlusConfigFabric.getInstance();

            return YetAnotherConfigLib.createBuilder()
                    .title(Text.translatable("fullscreenplus.name"))
                    .category(ConfigCategory.createBuilder()
                            .name(Text.translatable("fullscreenplus.options.group.display"))
                            .tooltip(Text.translatable("fullscreenplus.config.heading.mode"))
                            .option(Option.<FullscreenMode>createBuilder()
                                    .name(Text.translatable("fullscreenplus.option.fullscreen_mode"))
                                    .description(OptionDescription.of(Text.translatable("fullscreenplus.option.fullscreen_mode.tooltip")))
                                    .binding(
                                            FullscreenMode.BORDERLESS,
                                            () -> config.fullscreen,
                                            value -> config.fullscreen = value
                                    )
                                    .controller(opt -> EnumControllerBuilder.create(opt)
                                            .enumClass(FullscreenMode.class)
                                            .formatValue(FullscreenMode::getCaption))
                                    .build())
                            .build())
                    .category(ConfigCategory.createBuilder()
                            .name(Text.translatable("fullscreenplus.options.group.performance"))
                            .option(Option.<Boolean>createBuilder()
                                    .name(Text.translatable("fullscreenplus.config.dynamic_resolution"))
                                    .description(OptionDescription.of(Text.translatable("fullscreenplus.config.dynamic_resolution.tooltip")))
                                    .binding(
                                            false,
                                            () -> config.dynamicResolution,
                                            value -> config.dynamicResolution = value
                                    )
                                    .controller(TickBoxControllerBuilder::create)
                                    .build())
                            .option(Option.<Integer>createBuilder()
                                    .name(Text.translatable("fullscreenplus.config.render_scale"))
                                    .description(OptionDescription.of(Text.translatable("fullscreenplus.config.render_scale.tooltip")))
                                    .binding(
                                            100,
                                            () -> config.renderScale,
                                            value -> config.renderScale = value
                                    )
                                    .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                            .range(25, 100)
                                            .step(5)
                                            .formatValue(v -> Text.literal(v + "%")))
                                    .build())
                            .option(Option.<Boolean>createBuilder()
                                    .name(Text.translatable("fullscreenplus.config.low_latency"))
                                    .description(OptionDescription.of(Text.translatable("fullscreenplus.config.low_latency.tooltip")))
                                    .binding(
                                            false,
                                            () -> config.lowLatencyMode,
                                            value -> config.lowLatencyMode = value
                                    )
                                    .controller(TickBoxControllerBuilder::create)
                                    .build())
                            .build())
                    .category(ConfigCategory.createBuilder()
                            .name(Text.translatable("fullscreenplus.options.group.window"))
                            .option(Option.<Boolean>createBuilder()
                                    .name(Text.translatable("fullscreenplus.config.windowed_borderless"))
                                    .description(OptionDescription.of(Text.translatable("fullscreenplus.config.windowed_borderless.tooltip")))
                                    .binding(
                                            true,
                                            () -> config.windowedBorderless,
                                            value -> config.windowedBorderless = value
                                    )
                                    .controller(TickBoxControllerBuilder::create)
                                    .build())
                            .option(Option.<Integer>createBuilder()
                                    .name(Text.translatable("fullscreenplus.config.windowed_borderless_width"))
                                    .binding(
                                            1280,
                                            () -> config.windowedBorderlessWidth,
                                            value -> config.windowedBorderlessWidth = value
                                    )
                                    .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                            .range(640, 3840)
                                            .step(10))
                                    .build())
                            .option(Option.<Integer>createBuilder()
                                    .name(Text.translatable("fullscreenplus.config.windowed_borderless_height"))
                                    .binding(
                                            720,
                                            () -> config.windowedBorderlessHeight,
                                            value -> config.windowedBorderlessHeight = value
                                    )
                                    .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                            .range(480, 2160)
                                            .step(10))
                                    .build())
                            .option(Option.<String>createBuilder()
                                    .name(Text.translatable("fullscreenplus.config.window_title"))
                                    .description(OptionDescription.of(Text.translatable("fullscreenplus.config.window_title.tooltip")))
                                    .binding(
                                            "",
                                            () -> config.windowTitle,
                                            value -> config.windowTitle = value
                                    )
                                    .controller(StringControllerBuilder::create)
                                    .build())
                            .build())
                    .category(ConfigCategory.createBuilder()
                            .name(Text.translatable("fullscreenplus.options.group.advanced"))
                            .option(Option.<Boolean>createBuilder()
                                    .name(Text.translatable("fullscreenplus.config.ultrawide_support"))
                                    .description(OptionDescription.of(Text.translatable("fullscreenplus.config.ultrawide_support.tooltip")))
                                    .binding(
                                            false,
                                            () -> config.ultrawideSupport,
                                            value -> config.ultrawideSupport = value
                                    )
                                    .controller(TickBoxControllerBuilder::create)
                                    .build())
                            .option(Option.<Boolean>createBuilder()
                                    .name(Text.translatable("fullscreenplus.config.hdr_passthrough"))
                                    .description(OptionDescription.of(Text.translatable("fullscreenplus.config.hdr_passthrough.tooltip")))
                                    .binding(
                                            false,
                                            () -> config.hdrPassthrough,
                                            value -> config.hdrPassthrough = value
                                    )
                                    .controller(TickBoxControllerBuilder::create)
                                    .build())
                            .option(Option.<Boolean>createBuilder()
                                    .name(Text.translatable("fullscreenplus.config.refresh_rate_override"))
                                    .description(OptionDescription.of(Text.translatable("fullscreenplus.config.refresh_rate_override.tooltip")))
                                    .binding(
                                            false,
                                            () -> config.refreshRateOverride,
                                            value -> config.refreshRateOverride = value
                                    )
                                    .controller(TickBoxControllerBuilder::create)
                                    .build())
                            .option(Option.<Integer>createBuilder()
                                    .name(Text.translatable("fullscreenplus.config.refresh_rate"))
                                    .description(OptionDescription.of(Text.translatable("fullscreenplus.config.refresh_rate.tooltip")))
                                    .binding(
                                            60,
                                            () -> config.refreshRate,
                                            value -> config.refreshRate = value
                                    )
                                    .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                            .range(30, 360)
                                            .step(1)
                                            .formatValue(v -> Text.literal(v + " Hz")))
                                    .build())
                            .option(Option.<Boolean>createBuilder()
                                    .name(Text.translatable("fullscreenplus.config.show_notifications"))
                                    .description(OptionDescription.of(Text.translatable("fullscreenplus.config.show_notifications.tooltip")))
                                    .binding(
                                            false,
                                            () -> config.showNotifications,
                                            value -> config.showNotifications = value
                                    )
                                    .controller(TickBoxControllerBuilder::create)
                                    .build())
                            .build())
                    .category(ConfigCategory.createBuilder()
                            .name(Text.translatable("fullscreenplus.config.heading.customization"))
                            .option(Option.<Boolean>createBuilder()
                                    .name(Text.translatable("fullscreenplus.config.customization.enabled"))
                                    .description(OptionDescription.of(Text.translatable("fullscreenplus.config.customization.enabled.tooltip")))
                                    .binding(
                                            false,
                                            () -> config.customized,
                                            value -> config.customized = value
                                    )
                                    .controller(TickBoxControllerBuilder::create)
                                    .build())
                            .option(Option.<Boolean>createBuilder()
                                    .name(Text.translatable("fullscreenplus.config.customization.related"))
                                    .description(OptionDescription.of(Text.translatable("fullscreenplus.config.customization.related.tooltip")))
                                    .binding(
                                            false,
                                            () -> config.related,
                                            value -> config.related = value
                                    )
                                    .controller(TickBoxControllerBuilder::create)
                                    .build())
                            .option(Option.<Integer>createBuilder()
                                    .name(Text.translatable("fullscreenplus.config.customization.x"))
                                    .binding(
                                            0,
                                            () -> config.x,
                                            value -> config.x = value
                                    )
                                    .controller(opt -> IntegerFieldControllerBuilder.create(opt))
                                    .build())
                            .option(Option.<Integer>createBuilder()
                                    .name(Text.translatable("fullscreenplus.config.customization.y"))
                                    .binding(
                                            0,
                                            () -> config.y,
                                            value -> config.y = value
                                    )
                                    .controller(opt -> IntegerFieldControllerBuilder.create(opt))
                                    .build())
                            .option(Option.<Integer>createBuilder()
                                    .name(Text.translatable("fullscreenplus.config.customization.width"))
                                    .binding(
                                            800,
                                            () -> config.width,
                                            value -> config.width = value
                                    )
                                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(1))
                                    .build())
                            .option(Option.<Integer>createBuilder()
                                    .name(Text.translatable("fullscreenplus.config.customization.height"))
                                    .binding(
                                            600,
                                            () -> config.height,
                                            value -> config.height = value
                                    )
                                    .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(1))
                                    .build())
                            .build())
                    .save(() -> {
                        config.save();
                        if (MinecraftClient.getInstance().getWindow() != null) {
                            FullscreenPlus.toggleFullScreenMode(
                                    MinecraftClient.getInstance().options,
                                    MinecraftClient.getInstance().options.getFullscreen().getValue()
                            );
                        }
                        if (config.showNotifications) {
                            FullscreenPlusNotifications.showSettingsSaved();
                        }
                    })
                    .build()
                    .generateScreen(parent);
        };
    }
}
