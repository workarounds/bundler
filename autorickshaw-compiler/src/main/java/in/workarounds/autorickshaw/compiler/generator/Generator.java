package in.workarounds.autorickshaw.compiler.generator;

import com.squareup.javapoet.TypeSpec;

/**
 * Created by madki on 17/10/15.
 */
public interface Generator {
    TypeSpec brewBuilder();
    TypeSpec brewParser();
    TypeSpec brewConstants();
}
