package github.io.lucunji.explayerenderer.client.gui.hud;


import it.unimi.dsi.fastutil.objects.*;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class DataBackup<T> {
    private final T target;
    private final Map<DataBackupEntry<T, ?>, Object> data;

    public DataBackup(T target, List<DataBackupEntry<T, ?>> entries) {
        this.target = target;
        this.data = new Reference2ObjectOpenHashMap<>();
        entries.forEach(entry -> data.put(entry, null));
    }

    public final void save() {
        this.data.replaceAll((k, v) -> k.saver.apply(target));
    }

    @SuppressWarnings("unchecked")
    public final void restore() {
        this.data.forEach((key, val) -> ((BiConsumer<Object, Object>) key.restorer).accept(target, val));
    }


    public static class DataBackupEntry<U, V> {
        private final Function<U, V> saver;
        private final BiConsumer<U, V> restorer;

        public DataBackupEntry(Function<U, V> saver, BiConsumer<U, V> restorer) {
            this.saver = saver;
            this.restorer = restorer;
        }
    }
}