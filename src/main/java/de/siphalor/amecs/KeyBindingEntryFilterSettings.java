package de.siphalor.amecs;

import java.util.Objects;

public class KeyBindingEntryFilterSettings {
	public final String searchText;
	public final String keyFilter;
	
	public KeyBindingEntryFilterSettings(String searchText, String keyFilter) {
		this.searchText = searchText;
		this.keyFilter = keyFilter;
	}
	
    public static KeyBindingEntryFilterSettings parseFromInputString(String inputString) {
    	inputString = inputString.trim();
		String keyFilter = null;
		String searchText = null;
		int keyDelimiterPos = inputString.indexOf('=');
		if (keyDelimiterPos == 0) {
			// = at the bigging -> only keyFilter 
			keyFilter = inputString.substring(1).trim();
		} else if (keyDelimiterPos > 0) {
			// = in the middler -> searchText until = and keyFilter after =
			keyFilter = inputString.substring(keyDelimiterPos + 1).trim();
			searchText = inputString.substring(0, keyDelimiterPos).trim();
		} else {
			// = not found -> only searchText
			searchText = inputString;
		}
		return new KeyBindingEntryFilterSettings(searchText, keyFilter);
    }

	@Override
	public int hashCode() {
		return Objects.hash(keyFilter, searchText);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KeyBindingEntryFilterSettings other = (KeyBindingEntryFilterSettings) obj;
		return Objects.equals(keyFilter, other.keyFilter) && Objects.equals(searchText, other.searchText);
	}
}
