package in.workarounds.samples.freighter;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madki on 20/10/15.
 */
public class TestModel<T extends Parcelable> implements Parcelable {

    private List<T> items;
    private String someField;

    public List<T> items() {
        return items;
    }

    public void setItems(List<T> newValue) {
        items = newValue;
    }

    public String someField() {
        return someField;
    }

    public void setSomeField(String newValue) {
        someField = newValue;
    }

    public List<T> getItems() {
        return items;
    }

    public String getSomeField() {
        return someField;
    }

    public TestModel() {
    }
//region: Parcelable implementation

    public TestModel(Parcel in) {
        someField = in.readString();

        int size = in.readInt();
        if (size == 0) {
            items = null;
        }

        else {

            Class<?> type = (Class<?>) in.readSerializable();

            items = new ArrayList<>(size);
            in.readList(items, type.getClassLoader());
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(someField);

        if (items == null || items.size() == 0)
            dest.writeInt(0);

        else {
            dest.writeInt(items.size());

            final Class<?> objectsType = items.get(0).getClass();
            dest.writeSerializable(objectsType);

            dest.writeList(items);
        }

        dest.writeInt(items.size());
        for(int i=0;i<items.size();i++)
            items.get(i).writeToParcel(dest, flags);
    }

    public static final Parcelable.Creator<TestModel> CREATOR = new Parcelable.Creator<TestModel>() {
        public TestModel createFromParcel(Parcel in) {
            return new TestModel(in);
        }

        public TestModel[] newArray(int size) {
            return new TestModel[size];
        }
    };

//endregion
}

