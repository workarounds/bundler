package in.workarounds.bundler.annotations;

import android.os.Bundle;

/**
 * An empty class to indicate that default serialization and deserialization is to be used to put
 * and get the value from the bundle.
 */
public class DefaultSerializer implements Serializer {

    private DefaultSerializer() {
        throw new IllegalStateException("No instances");
    }

    @Override
    public void put(String key, Object value, Bundle bundle) {
    }

    @Override
    public Object get(String key, Bundle bundle) {
        return null;
    }
}
