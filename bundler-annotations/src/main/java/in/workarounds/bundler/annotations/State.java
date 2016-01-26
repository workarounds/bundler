package in.workarounds.bundler.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Created by madki on 30/10/15.
 */
@Target(FIELD) @Retention(CLASS)
public @interface State {
    Class<?> serializer() default DefaultSerializer.class;
    String key() default "";
}
