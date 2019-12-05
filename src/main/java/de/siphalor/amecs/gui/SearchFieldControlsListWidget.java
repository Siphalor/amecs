package de.siphalor.amecs.gui;

import de.siphalor.amecs.util.IKeyBindingEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.controls.ControlsListWidget;
import net.minecraft.client.gui.screen.controls.ControlsOptionsScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class SearchFieldControlsListWidget extends ControlsListWidget.Entry {
	protected MinecraftClient minecraft;

	private TextFieldWidget textFieldWidget;

	private Set<ControlsListWidget.KeyBindingEntry> entries = new TreeSet<>(Comparator.comparing(o -> ((IKeyBindingEntry) o).amecs$getKeyBinding()));

	public SearchFieldControlsListWidget(MinecraftClient minecraftClient) {
		minecraft = minecraftClient;
		TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
		assert minecraft.currentScreen != null;
		textFieldWidget = new TextFieldWidget(textRenderer, minecraft.currentScreen.width / 2 - 100, 0, 200, 20, "");
		textFieldWidget.setChangedListener(searchText -> {
			if(minecraftClient.currentScreen instanceof ControlsOptionsScreen) {
				searchText = searchText.trim();
				for(Element child : minecraftClient.currentScreen.children()) {
					if(child instanceof ControlsListWidget) {
						ControlsListWidget controlsListWidget = (ControlsListWidget) child;
						controlsListWidget.setScrollAmount(0);

						if(entries.size() <= 0)
							entries.addAll(controlsListWidget.children().stream().filter(entry -> entry instanceof ControlsListWidget.KeyBindingEntry).map(entry -> (ControlsListWidget.KeyBindingEntry) entry).collect(Collectors.toSet()));

						controlsListWidget.children().clear();

						controlsListWidget.children().add(this);

						String keyFilter = null;
						int keyDelimiterPos = searchText.indexOf('=');
						if(keyDelimiterPos == 0) {
							keyFilter = searchText.substring(1).trim();
							searchText = null;
						} else if(keyDelimiterPos > 0) {
							keyFilter = searchText.substring(keyDelimiterPos + 1).trim();
							searchText = searchText.substring(0, keyDelimiterPos).trim();
						}

						String lastCat = null;
						boolean includeCat = false;
						for(ControlsListWidget.KeyBindingEntry entry : entries) {
							final String cat = ((IKeyBindingEntry) entry).amecs$getKeyBinding().getCategory();
							if(!cat.equals(lastCat)) {
								includeCat = StringUtils.containsIgnoreCase(I18n.translate(cat), searchText);
							}
							if(
								includeCat
								|| (
									(searchText == null || StringUtils.containsIgnoreCase(((IKeyBindingEntry) entry).amecs$getBindingName(), searchText))
									&& (keyFilter == null || StringUtils.containsIgnoreCase(((IKeyBindingEntry) entry).amecs$getKeyBinding().getLocalizedName(), keyFilter))
								)
							) {
								if(!cat.equals(lastCat)) {
									controlsListWidget.children().add(controlsListWidget.new CategoryEntry(cat));
									lastCat = cat;
								}
								controlsListWidget.children().add(entry);
							}
						}
					}
				}
			}
		});
	}

	@Override
	public List<? extends Element> children() {
		return Collections.singletonList(textFieldWidget);
	}

	@Override
	public boolean mouseClicked(double double_1, double double_2, int int_1) {
		return textFieldWidget.mouseClicked(double_1, double_2, int_1);
	}

	@Override
	public boolean mouseReleased(double double_1, double double_2, int int_1) {
		return textFieldWidget.mouseReleased(double_1, double_2, int_1);
	}

	@Override
	public boolean keyPressed(int int_1, int int_2, int int_3) {
		return textFieldWidget.keyPressed(int_1, int_2, int_3);
	}

	@Override
	public boolean charTyped(char char_1, int int_1) {
		return textFieldWidget.charTyped(char_1, int_1);
	}

	@Override
	public boolean changeFocus(boolean boolean_1) {
		return textFieldWidget.changeFocus(boolean_1);
	}

	@Override
	public void render(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, float var9) {
		textFieldWidget.y = var2;
		textFieldWidget.render(var6, var7, var9);
	}
}
