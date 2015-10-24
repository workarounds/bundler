package in.workarounds.samples.freighter;

import android.content.Intent;

/**
 * Created by madki on 17/10/15.
 */
public class MainActivityInjector {

    public static void inject(MainActivity activity) {
        from(activity.getIntent()).into(activity);
    }

    public static IntentParser from(Intent intent) {
        return new IntentParser(intent);
    }

    public static class IntentParser {
        private Intent intent;

        public IntentParser(Intent intent) {
            this.intent = intent;
        }

        public boolean hasFirst() {
            return true;
//            return intent.hasExtra(IntentKeysMainActivity.INTENT_KEY_FIRST_INT);
        }

        public int first(int defaultValue) {
            if(hasFirst()) {
                return 1;
//                return intent.getIntExtra(IntentKeysMainActivity.INTENT_KEY_FIRST_INT, defaultValue);
            }
            return defaultValue;
        }

        public void into(MainActivity activity) {
//            activity.first = first(activity.first);
        }
    }
}
