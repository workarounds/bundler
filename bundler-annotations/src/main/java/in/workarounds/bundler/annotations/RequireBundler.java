package in.workarounds.bundler.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Created by madki on 16/10/15.
 */
@Retention(CLASS) @Target(TYPE)
public @interface RequireBundler {
    boolean requireAll() default true;
    String bundlerMethod() default "";
}
