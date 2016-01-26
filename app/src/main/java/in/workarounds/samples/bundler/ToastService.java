package in.workarounds.samples.bundler;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import in.workarounds.bundler.Bundler;
import in.workarounds.bundler.annotations.Arg;
import in.workarounds.bundler.annotations.RequireBundler;
import in.workarounds.bundler.annotations.Required;
import in.workarounds.bundler.parceler.ParcelerSerializer;

/**
 * A demo service that simply toasts the given book, author, rating
 */
@RequireBundler
public class ToastService extends Service {
    @Arg
    Book book;
    @Arg(serializer = ParcelerSerializer.class)
    Author author;
    @Arg @Required(false)
    int rating = -1;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // injecting fields from intent
        resetFields();
        Bundler.inject(this, intent);

        // intent might be null at times, show toast only if book and author are non null
        if(book != null && author !=null) {
            if(rating < 1) {
                Toast.makeText(
                        this,
                        book.name() + " written by " + author.name + ", unrated",
                        Toast.LENGTH_LONG
                ).show();
            } else {
                 Toast.makeText(
                        this,
                        book.name() + " written by " + author.name + ", rated " + rating,
                        Toast.LENGTH_LONG
                ).show();
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * resets the global fields. If the intent is null or the extras corresponding to the fields are
     * not passed in the intent Bundler simply leaves the field values as is. If the values
     * of the fields aren't reset then the value from previous intent may still remain. So it's a
     * good idea to reset them.
     */
    private void resetFields() {
        book = null;
        author = null;
        rating = -1;
    }
}
