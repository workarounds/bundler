package in.workarounds.samples.freighter.test;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import in.workarounds.freighter.annotations.Cargo;
import in.workarounds.freighter.annotations.Freighter;

/**
 * Created by madki on 24/10/15.
 */
@Freighter
public class TestService extends Service {
    @Cargo
    int one;
    @Cargo
    String two;
    @Cargo
    Bundle three;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
