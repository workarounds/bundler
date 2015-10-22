package in.workarounds.samples.autorickshaw;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import in.workarounds.autorickshaw.annotations.Cargo;
import in.workarounds.autorickshaw.annotations.Destination;

/**
 * Created by madki on 16/10/15.
 */
@Destination
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle bundle = new Bundle();
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
