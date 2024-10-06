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

package github.io.lucunji.extraplayerrenderer.config.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonToken;
import github.io.lucunji.extraplayerrenderer.ExtraPlayerRenderer;
import github.io.lucunji.extraplayerrenderer.config.model.ConfigOption;
import net.minecraft.resources.ResourceLocation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GsonConfigPersistence implements ConfigPersistence {
    private final Path path;
    private final Gson gson;

    public GsonConfigPersistence(Path path) {
        this.path = path;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public Path getPath() {
        return this.path;
    }

    @Override
    public boolean save(List<? extends ConfigOption<?>> options) {
        var categories = categorize(options);

        try (var writer = this.gson.newJsonWriter(new BufferedWriter(new FileWriter(this.path.toFile())))) {
            writer.beginObject();
            // for each category
            for (var entry : categories.entrySet()) {
                writer.name(entry.getKey().toString());
                writer.beginObject();

                // for each option in the category
                for (var optionEntry : entry.getValue().entrySet()) {
                    var id = optionEntry.getKey();
                    var option = optionEntry.getValue();
                    writer.name(id.toString());
                    this.gson.toJson(option.getValue(), option.getType(), writer);
                }

                writer.endObject();
            }
            writer.endObject();
        } catch (Exception e) {
            //noinspection StringConcatenationArgumentToLogCall
            ExtraPlayerRenderer.LOGGER.error("Failed to save config at " + this.path, e);
            return false;
        }
        return true;
    }

    @Override
    public boolean load(List<? extends ConfigOption<?>> options) {
        var categories = categorize(options);

        if (!this.path.toFile().exists() || !this.path.toFile().isFile()) {
            ExtraPlayerRenderer.LOGGER.info("Configuration is not found at {}", this.path);
            return false;
        }

        try (var reader = gson.newJsonReader(new BufferedReader(new FileReader(this.path.toFile())))) {
            reader.beginObject();

            // for each category
            while (reader.peek() == JsonToken.NAME) {
                var categoryName = reader.nextName();
                var category = categories.get(ResourceLocation.parse(categoryName));
                if (category == null)
                    throw new IllegalStateException("The category with key " + categoryName + "does not exist");

                reader.beginObject();

                // for each option in the category
                while (reader.peek() == JsonToken.NAME) {
                    var optionName = reader.nextName();
                    var option = category.get(ResourceLocation.parse(optionName));
                    if (option == null)
                        throw new IllegalStateException("The option with key " + optionName + " in category " + categoryName + " does not exist");

                    if (option.getType().isAssignableFrom(Integer.class))
                        //noinspection unchecked
                        ((ConfigOption<Integer>) option).setValue(reader.nextInt());
                    else if (option.getType().isAssignableFrom(Double.class))
                        //noinspection unchecked
                        ((ConfigOption<Double>) option).setValue(reader.nextDouble());
                    else if (option.getType().isAssignableFrom(Boolean.class))
                        //noinspection unchecked
                        ((ConfigOption<Boolean>) option).setValue(reader.nextBoolean());
                    else if (option.getType().isAssignableFrom(String.class))
                        //noinspection unchecked
                        ((ConfigOption<String>) option).setValue(reader.nextString());
                    else if (option.getType().isAssignableFrom(Long.class))
                        //noinspection unchecked
                        ((ConfigOption<Long>) option).setValue(reader.nextLong());
                    else if (option.getType().isEnum())
                        //noinspection unchecked,rawtypes
                        ((ConfigOption<Enum<?>>) option).setValue(Enum.valueOf(((Class) option.getType()), reader.nextString()));
                    else
                        throw new IllegalStateException("The option of type " + option.getType() + " could not be deserialized from a JSON value");
                }

                reader.endObject();
            }

            reader.endObject();
        } catch (Exception e) {
            //noinspection StringConcatenationArgumentToLogCall
            ExtraPlayerRenderer.LOGGER.error("Failed to load config at " + this.path, e);
            return false;
        }
        return true;
    }

    private Map<ResourceLocation, Map<ResourceLocation, ConfigOption<?>>> categorize(List<? extends ConfigOption<?>> options) {
        var categories = new LinkedHashMap<ResourceLocation, Map<ResourceLocation, ConfigOption<?>>>();
        for (ConfigOption<?> option : options)
            categories.computeIfAbsent(option.getCategory(), k -> new LinkedHashMap<>()).put(option.getId(), option);
        return categories;
    }
}
