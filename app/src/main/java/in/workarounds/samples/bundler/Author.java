package in.workarounds.samples.bundler;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

/**
 * A demo object that uses @Parcel annotation from Parceler library to serialize as a Parcelable
 */
@Parcel
public class Author {
    String name;

    @ParcelConstructor
    public Author(String name) {
        this.name = name;
    }
}
