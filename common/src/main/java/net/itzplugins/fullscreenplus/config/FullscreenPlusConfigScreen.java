package net.itzplugins.fullscreenplus.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
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
    private final List<IntFieldEntry> intFields = new ArrayList<>();

    protected FullscreenPlusConfigScreen(Text title, Screen parent) {
        super(title);
        this.parent = parent;
    }

    @Override
    protected final void init() {
        optionWidgets.clear();
        intFields.clear();
        addElements();

        int yStart = 48;
        int spacing = 28;
        for (int i = 0; i < optionWidgets.size(); i++) {
            ClickableWidget widget = optionWidgets.get(i);
            widget.setX(this.width / 2 - 155);
            widget.setY(yStart + i * spacing);
            addDrawableChild(widget);
        }

        addDrawableChild(
                ButtonWidget.builder(Text.translatable("fullscreenplus.option.apply"), button -> {
                            applyIntFields();
                            save();
                        })
                        .position(this.width / 2 - 155, this.height - 28)
                        .size(150, 20)
                        .build()
        );
        addDrawableChild(
                ButtonWidget.builder(ScreenTexts.DONE, button -> {
                            applyIntFields();
                            save();
                            this.client.setScreen(parent);
                        })
                        .position(this.width / 2 + 5, this.height - 28)
                        .size(150, 20)
                        .build()
        );
    }

    private void applyIntFields() {
        for (IntFieldEntry entry : intFields) {
            try {
                int value = Integer.parseInt(entry.field.getText());
                entry.setter.accept(value);
            } catch (NumberFormatException ignored) {
            }
        }
    }

    @Override
    public void close() {
        applyIntFields();
        save();
        this.client.setScreen(parent);
    }

    public abstract void save();

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);

        for (IntFieldEntry entry : intFields) {
            context.drawTextWithShadow(this.textRenderer, entry.label, entry.field.getX() - this.textRenderer.getWidth(entry.label) - 8, entry.field.getY() + 6, 0xAAAAAA);
        }
    }

    public abstract void addElements();

    public void addOption(SimpleOption<?> opt) {
        optionWidgets.add(opt.createWidget(MinecraftClient.getInstance().options, 0, 0, 310));
    }

    public void addHeading(Text text) {
        ButtonWidget heading = ButtonWidget.builder(text, b -> {})
                .size(310, 20)
                .build();
        heading.active = false;
        optionWidgets.add(heading);
    }

    public void addIntField(Text label, Supplier<Integer> getter, Consumer<Integer> setter) {
        TextFieldWidget field = new TextFieldWidget(this.textRenderer, 0, 0, 100, 20, label);
        field.setText(String.valueOf(getter.get()));
        field.setChangedListener(text -> {
            try {
                Integer.parseInt(text);
                field.setEditableColor(0xFFFFFF);
            } catch (NumberFormatException e) {
                field.setEditableColor(0xFF5555);
            }
        });

        intFields.add(new IntFieldEntry(label.getString(), field, setter));
        optionWidgets.add(field);
    }

    private record IntFieldEntry(String label, TextFieldWidget field, Consumer<Integer> setter) {}
}
