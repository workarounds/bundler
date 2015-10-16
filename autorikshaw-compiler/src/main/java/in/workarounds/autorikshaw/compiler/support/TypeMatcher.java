package in.workarounds.autorikshaw.compiler.support;

import in.workarounds.autorikshaw.compiler.model.type.BasicType;
import in.workarounds.autorikshaw.compiler.model.type.RootType;

/**
 * Created by madki on 16/10/15.
 */
public class TypeMatcher {

    public static boolean isSupported(RootType rootType) {
        if(rootType == null) {
            return false;
        }

        if(rootType instanceof BasicType) {
            return true;
        } else {
            return false;
        }
    }
}
