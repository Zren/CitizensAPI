package net.citizensnpcs.api.npc.trait;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.citizensnpcs.api.npc.NPC;

public class DefaultInstanceFactory<T> implements InstanceFactory<T> {
    private final Map<String, Factory<? extends T>> registered = new HashMap<String, Factory<? extends T>>();

    @Override
    public T getInstance(String name, NPC npc) {
        return registered.containsKey(name) ? registered.get(name).create(npc) : null;
    }

    @Override
    public void register(Class<? extends T> clazz) {
        registerWithFactory(clazz.getAnnotation(SaveId.class).value(), new DefaultFactory(clazz));
    }

    @Override
    public void registerAll(Collection<Class<? extends T>> classes) {
        for (Class<? extends T> clazz : classes) {
            register(clazz);
        }
    }

    @Override
    public void registerWithFactory(String name, Factory<? extends T> factory) {
        if (registered.get(name) != null)
            throw new IllegalArgumentException("A factory with the name '" + name + "' has already been registered.");
        registered.put(name, factory);
    }

    private class DefaultFactory implements Factory<T> {
        private final Class<? extends T> clazz;
        private Constructor<? extends T> constructor;

        private DefaultFactory(Class<? extends T> clazz) {
            this.clazz = clazz;
            try {
                constructor = clazz.getConstructor(NPC.class);
            } catch (Exception ex) {
                constructor = null;
            }
        }

        @Override
        public T create(NPC npc) {
            try {
                return constructor != null ? (T) constructor.newInstance(npc) : (T) clazz.newInstance();
            } catch (Exception ex) {
                return null;
            }
        }
    }
}