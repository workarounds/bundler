package in.workarounds.samples.bundler;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.workarounds.bundler.Bundler;

/**
 * Created by madki on 29/10/15.
 */
public class BookListActivity extends AppCompatActivity {
    private String[] books = {
            "To kill a mocking bird",
            "Hitchhiker's guide to galaxy",
            "Catcher in the rye",
            "Zero to one",
            "A brief history of time"
    };
    private String[] authors = {
            "Harper Lee",
            "Douglas Adams",
            "J. D. Salinger",
            "Peter Theil",
            "Stephen Hawkings"
    };

    @Bind(R.id.lv_books) ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        ButterKnife.bind(this);

        listView.setAdapter(new BookAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int type = (position > 2) ? Book.NON_FICTION : Book.FICTION;
                Bundler.bookDetailActivity(
                        new AutoParcel_Book(books[position]),
                        new Author(authors[position]),
                        type
                ).start(BookListActivity.this);
            }
        });
    }

    class BookAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return books.length;
        }

        @Override
        public String getItem(int position) {
            return books[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_book, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.bind(books[position], authors[position]);
            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.tv_book_title) TextView title;
            @Bind(R.id.tv_book_author) TextView author;

            public ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bind(String book, String writer) {
                title.setText(book);
                author.setText(writer);
            }
        }
    }
}
