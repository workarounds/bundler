package in.workarounds.samples.bundler.test;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import in.workarounds.bundler.annotations.BundlerArg;
import in.workarounds.bundler.annotations.RequireBundler;

/**
 * Created by madki on 24/10/15.
 */
@RequireBundler
public class TestService extends Service {
    @BundlerArg
    int one;
    @BundlerArg
    String two;
    @BundlerArg
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
