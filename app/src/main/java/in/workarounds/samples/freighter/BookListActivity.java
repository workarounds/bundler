package in.workarounds.samples.freighter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.zip.Inflater;

/**
 * Created by madki on 29/10/15.
 */
public class BookListActivity extends AppCompatActivity {
    private int[] ids = {1, 2, 3};
    private String[] books = {"To kill a mocking bird", "Hitchhiker's guide to galaxy", "Catcher in the rye"};
    private String[] authors = {"Harper Lee", "Douglas Adams", "J. D. Salinger"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        ListView listView = (ListView) findViewById(R.id.lv_books);
        listView.setAdapter(new BookAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FreighterBookDetailActivity.supply()
                        .author(authors[position])
                        .book(books[position])
                        .id(ids[position])
                        .start(BookListActivity.this);
            }
        });
    }

    private class BookAdapter extends BaseAdapter {

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

        private class ViewHolder {
            TextView title;
            TextView author;

            public ViewHolder(View view) {
                title = (TextView) view.findViewById(R.id.tv_book_title);
                author = (TextView) view.findViewById(R.id.tv_book_author);
            }

            public void bind(String book, String writer) {
                title.setText(book);
                author.setText(writer);
            }
        }
    }
}
