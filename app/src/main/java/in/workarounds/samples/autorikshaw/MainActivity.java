package in.workarounds.samples.autorikshaw;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import in.workarounds.autorikshaw.annotations.Destination;
import in.workarounds.autorikshaw.annotations.Passenger;

/**
 * Created by madki on 16/10/15.
 */
@Destination
public class MainActivity extends AppCompatActivity {
    @Passenger
    int first;
    @Passenger
    Integer second;
    @Passenger
    String third;
    @Passenger
    boolean fourth;
    @Passenger
    Bundle fifth;
    @Passenger
    String[] sixth;
    @Passenger
    List<String> seventh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
