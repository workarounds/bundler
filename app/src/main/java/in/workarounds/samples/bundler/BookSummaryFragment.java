package in.workarounds.samples.bundler;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.workarounds.bundler.Bundler;
import in.workarounds.bundler.annotations.Arg;
import in.workarounds.bundler.annotations.RequireBundler;
import in.workarounds.bundler.annotations.State;
import in.workarounds.bundler.parceler.ParcelerSerializer;

/**
 * A demo fragment that simple contains text view showing summary of the book details
 */
@RequireBundler
public class BookSummaryFragment extends Fragment {
    @Arg
    Book book;
    @Arg(serializer = ParcelerSerializer.class)
    Author author;
    @Arg @BookType
    int bookType;
    @Arg @State
    int rating;

    @Bind(R.id.tv_book_summary)
    TextView tvBookSummary;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // these methods can be called in onCreate or onCreateView or onViewCreated
        Bundler.inject(this);
        Bundler.restoreState(this, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_book_summary, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
        displaySummary();
    }

    public void updateRating(int rating) {
        this.rating = rating;
        displaySummary();
    }

    private void displaySummary() {
        String summary = book.name() + " is a "
                + ((bookType == Book.FICTION) ? "fiction" : "non fiction")
                + " written by " + author.name
                + ((rating > 0) ? " and rated " + Integer.toString(rating) : "" );
        tvBookSummary.setText(summary);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Bundler.saveState(this, outState);
    }
}
