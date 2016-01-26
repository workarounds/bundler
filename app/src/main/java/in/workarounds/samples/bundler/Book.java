package in.workarounds.samples.bundler;

import android.os.Parcelable;

import auto.parcel.AutoParcel;

/**
 * Demo object that uses @AutoParcel annotation to implement Parcelable
 */
@AutoParcel
public abstract class Book implements Parcelable {
    public static final int FICTION = 1;
    public static final int NON_FICTION = 2;

    public abstract String name();

}
