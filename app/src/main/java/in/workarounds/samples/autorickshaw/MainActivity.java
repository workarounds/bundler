package in.workarounds.samples.autorickshaw;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import in.workarounds.autorickshaw.annotations.Destination;
import in.workarounds.autorickshaw.annotations.Passenger;

/**
 * Created by madki on 16/10/15.
 */
@Destination
public class MainActivity extends AppCompatActivity {
    @Passenger
    int first;
    @Passenger
    int[] array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
