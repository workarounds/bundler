package in.workarounds.samples.bundler;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Support annotation for book type
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({Book.FICTION, Book.NON_FICTION})
public @interface BookType {
}
