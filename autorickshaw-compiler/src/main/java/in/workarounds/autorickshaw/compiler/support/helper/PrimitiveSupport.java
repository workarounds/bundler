package in.workarounds.autorickshaw.compiler.support.helper;

/**
 * Created by madki on 20/10/15.
 */
public class PrimitiveSupport {
    private String intentKeyType;
    private String bundlePutMethodName;
    private String bundlePutArrayMethodName;
    private String bundleGetMethodName;
    private String bundleGetArrayMethodName;
    private Class<?> type;
    private Class<?> nullableType;

    public PrimitiveSupport(String intentKeyType,
                            String bundlePutMethodName,
                            String bundlePutArrayMethodName,
                            String bundleGetMethodName,
                            String bundleGetArrayMethodName,
                            Class<?> type,
                            Class<?> nullableType) {
        this.intentKeyType = intentKeyType;
        this.bundlePutMethodName = bundlePutMethodName;
        this.bundlePutArrayMethodName = bundlePutArrayMethodName;
        this.bundleGetMethodName = bundleGetMethodName;
        this.bundleGetArrayMethodName = bundleGetArrayMethodName;
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
        private String bundleGetMethodName;
        private String bundleGetArrayMethodName;
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

        public Builder bundleGetMethodName(String bundleGetMethodName) {
            this.bundleGetMethodName = bundleGetMethodName;
            return this;
        }

        public Builder bundleGetArrayMethodName(String bundleGetArrayMethodName) {
            this.bundleGetArrayMethodName = bundleGetArrayMethodName;
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
            if (this.bundlePutArrayMethodName == null) {
                this.bundlePutArrayMethodName = this.bundlePutMethodName + "Array";
            }
            if (this.bundleGetArrayMethodName == null) {
                this.bundleGetArrayMethodName = this.bundleGetMethodName + "Array";
            }
            return new PrimitiveSupport(intentKeyType,
                    bundlePutMethodName,
                    bundlePutArrayMethodName,
                    bundleGetMethodName,
                    bundleGetArrayMethodName,
                    type,
                    nullableType);
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

    public String getBundleGetMethodName() {
        return bundleGetMethodName;
    }

    public String getBundleGetArrayMethodName() {
        return bundleGetArrayMethodName;
    }

    public Class<?> getNullableType() {
        return nullableType;
    }
}
