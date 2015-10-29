package in.workarounds.samples.freighter.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.workarounds.freighter.annotations.Cargo;
import in.workarounds.freighter.annotations.Freighter;

/**
 * Created by madki on 25/10/15.
 */
@Freighter
public class TestFragment extends Fragment {
    @Cargo
    int one;
    @Cargo
    String two;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
