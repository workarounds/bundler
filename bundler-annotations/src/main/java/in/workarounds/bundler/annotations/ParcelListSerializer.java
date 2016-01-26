package in.workarounds.bundler.annotations;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Casts a List of parcelables into and ArrayList and puts in bundle
 */
public class ParcelListSerializer<T extends Parcelable> implements Serializer<List<T>> {
    private static final String TAG = "ParcelListSerializer";

    /**
     * Casts the List to ArrayList if the list is implemented as an ArrayList other wise throws
     * ClassCastException
     *
     * @param key    The key you have to use as the key for the bundle to save the value
     * @param value  The value you have to save into the bundle (for the given key)
     * @param bundle The Bundle to save key / value. It's not null.
     */
    @Override
    public void put(String key, List<T> value, Bundle bundle) {
        if (!(value instanceof ArrayList)) {
            throw new ClassCastException("ParcelableListSerializer can serialize List only if it's " +
                    "implemented as a " + ArrayList.class.getName() + ", but give value is implemented as " + value.getClass().getName());
        }
        bundle.putParcelableArrayList(key, (ArrayList<T>) value);
    }

    /**
     * @param key    The key for the value
     * @param bundle The Bundle where the value is saved in
     * @param <V>    The object type implementing the parcelable that is returned
     * @return The parcelable array list if it's of correct type, null otherwise
     */
    @Override
    @SuppressWarnings("unchecked")
    public <V extends List<T>> V get(String key, Bundle bundle) {
        try {
            return (V) bundle.getParcelableArrayList(key);
        } catch (ClassCastException e) {
            Log.w(TAG, "Unable to cast ArrayList<Parcelable> in the bundle to given type");
            Log.w(TAG, "Attempt to cast generated internal exception:", e);
            return null;
        }
    }
}
