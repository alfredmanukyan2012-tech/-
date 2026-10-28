package com.example.cheatinv;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ItemMenuScreen extends Screen {
    private static final int COLS = 12;
    private static final int ROWS = 6;
    private static final int SLOT = 18;

    private static String lastSearch = "";

    private final List<ItemStack> all = new ArrayList<>();
    private final List<ItemStack> filtered = new ArrayList<>();
    private TextFieldWidget search;
    private int scrollRow = 0;
    private int left;
    private int top;

    public ItemMenuScreen() {
        super(Text.translatable("cheatinv.title"));
    }

    @Override
    protected void init() {
        left = (width - COLS * SLOT) / 2;
        top = (height - ROWS * SLOT) / 2 + 8;

        if (all.isEmpty()) {
            for (Item item : Registries.ITEM) {
                if (item != Items.AIR) all.add(new ItemStack(item));
            }
        }

        search = new TextFieldWidget(textRenderer, left, top - 22, COLS * SLOT, 16, Text.translatable("cheatinv.title"));
        search.setText(lastSearch);
        search.setChangedListener(s -> {
            lastSearch = s;
            scrollRow = 0;
            applyFilter();
        });
        addDrawableChild(search);
        setInitialFocus(search);
        applyFilter();
    }

    private void applyFilter() {
        String q = search.getText().trim().toLowerCase(Locale.ROOT);
        filtered.clear();
        for (ItemStack stack : all) {
            if (q.isEmpty()
                    || stack.getName().getString().toLowerCase(Locale.ROOT).contains(q)
                    || Registries.ITEM.getId(stack.getItem()).toString().contains(q)) {
                filtered.add(stack);
            }
        }
    }

    private int maxScroll() {
        int totalRows = (filtered.size() + COLS - 1) / COLS;
        return Math.max(0, totalRows - ROWS);
    }

    @Override
    public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.renderBackground(ctx, mouseX, mouseY, delta);
        ctx.fill(left - 6, top - 28, left + COLS * SLOT + 6, top + ROWS * SLOT + 18, 0xE0101010);
        ctx.fill(left - 1, top - 1, left + COLS * SLOT + 1, top + ROWS * SLOT + 1, 0xFF373737);
        ctx.fill(left, top, left + COLS * SLOT, top + ROWS * SLOT, 0xFF8B8B8B);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);

        ItemStack hovered = null;
        for (int i = 0; i < COLS * ROWS; i++) {
            int idx = scrollRow * COLS + i;
            if (idx >= filtered.size()) break;

            int x = left + (i % COLS) * SLOT;
            int y = top + (i / COLS) * SLOT;
            ItemStack stack = filtered.get(idx);

            boolean over = mouseX >= x && mouseX < x + SLOT && mouseY >= y && mouseY < y + SLOT;
            if (over) {
                ctx.fill(x, y, x + SLOT, y + SLOT, 0x80FFFFFF);
                hovered = stack;
            }
            ctx.drawItem(stack, x + 1, y + 1);
        }

        ctx.drawCenteredTextWithShadow(textRenderer, Text.translatable("cheatinv.hint"),
                width / 2, top + ROWS * SLOT + 6, 0xAAAAAA);

        if (hovered != null) {
            ctx.drawItemTooltip(textRenderer, hovered, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        if (vertical != 0) {
            scrollRow = Math.max(0, Math.min(maxScroll(), scrollRow - (int) Math.signum(vertical)));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontal, vertical);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) return true;

        if (mouseX >= left && mouseX < left + COLS * SLOT && mouseY >= top && mouseY < top + ROWS * SLOT) {
            int col = (int) ((mouseX - left) / SLOT);
            int row = (int) ((mouseY - top) / SLOT);
            int idx = (scrollRow + row) * COLS + col;
            if (idx >= 0 && idx < filtered.size()) {
                ItemStack stack = filtered.get(idx);
                boolean full = hasShiftDown() || button == 1;
                int count = full ? stack.getMaxCount() : 1;
                ClientPlayNetworking.send(new GiveItemPayload(Registries.ITEM.getId(stack.getItem()), count));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
