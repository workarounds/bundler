package in.workarounds.autorikshaw.compiler.model.type;

/**
 * Created by madki on 17/10/15.
 * Base class for all custom types.
 */
public class RootType {
    /**
     * boolean that decides if the annotated element is
     * an array
     */
    private final boolean isArray;

    public RootType(boolean isArray) {
        this.isArray = isArray;
    }

    public RootType(RootType rootType) {
        if(rootType != null) {
            this.isArray = rootType.isArray();
        } else {
            isArray = false;
        }
    }

    public boolean isArray() {
        return isArray;
    }
}

