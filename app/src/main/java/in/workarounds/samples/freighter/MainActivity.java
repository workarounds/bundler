package in.workarounds.samples.freighter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

import in.workarounds.freighter.annotations.Cargo;
import in.workarounds.freighter.annotations.Freighter;

/**
 * Created by madki on 16/10/15.
 */
@Freighter
public class MainActivity extends AppCompatActivity {
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
        Bundle dummy = new Bundle();
        dummy.putInt("i", 1);

        Bundle bundle = new Bundle();
        arrayList = new ArrayList<>();
        arrayList.add(dummy);
        arrayList.add(dummy);
        arrayList.add(dummy);
        bundle.putParcelableArrayList("k", arrayList);

        ArrayList<Bundle> bundles = bundle.getParcelableArrayList("k");
        for (Bundle b: bundles) {
            Log.d("MA", "i: " + b.getInt("i"));
        }
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
}
