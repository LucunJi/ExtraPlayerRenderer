package github.io.lucunji.explayerenderer.settings;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class IntegerSetting extends AbstractSetting<Integer> {

    public IntegerSetting(Integer defaultVal) {
        super(defaultVal);
    }

    @Override
    protected void read(JsonReader jsonReader) throws IOException {
        this.value = jsonReader.nextInt();
    }

    @Override
    public void write(JsonWriter jsonWriter) throws IOException {
        jsonWriter.value(value);
    }
}
