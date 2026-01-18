package de.siphalor.amecs.gui;

//- import com.mojang.blaze3d.vertex.PoseStack;
import de.siphalor.amecs.Amecs;
import de.siphalor.amecs.compat.NMUKProxy;
import de.siphalor.amecs.impl.duck.IKeyBindingEntry;
import lombok.val;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
//- import net.minecraft.client.gui.screens.controls.ControlList;
//# if MC_VERSION_NUMBER >= 12100
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
//# else
//- import net.minecraft.client.gui.screens.controls.KeyBindsList;
//# end
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
//- import net.minecraft.network.chat.TextComponent;
//- import net.minecraft.network.chat.TranslatableComponent;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Environment(EnvType.CLIENT)
public class SearchFieldControlsListWidget
		//# if MC_VERSION_NUMBER >= 11802
		extends KeyBindsList.Entry
		//# else
		//- extends ControlList.Entry
		//# end
{
	private final EditBox searchField;

	private int lastEntryCount = 0;
	//# if MC_VERSION_NUMBER >= 11802
	private final Set<KeyBindsList.KeyEntry> entries =
	//# else
	//- private final Set<ControlList.KeyEntry> entries =
	//# end
			new TreeSet<>(Comparator.comparing(o -> ((IKeyBindingEntry) o).amecs$getKeyBinding()));

	public SearchFieldControlsListWidget(
			//# if MC_VERSION_NUMBER >= 11802
			KeyBindsList listWidget,
			//# else
			//- ControlList listWidget,
			//# end
			Minecraft minecraft
	) {
		Font font = minecraft.font;
		assert minecraft.screen != null;

		//# if MC_VERSION_NUMBER >= 12109
		setHeight(20);
		//# end
		searchField = new EditBox(
				font,
				minecraft.screen.width / 2 - 125,
				0,
				250,
				20,
				//# if MC_VERSION_NUMBER >= 11900
				Component.empty()
				//# else
				//- TextComponent.EMPTY
				//# end
		);
		searchField.setSuggestion(I18n.get("amecs.search.placeholder"));
		searchField.setResponder(searchText -> {
			if (searchText.isEmpty()) {
				searchField.setSuggestion(I18n.get("amecs.search.placeholder"));
			} else {
				searchField.setSuggestion("");
			}

			searchText = searchText.trim();
			listWidget.setScrollAmount(0);

			//# if MC_VERSION_NUMBER >= 12109
			val children = new ArrayList<>(listWidget.children());
			//# else
			//- val children = listWidget.children();
			//# end
			if (entries.isEmpty()) {
				//# if MC_VERSION_NUMBER >= 11802
				for (KeyBindsList.Entry entry : children) {
					if (entry instanceof KeyBindsList.KeyEntry) {
						entries.add((KeyBindsList.KeyEntry) entry);
					}
				}
				//# else
				//- for (ControlList.Entry entry : children) {
				//- 	if (entry instanceof ControlList.KeyEntry) {
				//- 		entries.add((ControlList.KeyEntry) entry);
				//- 	}
				//- }
				//# end
				lastEntryCount = children.size();
			}
			int childrenCount = children.size();
			if (childrenCount != lastEntryCount) {
				Amecs.log(Level.INFO, "Controls search results changed externally - recompiling the list!");
				try {
					//# if MC_VERSION_NUMBER >= 11802
					Constructor<KeyBindsList.KeyEntry> c = KeyBindsList.KeyEntry.class.getDeclaredConstructor(
							KeyBindsList.class, KeyMapping.class, Component.class
					);
					KeyBindsList.KeyEntry entry;
					//# else
					//- Constructor<ControlList.KeyEntry> c = ControlList.KeyEntry.class.getDeclaredConstructor(
					//- 		ControlList.class, KeyMapping.class, Component.class
					//- );
					//- ControlList.KeyEntry entry;
					//# end
					entries.clear();
					KeyMapping[] keyBindings = minecraft.options.keyMappings;
					Arrays.sort(keyBindings);
					//# if MC_VERSION_NUMBER >= 12109
					KeyMapping.Category lastCat = null;
					//# else
					//- String lastCat = null;
					//# end
					lastEntryCount = 1;
					for (KeyMapping keyBinding : keyBindings) {
						if (!Objects.equals(lastCat, keyBinding.getCategory())) {
							lastCat = keyBinding.getCategory();
							//# if MC_VERSION_NUMBER >= 12109
							children.add(listWidget.new CategoryEntry(keyBinding.getCategory()));
							//# elif MC_VERSION_NUMBER >= 11900
							//- children.add(listWidget.new CategoryEntry(Component.translatable(keyBinding.getCategory())));
							//# else
							//- children.add(listWidget.new CategoryEntry(new TranslatableComponent(keyBinding.getCategory())));
							//# end
							lastEntryCount++;
						}
						//# if MC_VERSION_NUMBER >= 11900
						entry = c.newInstance(listWidget, keyBinding, Component.translatable(keyBinding.getName()));
						//# else
						//- entry = c.newInstance(listWidget, keyBinding, new TranslatableComponent(keyBinding.getName()));
						//# end
						children.add(entry);
						entries.add(entry);
						lastEntryCount++;
					}
				} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
					Amecs.log(Level.ERROR, "An unexpected exception occurred during recompilation of controls list!", e);
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
			//# if MC_VERSION_NUMBER >= 12109
			KeyMapping.Category lastCat = null;
			//# else
			//- String lastCat = null;
			//# end
			boolean lastMatched = false;
			boolean includeCat = false;
			lastEntryCount = 1;
			//# if MC_VERSION_NUMBER >= 11802
			for (KeyBindsList.KeyEntry entry : entries) {
			//# else
			//- for (ControlList.KeyEntry entry : entries) {
			//# end
				KeyMapping binding = ((IKeyBindingEntry) entry).amecs$getKeyBinding();
				if (nmuk && lastMatched && NMUKProxy.isAlternative(binding)) {
					children.add(entry);
					lastEntryCount++;
					continue;
				}

				val cat = binding.getCategory();
				if (!cat.equals(lastCat)) {
					//# if MC_VERSION_NUMBER >= 12109
					includeCat = StringUtils.containsIgnoreCase(cat.label().getString(256), searchText);
					//# else
					//- includeCat = StringUtils.containsIgnoreCase(I18n.get(cat), searchText);
					//# end
				}
				if (
						(
								includeCat
										|| searchText == null
										|| StringUtils.containsIgnoreCase(I18n.get(((IKeyBindingEntry) entry).amecs$getKeyBinding().getName()), searchText)
						) && Amecs.entryKeyMatches(entry, keyFilter)
				) {
					if (!cat.equals(lastCat)) {
						//# if MC_VERSION_NUMBER >= 12109
						children.add(listWidget.new CategoryEntry(cat));
						//# elif MC_VERSION_NUMBER >= 11900
						//- children.add(listWidget.new CategoryEntry(Component.translatable(cat)));
						//# else
						//- children.add(listWidget.new CategoryEntry(new TranslatableComponent(cat)));
						//# end
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
				//# if MC_VERSION_NUMBER >= 11900
				MutableComponent noResultsText = Component.translatable(Amecs.MOD_ID + ".search.no_results");
				//# else
				//- MutableComponent noResultsText = new TranslatableComponent(Amecs.MOD_ID + ".search.no_results");
				//# end
				noResultsText.setStyle(noResultsText.getStyle().withColor(ChatFormatting.GRAY));
				// FIXME
				// children.add(listWidget.new CategoryEntry(noResultsText));
			}

			//# if MC_VERSION_NUMBER >= 12109
			listWidget.replaceEntries(children);
			//# end
		});
	}

	@Override
	public List<? extends GuiEventListener> children() {
		return Collections.singletonList(searchField);
	}

	//# if MC_VERSION_NUMBER >= 11700
	@Override
	public List<? extends NarratableEntry> narratables() {
		return Collections.singletonList(searchField);
	}
	//# end

	//# if MC_VERSION_NUMBER >= 12109
	@Override
	public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
		return searchField.mouseClicked(mouseButtonEvent, bl);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent mouseButtonEvent) {
		return searchField.mouseReleased(mouseButtonEvent);
	}

	@Override
	public boolean keyPressed(KeyEvent keyEvent) {
		return searchField.keyPressed(keyEvent);
	}

	@Override
	public boolean charTyped(CharacterEvent characterEvent) {
		return searchField.charTyped(characterEvent);
	}

	@Override
	public void setY(int y) {
		super.setY(y);
		searchField.setY(y);
	}

	@Override
	public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		searchField.renderWidget(guiGraphics, mouseX, mouseY, tickDelta);
	}
	//# else
	//- @Override
	//- public boolean mouseClicked(double double_1, double double_2, int int_1) {
	//- 	return searchField.mouseClicked(double_1, double_2, int_1);
	//- }

	//- @Override
	//- public boolean mouseReleased(double double_1, double double_2, int int_1) {
	//- 	return searchField.mouseReleased(double_1, double_2, int_1);
	//- }

	//- @Override
	//- public boolean keyPressed(int int_1, int int_2, int int_3) {
	//- 	return searchField.keyPressed(int_1, int_2, int_3);
	//- }

	//- @Override
	//- public boolean charTyped(char char_1, int int_1) {
	//- 	return searchField.charTyped(char_1, int_1);
	//- }
	//- @Override
	//- public void render(
	//- 		//# if MC_VERSION_NUMBER >= 12000
	//- 		GuiGraphics drawContext,
	//- 		//# else
	//- 		PoseStack drawContext,
	//- 		//# end
	//- 		int index,
	//- 		int y,
	//- 		int x,
	//- 		int entryWidth,
	//- 		int entryHeight,
	//- 		int mouseX,
	//- 		int mouseY,
	//- 		boolean var8,
	//- 		float tickDelta
	//- ) {
	//- 	//# if MC_VERSION_NUMBER >= 11903
	//- 	searchField.setY(y);
	//- 	//# else
	//- 	searchField.y = y;
	//- 	//# end
	//- 	searchField.render(drawContext, mouseX, mouseY, tickDelta);
	//- }
	//# end

	//# if MC_VERSION_NUMBER >= 11904
	@Override
	public void setFocused(boolean focused) {
		searchField.setFocused(focused);
	}
	//# else
	//- @Override
	//- public boolean changeFocus(boolean focused) {
	//- 	return searchField.changeFocus(focused);
	//- }
	//# end

	//# if MC_VERSION_NUMBER >= 11904
	@Override
	public void refreshEntry() {

	}
	//# end
}
