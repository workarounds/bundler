package in.workarounds.samples.bundler.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import in.workarounds.bundler.annotations.Arg;
import in.workarounds.bundler.annotations.RequireBundler;

/**
 * Created by madki on 12/11/15.
 */
@RequireBundler
public class BookDetailActivity extends AppCompatActivity {
    @Arg int one;
    @Arg String two;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
