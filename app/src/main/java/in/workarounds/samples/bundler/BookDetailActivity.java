package in.workarounds.samples.bundler;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import in.workarounds.bundler.annotations.BundlerArg;
import in.workarounds.bundler.annotations.RequireBundler;
import in.workarounds.bundler.annotations.InstanceState;

/**
 * Created by madki on 29/10/15.
 */
@RequireBundler
public class BookDetailActivity extends AppCompatActivity {
    private static final String TAG = "BookDetailActivity";
    public static final int BOOK_TYPE_FICTION = 1;
    public static final int BOOK_TYPE_NON_FICTION = 2;


    @BundlerArg
    @InstanceState
    int id;
    @NonNull
    @BundlerArg
    @InstanceState
    String book;
    @Nullable
    @BundlerArg
    @InstanceState
    String author;
    @BundlerArg
    @InstanceState
    @BookType
    int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_book_detail);
        Intent intent = null;

        TextView bookName = (TextView) findViewById(R.id.tv_book_name);
        bookName.setText(book);

        TextView writer = (TextView) findViewById(R.id.tv_book_writer);
        writer.setText(author);


        Log.d(TAG, "book id = " + id);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @IntDef({BOOK_TYPE_FICTION, BOOK_TYPE_NON_FICTION})
    @Retention(RetentionPolicy.SOURCE)
    @interface BookType {
    }
}
