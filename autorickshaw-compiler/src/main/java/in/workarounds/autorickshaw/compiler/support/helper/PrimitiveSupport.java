package in.workarounds.autorickshaw.compiler.support.helper;

/**
 * Created by madki on 20/10/15.
 */
public class PrimitiveSupport {
    private String intentKeyType;
    private String bundlePutMethodName;
    private String bundlePutArrayMethodName;
    private Class<?> type;
    private Class<?> nullableType;

    public PrimitiveSupport(String intentKeyType,
                            String bundlePutMethodName,
                            String bundlePutArrayMethodName,
                            Class<?> type,
                            Class<?> nullableType) {
        this.intentKeyType = intentKeyType;
        this.bundlePutMethodName = bundlePutMethodName;
        this.bundlePutArrayMethodName = bundlePutArrayMethodName;
        this.type = type;
        this.nullableType = nullableType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String intentKeyType;
        private String bundlePutMethodName;
        private String bundlePutArrayMethodName;
        private Class<?> type;
        private Class<?> nullableType;

        public Builder intentKeyType(String intentKeyType) {
            this.intentKeyType = intentKeyType;
            return this;
        }

        public Builder bundlePutMethodName(String bundlePutMethodName) {
            this.bundlePutMethodName = bundlePutMethodName;
            return this;
        }

        public Builder bundlePutArrayMethodName(String bundlePutArrayMethodName) {
            this.bundlePutArrayMethodName = bundlePutArrayMethodName;
            return this;
        }

        public Builder type(Class<?> type) {
            this.type = type;
            return this;
        }

        public Builder nullableType(Class<?> nullableType) {
            this.nullableType = nullableType;
            return this;
        }

        public PrimitiveSupport build() {
            if(this.bundlePutArrayMethodName == null) {
                this.bundlePutArrayMethodName = this.bundlePutMethodName + "Array";
            }
            return new PrimitiveSupport(intentKeyType, bundlePutMethodName, bundlePutArrayMethodName, type, nullableType);
        }

    }

    public String getIntentKeyType() {
        return intentKeyType;
    }

    public String getBundlePutMethodName() {
        return bundlePutMethodName;
    }

    public String getBundlePutArrayMethodName() {
        return bundlePutArrayMethodName;
    }

    public Class<?> getType() {
        return type;
    }

    public Class<?> getNullableType() {
        return nullableType;
    }
}
