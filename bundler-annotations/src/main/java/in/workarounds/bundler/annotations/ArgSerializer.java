package in.workarounds.bundler.annotations;

/**
 * With this class you can provide your own serialization and deserialization implementation to put
 * and get a custom type from the bundle. The class should have an empty constructor
 * @param <I> the input type that is to be serialized
 * @param <O> the serialized output type that bundler library recognizes
 */
public abstract class ArgSerializer<I, O> {

    /**
     * converts the value into a serialized object recognized by bundler library
     * @param value the value that requires serialization
     * @return serialized object recognized by bundler library
     */
    public abstract O serialize(I value);

    /**
     * converts the data (which is the serialized version) back into the original value
     * @param data the serialized version of the original value
     * @return the deserialized original value
     */
    public abstract I deserialize(O data);

}
