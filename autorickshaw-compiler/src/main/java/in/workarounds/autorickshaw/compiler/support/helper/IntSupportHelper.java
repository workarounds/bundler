package in.workarounds.autorickshaw.compiler.support.helper;

/**
 * Created by madki on 16/10/15.
 */
public class IntSupportHelper extends SupportHelper {
    private static IntSupportHelper helper;

    public static IntSupportHelper instance() {
        if(helper == null) {
            helper = new IntSupportHelper();
        }
        return helper;
    }


}
