package in.workarounds.samples.bundler.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import in.workarounds.bundler.annotations.Arg;
import in.workarounds.bundler.annotations.RequireBundler;
import in.workarounds.samples.bundler.R;

/**
 * Created by madki on 16/10/15.
 */
@RequireBundler(requireAll = false, bundlerMethod = "someTestActivity")
public class TestActivity extends AppCompatActivity {
    @Arg
    int integer;
    @Arg
    int[] integerArray;
    @Arg
    boolean bool;
    @Arg
    boolean[] boolArray;
    @Arg
    char character;
    @Arg
    char[] characterArray;
    @Arg
    long longNumber;
    @Arg
    long[] longArray;
    @Arg
    double doubleNumber;
    @Arg
    double[] doubleArray;
    @Arg
    byte aByte;
    @Arg
    byte[] byteArray;
    @Arg
    float floatNumber;
    @Arg
    float[] floatArray;
    @Arg
    short shortNumber;
    @Arg
    short[] shortArray;
    @Arg
    String string;
    @Arg
    Integer some;
    @Arg
    Bundle random;
    @Arg
    Bundle[] bundles;
    @Arg
    Integer[][] integers;
    @Arg
    ArrayList<SomeObject> objects;
    @Arg
    ArrayList<Integer>[][] randObjects;
    @Arg
    CharSequence charSequence;
    @Arg
    CharSequence[] charArray;
    @Arg
    String[] strings;
    @Arg
    ArrayList<String> stringArrayList;
    @Arg
    ArrayList<Integer> integerArrayList;
    @Arg
    SparseArray<Bundle> sparseArray;
    @Arg
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

    public static class TestFragment extends Fragment {
        @Arg
        int one;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }
}
