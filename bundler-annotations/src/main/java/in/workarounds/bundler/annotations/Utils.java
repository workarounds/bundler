package in.workarounds.bundler.annotations;

import android.util.Log;

/**
 * Utility classes to be used in generated code
 */
public class Utils {

    /**
     * No instances
     */
    private Utils() {
        throw new IllegalArgumentException("No instances");
    }

    @SuppressWarnings("unchecked")
    public static<V> V silentCast(String field, Object value, String className, V defaultValue, String TAG) {
        try {
            return (V) value;
        } catch (ClassCastException e) {
            typeWarning(field, value, className, defaultValue, e, TAG);
            return defaultValue;
        }
    }

    // Log a message if the value was non-null but not of the expected type
    public static void typeWarning(String label, Object value, String className,
                Object defaultValue, ClassCastException e, String TAG) {
        StringBuilder sb = new StringBuilder();
        sb.append("Field ");
        sb.append(label);
        sb.append(" expected ");
        sb.append(className);
        sb.append(" but value was a ");
        sb.append(value.getClass().getName());
        sb.append(".  The default value ");
        sb.append(defaultValue);
        sb.append(" was returned.");
        Log.w(TAG, sb.toString());
        Log.w(TAG, "Attempt to cast generated internal exception:", e);
    }
}
