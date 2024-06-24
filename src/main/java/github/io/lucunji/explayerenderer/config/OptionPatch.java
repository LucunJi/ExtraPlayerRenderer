package github.io.lucunji.explayerenderer.config;

public interface OptionPatch<T> {
    T extraPlayerRenderer$getSavedValue();
    boolean extraPlayerRenderer$savePendingValue();
    void extraPlayerRenderer$restoreSavedValue();
}
