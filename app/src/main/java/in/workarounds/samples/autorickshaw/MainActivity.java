package in.workarounds.samples.autorickshaw;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import in.workarounds.autorickshaw.annotations.Destination;
import in.workarounds.autorickshaw.annotations.Passenger;

/**
 * Created by madki on 16/10/15.
 */
@Destination
public class MainActivity extends AppCompatActivity {
    @Passenger
    int integer;
    @Passenger
    int[] integerArray;
    @Passenger
    boolean bool;
    @Passenger
    boolean[] boolArray;
    @Passenger
    char character;
    @Passenger
    char[] characterArray;
    @Passenger
    long longNumber;
    @Passenger
    long[] longArray;
    @Passenger
    double doubleNumber;
    @Passenger
    double[] doubleArray;
    @Passenger
    byte aByte;
    @Passenger
    byte[] byteArray;
    @Passenger
    float floatNumber;
    @Passenger
    float[] floatArray;
    @Passenger
    short shortNumber;
    @Passenger
    short[] shortArray;
    @Passenger
    ArrayList<String>[] strArray= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle bundle = new Bundle();

        bundle.putSerializable("key", strArray);
    }

    private static class SomeObject {
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
