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
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class FullscreenPlusConfigScreen extends Screen {

    private final Screen parent;
    private final List<ClickableWidget> widgets = new ArrayList<>();
    private final List<TextFieldWidget> textFields = new ArrayList<>();
    private final List<TextFieldEntry> fieldEntries = new ArrayList<>();
    private final List<StringFieldEntry> stringFieldEntries = new ArrayList<>();
    private int currentY;

    protected FullscreenPlusConfigScreen(Text title, Screen parent) {
        super(title);
        this.parent = parent;
    }

    @Override
    protected void init() {
        widgets.clear();
        textFields.clear();
        fieldEntries.clear();
        stringFieldEntries.clear();
        currentY = 40;

        addElements();

        for (ClickableWidget w : widgets) {
            addDrawableChild(w);
        }
        for (TextFieldWidget tf : textFields) {
            addDrawableChild(tf);
        }

        addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> close())
                .dimensions(this.width / 2 - 100, this.height - 27, 200, 20)
                .build());
    }

    @Override
    public void close() {
        for (TextFieldEntry entry : fieldEntries) {
            try {
                int val = Integer.parseInt(entry.field.getText());
                entry.setter.accept(val);
            } catch (NumberFormatException ignored) {
            }
        }
        save();
        this.client.setScreen(parent);
    }

    public abstract void save();

    public abstract void addElements();

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);

        for (TextFieldEntry entry : fieldEntries) {
            context.drawTextWithShadow(this.textRenderer, entry.label,
                    entry.field.getX() - this.textRenderer.getWidth(entry.label) - 10, entry.field.getY() + 6,
                    0xAAAAAA);
        }
    }

    public void addOption(SimpleOption<?> option) {
        ClickableWidget w = option.createWidget(MinecraftClient.getInstance().options, this.width / 2 - 155, currentY,
                310);
        widgets.add(w);
        currentY += 25;
    }

    public void addHeading(Text text) {
        currentY += 8;
        ButtonWidget heading = ButtonWidget.builder(text.copy().formatted(Formatting.YELLOW), b -> {
        })
                .dimensions(this.width / 2 - 155, currentY, 310, 20)
                .build();
        heading.active = false;
        widgets.add(heading);
        currentY += 28;
    }

    public void addBooleanOption(Text label, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        final boolean[] value = { getter.get() };
        ButtonWidget btn = ButtonWidget.builder(getBoolText(label, value[0]), b -> {
            value[0] = !value[0];
            setter.accept(value[0]);
            b.setMessage(getBoolText(label, value[0]));
        }).dimensions(this.width / 2 - 155, currentY, 310, 20).build();
        widgets.add(btn);
        currentY += 25;
    }

    private Text getBoolText(Text label, boolean val) {
        return Text.literal("").append(label).append(": ")
                .append(val ? Text.literal("ON").formatted(Formatting.GREEN)
                        : Text.literal("OFF").formatted(Formatting.RED));
    }

    public void addIntField(Text label, Supplier<Integer> getter, Consumer<Integer> setter) {
        TextFieldWidget field = new TextFieldWidget(this.textRenderer, this.width / 2 + 5, currentY, 150, 20, label);
        field.setText(String.valueOf(getter.get()));
        field.setChangedListener(t -> {
            try {
                Integer.parseInt(t);
                field.setEditableColor(0xE0E0E0);
            } catch (NumberFormatException e) {
                field.setEditableColor(0xFF6B6B);
            }
        });
        textFields.add(field);
        fieldEntries.add(new TextFieldEntry(label.getString(), field, setter));
        currentY += 25;
    }

    public void addStringField(Text label, Supplier<String> getter, Consumer<String> setter) {
        TextFieldWidget field = new TextFieldWidget(this.textRenderer, this.width / 2 + 5, currentY, 150, 20, label);
        field.setText(getter.get() != null ? getter.get() : "");
        field.setMaxLength(100);
        field.setChangedListener(setter::accept);
        textFields.add(field);
        stringFieldEntries.add(new StringFieldEntry(label.getString(), field));
        currentY += 25;
    }

    public void addSlider(Text label, int min, int max, Supplier<Integer> getter, Consumer<Integer> setter) {
        double initial = (getter.get() - min) / (double) (max - min);
        final int[] currentValue = { getter.get() };

        ButtonWidget slider = ButtonWidget.builder(
                Text.literal("").append(label).append(": ").append(Text.literal(String.valueOf(currentValue[0]))),
                b -> {
                }).dimensions(this.width / 2 - 155, currentY, 310, 20).build();

        widgets.add(slider);
        currentY += 25;
    }

    private record TextFieldEntry(String label, TextFieldWidget field, Consumer<Integer> setter) {
    }

    private record StringFieldEntry(String label, TextFieldWidget field) {
    }
}
