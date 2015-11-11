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

import in.workarounds.bundler.annotations.BundlerArg;
import in.workarounds.bundler.annotations.RequireBundler;
import in.workarounds.samples.bundler.R;

/**
 * Created by madki on 16/10/15.
 */
@RequireBundler
public class TestActivity extends AppCompatActivity {
    @BundlerArg
    int integer;
    @BundlerArg
    int[] integerArray;
    @BundlerArg
    boolean bool;
    @BundlerArg
    boolean[] boolArray;
    @BundlerArg
    char character;
    @BundlerArg
    char[] characterArray;
    @BundlerArg
    long longNumber;
    @BundlerArg
    long[] longArray;
    @BundlerArg
    double doubleNumber;
    @BundlerArg
    double[] doubleArray;
    @BundlerArg
    byte aByte;
    @BundlerArg
    byte[] byteArray;
    @BundlerArg
    float floatNumber;
    @BundlerArg
    float[] floatArray;
    @BundlerArg
    short shortNumber;
    @BundlerArg
    short[] shortArray;
    @BundlerArg
    String string;
    @BundlerArg
    Integer some;
    @BundlerArg
    Bundle random;
    @BundlerArg
    Bundle[] bundles;
    @BundlerArg
    Integer[][] integers;
    @BundlerArg
    ArrayList<SomeObject> objects;
    @BundlerArg
    ArrayList<Integer>[][] randObjects;
    @BundlerArg
    CharSequence charSequence;
    @BundlerArg
    CharSequence[] charArray;
    @BundlerArg
    String[] strings;
    @BundlerArg
    ArrayList<String> stringArrayList;
    @BundlerArg
    ArrayList<Integer> integerArrayList;
    @BundlerArg
    SparseArray<Bundle> sparseArray;
    @BundlerArg
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
        @BundlerArg
        int one;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }
}
