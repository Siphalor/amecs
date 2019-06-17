package de.siphalor.amecs;

import de.siphalor.amecs.util.IKeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AmecsKeyBindingRegistry {
    public static Map<InputUtil.KeyCode, ConcurrentLinkedQueue<AmecsKeyBinding>> codeToBindings = new HashMap<>();

    public static void register(InputUtil.KeyCode keyCode, AmecsKeyBinding amecsKeyBinding) {
        if(codeToBindings.containsKey(keyCode))
            codeToBindings.get(keyCode).add(amecsKeyBinding);
        else
            codeToBindings.put(keyCode, new ConcurrentLinkedQueue<>(Collections.singleton(amecsKeyBinding)));
        ((IKeyBinding) amecsKeyBinding.getKeyBinding()).amecs$setAmecsKeyBinding(amecsKeyBinding);
    }

    public static void update() {
        Map<InputUtil.KeyCode, ConcurrentLinkedQueue<AmecsKeyBinding>> oldMap = codeToBindings;
        Stream<AmecsKeyBinding> amecsKeyBindingStream = codeToBindings.values().stream().flatMap(Collection::stream);
        codeToBindings = new HashMap<>();
        amecsKeyBindingStream.forEach(amecsKeyBinding -> register(((IKeyBinding) amecsKeyBinding.getKeyBinding()).getKeyCode(), amecsKeyBinding));
        oldMap.clear();
    }

    public static Stream<AmecsKeyBinding> getMatchingKeyBindings(InputUtil.KeyCode keyCode) {
        Stream<AmecsKeyBinding> result = codeToBindings.get(keyCode).stream().filter(AmecsKeyBinding::modifiersMatch);
        Set<AmecsKeyBinding> amecsKeyBindings = result.collect(Collectors.toSet());
        if(amecsKeyBindings.isEmpty())
            return codeToBindings.get(keyCode).stream().filter(amecsKeyBinding -> amecsKeyBinding.getModifiers() == 0);
        return amecsKeyBindings.stream();
    }
}
