package in.workarounds.samples.bundler;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import in.workarounds.bundler.annotations.Arg;
import in.workarounds.bundler.annotations.RequireBundler;
import in.workarounds.bundler.annotations.Required;
import in.workarounds.bundler.annotations.State;

/**
 * Created by madki on 18/11/15.
 */
@RequireBundler(requireAll = false)
public class BaseActivity extends AppCompatActivity {
    @Arg
    int someInt;
    @Arg @Required
    boolean someBool;

    @State
    String someState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_book_detail);

    }
}
