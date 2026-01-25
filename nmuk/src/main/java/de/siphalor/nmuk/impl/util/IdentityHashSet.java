/*
 * Copyright 2026 Siphalor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
