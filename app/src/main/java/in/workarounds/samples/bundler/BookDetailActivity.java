package in.workarounds.samples.bundler;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import in.workarounds.Bundler;
import in.workarounds.bundler.ParcelListSerializer;
import in.workarounds.bundler.annotations.Arg;
import in.workarounds.bundler.annotations.RequireBundler;
import in.workarounds.bundler.annotations.Required;
import in.workarounds.bundler.annotations.State;

/**
 * Created by madki on 29/10/15.
 */
@RequireBundler(inheritArgs = false, requireAll = false)
public class BookDetailActivity extends BaseActivity {
    private static final String TAG = "BookDetailActivity";
    public static final int BOOK_TYPE_FICTION = 1;
    public static final int BOOK_TYPE_NON_FICTION = 2;
    private static final String FIRST_WAY = "firstWay";
    private static final String SECOND_WAY = "secondWay";

    @Arg
    @State
    int id;
    @NonNull
    @Arg
    @State
    String book;
    @Arg
    @State
    String author;
    @Arg
    @State
    @BookType
    int type;
    @Arg
    @Required(false)
    @State
    int someInt;
    @Arg(serializer = ParcelListSerializer.class)
    @State(serializer = ParcelListSerializer.class)
    List<Bundle> bundles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundler.inject(this);
        Bundler.restoreState(this, savedInstanceState);

        TextView bookName = (TextView) findViewById(R.id.tv_book_name);
        bookName.setText(book);

        TextView writer = (TextView) findViewById(R.id.tv_book_writer);
        writer.setText(author);

        for (Bundle b : bundles) {
            Log.d(TAG, b.getString("key"));
        }

        Bundle b3 = new Bundle();
        b3.putString("key", "!!!");
        bundles.add(b3);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Bundler.restoreState(this, savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundler.saveState(this, outState);
    }

    @IntDef({BOOK_TYPE_FICTION, BOOK_TYPE_NON_FICTION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BookType {
    }
}
