package de.siphalor.nmuk.impl.util;

import java.util.AbstractSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IdentityHashSet<T> extends AbstractSet<T> {
	private final IdentityHashMap<T, Boolean> backingMap;

	public IdentityHashSet(int expectedMaxSize) {
		backingMap = new IdentityHashMap<>(expectedMaxSize);
	}

	@Override
	public Iterator<T> iterator() {
		return backingMap.keySet().iterator();
	}

	@Override
	public int size() {
		return backingMap.size();
	}

	@Override
	public boolean add(T t) {
		return backingMap.put(t, true) == null;
	}

	@Override
	public boolean remove(Object o) {
		return backingMap.remove(o) != null;
	}
}
