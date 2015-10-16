package in.workarounds.samples.autorikshaw;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import java.io.DataInputStream;
import java.util.HashMap;
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
    @Passenger
    IBinder binder;
    @Passenger
    HashMap<Integer, String> hashMap;
    @Passenger
    Intent intent;
    @Passenger
    DataInputStream stream;
    @Passenger
    int[] array;
    @Passenger
    byte[] bytes;
    Byte aByte;

    short aShort;
    Short someShort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("Integer", second);

    }
}
