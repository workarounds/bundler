package in.workarounds.samples.freighter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import in.workarounds.freighter.annotations.Cargo;
import in.workarounds.freighter.annotations.Freighter;

/**
 * Created by madki on 29/10/15.
 */
@Freighter
public class BookDetailActivity extends AppCompatActivity {
    private static final String TAG = "BookDetailActivity";
    @Cargo int id;
    @Cargo String book;
    @Cargo String author;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        FreighterBookDetailActivity.inject(this);

        TextView bookName = (TextView) findViewById(R.id.tv_book_name);
        bookName.setText(book);

        TextView writer = (TextView) findViewById(R.id.tv_book_writer);
        writer.setText(author);

        Log.d(TAG, "book id = " + id);
    }
}
