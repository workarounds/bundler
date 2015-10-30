package in.workarounds.samples.freighter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import in.workarounds.freighter.annotations.Cargo;
import in.workarounds.freighter.annotations.Freighter;
import in.workarounds.freighter.annotations.InstanceState;

/**
 * Created by madki on 29/10/15.
 */
@Freighter
public class BookDetailActivity extends AppCompatActivity {
    private static final String TAG = "BookDetailActivity";
    @Cargo
    @InstanceState
    int id;
    @Cargo
    @InstanceState
    String book;
    @Cargo
    @InstanceState
    String author;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FreighterBookDetailActivity.restoreState(this, savedInstanceState);

        setContentView(R.layout.activity_book_detail);
        FreighterBookDetailActivity.inject(this);

        TextView bookName = (TextView) findViewById(R.id.tv_book_name);
        bookName.setText(book);

        TextView writer = (TextView) findViewById(R.id.tv_book_writer);
        writer.setText(author);

        Log.d(TAG, "book id = " + id);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        FreighterBookDetailActivity.restoreState(this, savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        FreighterBookDetailActivity.saveState(this, outState);
        super.onSaveInstanceState(outState);
    }
}
