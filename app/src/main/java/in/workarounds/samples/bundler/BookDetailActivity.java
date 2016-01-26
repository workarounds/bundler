package in.workarounds.samples.bundler;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.workarounds.bundler.Bundler;
import in.workarounds.bundler.annotations.Arg;
import in.workarounds.bundler.annotations.RequireBundler;
import in.workarounds.bundler.annotations.Required;
import in.workarounds.bundler.annotations.State;

/**
 * Created by madki on 29/10/15.
 */
@RequireBundler
public class BookDetailActivity extends AppCompatActivity {
    private static final String TAG = "BookDetailActivity";
    @Arg
    Book book;
    @Arg(serializer = ParcelSerializer.class)
    Author author;
    @Arg
    @BookType
    int bookType;
    @Arg
    @Required(false)
    @State
    int rating;

    @Bind(R.id.tv_book_name)
    TextView tvBookName;
    @Bind(R.id.tv_book_writer)
    TextView tvBookWriter;
    @Bind(R.id.tv_type)
    TextView tvType;
    @Bind(R.id.tv_rating)
    TextView tvRating;
    @Bind(R.id.et_rating)
    EditText etRating;

    @OnClick(R.id.btn_rate)
    void rate() {
        String ratingStr = etRating.getText().toString();
        if(!TextUtils.isEmpty(ratingStr)) {
            tvRating.setText(ratingStr);
            rating = Integer.parseInt(ratingStr);
        }
    }

    @OnClick(R.id.btn_toast)
    void toast() {
        Bundler.toastService(book, author).rating(rating).start(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_book_detail);

        ButterKnife.bind(this);

        Bundler.inject(this);
        Bundler.restoreState(this, savedInstanceState);

        tvBookName.setText(book.name());
        tvBookWriter.setText(author.name);
        tvRating.setText((rating > 0) ? Integer.toString(rating) : "unrated");
        tvType.setText((bookType == Book.FICTION) ? "Fiction" : "Non fiction");
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

}
