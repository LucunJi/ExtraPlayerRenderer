package github.io.lucunji.explayerenderer.settings;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class BooleanSetting extends AbstractSetting<Boolean> {

    public BooleanSetting(Boolean defaultVal) {
        super(defaultVal);
    }

    @Override
    public void read(JsonReader jsonReader) throws IOException {
        this.value = jsonReader.nextBoolean();
    }

    @Override
    public void write(JsonWriter jsonWriter) throws IOException {
        jsonWriter.value(value);
    }
}
