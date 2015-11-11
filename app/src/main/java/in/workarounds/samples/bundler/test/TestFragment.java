package in.workarounds.samples.bundler.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.workarounds.bundler.annotations.BundlerArg;
import in.workarounds.bundler.annotations.RequireBundler;

/**
 * Created by madki on 25/10/15.
 */
@RequireBundler
public class TestFragment extends Fragment {
    @BundlerArg
    int one;
    @BundlerArg
    String two;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
