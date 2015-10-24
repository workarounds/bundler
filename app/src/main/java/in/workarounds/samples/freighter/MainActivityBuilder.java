package in.workarounds.samples.freighter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by madki on 17/10/15.
 */
public class MainActivityBuilder {

    public static IntentBuilder from(Context context) {
        return new IntentBuilder(context);
    }


    public static class IntentBuilder {
        private Context context;
        private Integer first;

        private IntentBuilder(Context context) {
            this.context = context;
        }

        public IntentBuilder first(int first) {
            this.first = first;
            return this;
        }

        public Intent intent() {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtras(bundle());
            return intent;
        }

        public void start() {
            context.startActivity(intent());
        }

        public Bundle bundle() {
            Bundle bundle = new Bundle();
            if(first != null) {
//                bundle.putInt(IntentKeysMainActivity.INTENT_KEY_FIRST_INT, first);
            }
            return bundle;
        }

    }

}
