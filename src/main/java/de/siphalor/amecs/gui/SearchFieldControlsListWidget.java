package de.siphalor.amecs.gui;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import de.siphalor.amecs.Amecs;
import de.siphalor.amecs.compat.NMUKProxy;
import de.siphalor.amecs.impl.duck.IKeyBindingEntry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class SearchFieldControlsListWidget extends ControlsListWidget.Entry {
	private static final Constructor<ControlsListWidget.KeyBindingEntry> KeyBindingEntry_contructor;
	
	static {
		Constructor<ControlsListWidget.KeyBindingEntry> local_KeyBindingEntry_contructor = null;
		try {
			//noinspection JavaReflectionMemberAccess
			local_KeyBindingEntry_contructor = ControlsListWidget.KeyBindingEntry.class.getDeclaredConstructor(
				ControlsListWidget.class, KeyBinding.class, Text.class);
			local_KeyBindingEntry_contructor.setAccessible(true);
		} catch(NoSuchMethodException | SecurityException e) {
			Amecs.log(Level.ERROR, "Failed to load constructor from class \"KeyBindingEntry\" with reflection");
			e.printStackTrace();
		}
		
		KeyBindingEntry_contructor = local_KeyBindingEntry_contructor;
	}
	
	// we do not need them to be saved in this object
//	protected ControlsOptionsScreen parent;
//	protected MinecraftClient client;

	public final TextFieldWidget textFieldWidget;

	private int lastEntryCount = 0;
	private final Set<ControlsListWidget.KeyBindingEntry> entries = new TreeSet<>(Comparator.comparing(o -> ((IKeyBindingEntry) o).amecs$getKeyBinding()));

	public SearchFieldControlsListWidget(ControlsOptionsScreen parent, MinecraftClient client) {
//		this.parent = parent;
//		this.client = client;
		TextRenderer textRenderer = client.textRenderer;
		assert parent != null;

		textFieldWidget = new TextFieldWidget(textRenderer, parent.width / 2 - 100, 0, 200, 20, new LiteralText(""));
		textFieldWidget.setSuggestion(I18n.translate("amecs.search.placeholder"));
		textFieldWidget.setChangedListener(searchText -> {
			ControlsListWidget listWidget = null;
			for (Element child : parent.children()) {
				if (child instanceof ControlsListWidget) {
					listWidget = (ControlsListWidget) child;
					break;
				}
			}
			assert listWidget != null;

			if (searchText.isEmpty()) {
				textFieldWidget.setSuggestion(I18n.translate("amecs.search.placeholder"));
			} else {
				textFieldWidget.setSuggestion("");
			}

			searchText = searchText.trim();
			listWidget.setScrollAmount(0);

			List<ControlsListWidget.Entry> children = listWidget.children();
			if (entries.isEmpty()) {
				for (ControlsListWidget.Entry entry : children) {
					if (entry instanceof ControlsListWidget.KeyBindingEntry) {
						entries.add((ControlsListWidget.KeyBindingEntry) entry);
					}
				}
				lastEntryCount = children.size();
			}
			int childrenCount = children.size();
			if (childrenCount != lastEntryCount) {
				Amecs.log(Level.INFO, "Controls search results changed externally - recompiling the list!");
				try {
					entries.clear();
					KeyBinding[] keyBindings = client.options.keysAll.clone();
					Arrays.sort(keyBindings);
					String lastCat = null;
					ControlsListWidget.KeyBindingEntry entry;
					lastEntryCount = 1;
					for (KeyBinding keyBinding : keyBindings) {
						if (!Objects.equals(lastCat, keyBinding.getCategory())) {
							lastCat = keyBinding.getCategory();
							children.add(listWidget.new CategoryEntry(new TranslatableText(keyBinding.getCategory())));
							lastEntryCount++;
						}
						entry = KeyBindingEntry_contructor.newInstance(listWidget, keyBinding, new TranslatableText(keyBinding.getTranslationKey()));
						children.add(entry);
						entries.add(entry);
						lastEntryCount++;
					}
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
					Amecs.log(Level.ERROR, "An unexpected exception occured during recompilation of controls list!");
					e.printStackTrace();
				}
			}

			children.clear();

			children.add(this);

			String keyFilter = null;
			int keyDelimiterPos = searchText.indexOf('=');
			if (keyDelimiterPos == 0) {
				keyFilter = searchText.substring(1).trim();
				searchText = null;
			} else if (keyDelimiterPos > 0) {
				keyFilter = searchText.substring(keyDelimiterPos + 1).trim();
				searchText = searchText.substring(0, keyDelimiterPos).trim();
			}

			final boolean nmuk = FabricLoader.getInstance().isModLoaded("nmuk");
			String lastCat = null;
			boolean lastMatched = false;
			boolean includeCat = false;
			lastEntryCount = 1;
			for (ControlsListWidget.KeyBindingEntry entry : entries) {
				KeyBinding binding = ((IKeyBindingEntry) entry).amecs$getKeyBinding();
				if (nmuk && lastMatched && NMUKProxy.isAlternative(binding)) {
					children.add(entry);
					lastEntryCount++;
					continue;
				}

				final String cat = binding.getCategory();
				if (!cat.equals(lastCat)) {
					includeCat = StringUtils.containsIgnoreCase(I18n.translate(cat), searchText);
				}
				if (
						(
								includeCat
										|| searchText == null
										|| StringUtils.containsIgnoreCase(I18n.translate(((IKeyBindingEntry) entry).amecs$getKeyBinding().getTranslationKey()), searchText)
						) && Amecs.entryKeyMatches(entry, keyFilter)
				) {
					if (!cat.equals(lastCat)) {
						children.add(listWidget.new CategoryEntry(new TranslatableText(cat)));
						lastCat = cat;
						lastEntryCount++;
					}
					children.add(entry);
					lastEntryCount++;
					lastMatched = true;
				} else {
					lastMatched = false;
				}
			}
			if (lastEntryCount <= 1) {
				BaseText noResultsText = new TranslatableText(Amecs.MOD_ID + ".search.no_results");
				noResultsText.setStyle(noResultsText.getStyle().withColor(Formatting.GRAY));
				children.add(listWidget.new CategoryEntry(noResultsText));
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
	public void render(MatrixStack matrixStack, int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, float var9) {
		textFieldWidget.y = var2;
		textFieldWidget.render(matrixStack, var6, var7, var9);
	}

	@Override
	public List<? extends Selectable> method_37025() {
		return Collections.singletonList(textFieldWidget);
	}
}
