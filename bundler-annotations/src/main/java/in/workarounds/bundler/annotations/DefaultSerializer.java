package in.workarounds.bundler.annotations;

/**
 * An empty class to indicate that default serialization and deserialization is to be used to put
 * and get the value from the bundle.
 */
public class DefaultSerializer extends ArgSerializer {
    private DefaultSerializer() {
        throw new IllegalStateException("No instances");
    }

    @Override
    public Object serialize(Object value) {
        return value;
    }

    @Override
    public Object deserialize(Object data) {
        return data;
    }
}
