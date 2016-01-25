package in.workarounds.bundler;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madki on 25/01/16.
 */
public class ParcelListSerializer<T extends Parcelable> implements Serializer<List<T>> {
    private static final String TAG = "ParcelListSerializer";

    @Override
    public void put(String key, List<T> value, Bundle bundle) {
        if(!(value instanceof ArrayList)) {
            throw new ClassCastException("ParcelableListSerializer can serialize List only if it's " +
                    "implemented as a " + ArrayList.class.getName() + ", but give value is implemented as " + value.getClass().getName());
        }
        bundle.putParcelableArrayList(key, (ArrayList<T>) value);
    }

    @Override
    public <V extends List<T>> V get(String key, Bundle bundle) {
        V value = null;
        try {
            value = (V) bundle.getParcelableArrayList(key);
        } catch (Exception e) {
            Log.w(TAG, e);
        }
        return value;
    }
}
