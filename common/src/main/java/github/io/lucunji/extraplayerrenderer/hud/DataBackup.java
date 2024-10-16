/*
 *     Highly configurable paper doll mod.
 *     Copyright (C) 2024  LucunJi, And all  Contributors
 *
 *     This file is part of Extra Player Renderer.
 *
 *     Extra Player Renderer is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Extra Player Renderer is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Extra Player Renderer.  If not, see <https://www.gnu.org/licenses/>.
 */

package github.io.lucunji.extraplayerrenderer.hud;


import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;

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