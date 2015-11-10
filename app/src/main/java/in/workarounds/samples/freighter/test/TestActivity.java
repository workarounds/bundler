package in.workarounds.samples.freighter.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import in.workarounds.bundler.annotations.Cargo;
import in.workarounds.bundler.annotations.Freighter;
import in.workarounds.samples.freighter.R;

/**
 * Created by madki on 16/10/15.
 */
@Freighter
public class TestActivity extends AppCompatActivity {
    @Cargo
    int integer;
    @Cargo
    int[] integerArray;
    @Cargo
    boolean bool;
    @Cargo
    boolean[] boolArray;
    @Cargo
    char character;
    @Cargo
    char[] characterArray;
    @Cargo
    long longNumber;
    @Cargo
    long[] longArray;
    @Cargo
    double doubleNumber;
    @Cargo
    double[] doubleArray;
    @Cargo
    byte aByte;
    @Cargo
    byte[] byteArray;
    @Cargo
    float floatNumber;
    @Cargo
    float[] floatArray;
    @Cargo
    short shortNumber;
    @Cargo
    short[] shortArray;
    @Cargo
    String string;
    @Cargo
    Integer some;
    @Cargo
    Bundle random;
    @Cargo
    Bundle[] bundles;
    @Cargo
    Integer[][] integers;
    @Cargo
    ArrayList<SomeObject> objects;
    @Cargo
    ArrayList<Integer>[][] randObjects;
    @Cargo
    CharSequence charSequence;
    @Cargo
    CharSequence[] charArray;
    @Cargo
    String[] strings;
    @Cargo
    ArrayList<String> stringArrayList;
    @Cargo
    ArrayList<Integer> integerArrayList;
    @Cargo
    SparseArray<Bundle> sparseArray;
    @Cargo
    ArrayList<Bundle> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public static class SomeObject {
        public int one;
        public String two;

        public SomeObject(int one, String two) {
            this.one = one;
            this.two = two;
        }

        @Override
        public String toString() {
            return "one = " + one + ", two = " + two;
        }
    }

    @Freighter
    public static class TestFragment extends Fragment {
        @Cargo
        int one;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }
}
