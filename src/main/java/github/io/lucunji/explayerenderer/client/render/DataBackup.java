package github.io.lucunji.explayerenderer.client.render;


import net.minecraft.entity.LivingEntity;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class DataBackup<E extends LivingEntity> {
    private final E entity;
    private final List<DataBackupEntry<E, ?>> entries;

    public DataBackup(E entity, List<DataBackupEntry<E, ?>> entries) {
        this.entity = entity;
        this.entries = entries;
    }

    public void save() {
        this.entries.forEach(entry -> entry.save(this.entity));
    }

    public void restore() {
        for (int i = entries.size() - 1; i >= 0; i--) {
            entries.get(i).restore(this.entity);
        }
    }


    public static class DataBackupEntry<E extends LivingEntity, T> {
        private final Function<E, T> saver;
        private final BiConsumer<E, T> restorer;
        private T value = null;

        public DataBackupEntry(Function<E, T> saver, BiConsumer<E, T> restorer) {
            this.saver = saver;
            this.restorer = restorer;
        }

        private void save(E entity) {
            this.value = saver.apply(entity);
        }

        private void restore(E entity) {
            this.restorer.accept(entity, this.value);
        }
    }

}