package in.workarounds.bundler.parceler;

import android.os.Bundle;

import org.parceler.Parcels;

import in.workarounds.bundler.annotations.Serializer;

public class ParcelerSerializer implements Serializer<Object> {
    @Override
    public void put(String key, Object value, Bundle bundle) {
        bundle.putParcelable(key, Parcels.wrap(value));
    }

    @Override
    public <V> V get(String key, Bundle bundle) {
        return Parcels.unwrap(bundle.getParcelable(key));
    }
}
