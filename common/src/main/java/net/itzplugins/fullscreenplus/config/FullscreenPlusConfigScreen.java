package net.itzplugins.fullscreenplus.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class FullscreenPlusConfigScreen extends Screen {

    private final Screen parent;
    private final List<ClickableWidget> optionWidgets = new ArrayList<>();
    private int scrollOffset = 0;

    protected FullscreenPlusConfigScreen(Text title, Screen parent) {
        super(title);
        this.parent = parent;
    }

    @Override
    protected final void init() {
        optionWidgets.clear();
        addElements();
        
        int yStart = 40;
        for (int i = 0; i < optionWidgets.size(); i++) {
            ClickableWidget widget = optionWidgets.get(i);
            widget.setY(yStart + i * 25);
            addDrawableChild(widget);
        }
        
        addDrawableChild(
                ButtonWidget.builder(Text.translatable("fullscreenplus.option.apply"), (button) -> save())
                        .position(this.width / 2 - 175, this.height - 27)
                        .size(150, 20)
                        .build()
        );
        addDrawableChild(
                ButtonWidget.builder(ScreenTexts.DONE, (button) -> {
                            save();
                            this.client.setScreen(parent);
                        })
                        .position(this.width / 2 + 25, this.height - 27)
                        .size(150, 20)
                        .build()
        );
    }

    @Override
    public final void removed() {
        save();
    }

    public abstract void save();

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 16777215);
    }

    public abstract void addElements();

    public void addOption(SimpleOption<?> opt) {
        optionWidgets.add(opt.createWidget(MinecraftClient.getInstance().options, width / 2 - 155, 0, 310));
    }

    public void addHeading(Text text) {
        optionWidgets.add(ButtonWidget.builder(text, b -> {})
                .position(width / 2 - 155, 0)
                .size(310, 20)
                .build());
    }

    public void addIntField(Text description, Supplier<Integer> getter, Consumer<Integer> setter) {
        optionWidgets.add(ButtonWidget.builder(
                Text.literal(description.getString() + ": " + getter.get()),
                b -> {})
                .position(width / 2 - 155, 0)
                .size(310, 20)
                .build());
    }

}
