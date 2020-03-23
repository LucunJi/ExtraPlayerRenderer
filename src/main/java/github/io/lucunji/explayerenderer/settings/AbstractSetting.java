package github.io.lucunji.explayerenderer.settings;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Optional;

public abstract class AbstractSetting<T> {

    protected T value;

    public AbstractSetting(T defaultVal) {
        this.value = defaultVal;
    }

    protected abstract void read(JsonReader jsonReader) throws IOException;

    public Optional<T> get(){
        return Optional.of(value);
    }

    public void set(T newValue) {
        this.value = newValue;
    }

    public abstract void write(JsonWriter jsonWriter) throws IOException;
}
