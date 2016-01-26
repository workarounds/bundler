package in.workarounds.bundler.annotations;

import android.os.Bundle;

/**
 * With this class you can provide your own serialization and deserialization implementation to put
 * and get a custom type from the bundle. The class must have a default empty constructor which
 * will be used for instantiation
 *
 * @param <T> the custom type that will be serialized by this serializer
 */
public interface Serializer<T> {

    /**
     * Put (save) a value into the bundle.
     *
     * @param key    The key you have to use as the key for the bundle to save the value
     * @param value  The value you have to save into the bundle (for the given key)
     * @param bundle The Bundle to save key / value. It's not null.
     */
    void put(String key, T value, Bundle bundle);

    /**
     * Get a value from the bundle
     *
     * @param key    The key for the value
     * @param bundle The Bundle where the value is saved in
     * @param <V>    The return type expected
     * @return The value retrieved from the Bundle with the given key
     */
    <V extends T> V get(String key, Bundle bundle);

}
