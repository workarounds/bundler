package in.workarounds.autorikshaw.compiler.support;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.UnionType;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.SimpleTypeVisitor7;

import in.workarounds.autorikshaw.compiler.RikshawProcessor;

/**
 * Created by madki on 16/10/15.
 */
public class CustomTypeVisitor extends SimpleTypeVisitor7<SupportedType, RikshawProcessor> {
    @Override
    public SupportedType visitArray(ArrayType t, RikshawProcessor rikshawProcessor) {
        t.getComponentType().accept(new CustomTypeVisitor(), rikshawProcessor);
        rikshawProcessor.message(null, "visited array type");
        return SupportedType.NONE;
    }

    @Override
    public SupportedType visitDeclared(DeclaredType t, RikshawProcessor rikshawProcessor) {
        if(t.getTypeArguments().size() == 0) {
            rikshawProcessor.message(null, "visited %s", ((TypeElement) t.asElement()).getQualifiedName());
        } else if(t.getTypeArguments().size() == 1) {
            t.getTypeArguments().get(0).accept(new CustomTypeVisitor(), rikshawProcessor);
        }
        rikshawProcessor.message(null, "visited declared type");
        return SupportedType.NONE;
    }

    @Override
    public SupportedType visitError(ErrorType t, RikshawProcessor rikshawProcessor) {
        rikshawProcessor.message(null, "visited error type");
        return SupportedType.NONE;
    }

    @Override
    public SupportedType visitExecutable(ExecutableType t, RikshawProcessor rikshawProcessor) {
        rikshawProcessor.message(null, "visited executable type");
        return SupportedType.NONE;
    }

    @Override
    public SupportedType visitNoType(NoType t, RikshawProcessor rikshawProcessor) {
        rikshawProcessor.message(null, "visited no type");
        return SupportedType.NONE;
    }

    @Override
    public SupportedType visitNull(NullType t, RikshawProcessor rikshawProcessor) {
        rikshawProcessor.message(null, "visited null type");
        return SupportedType.NONE;
    }

    @Override
    public SupportedType visitPrimitive(PrimitiveType t, RikshawProcessor rikshawProcessor) {
        rikshawProcessor.message(null, "visited primitive type");
        return SupportedType.NONE;
    }

    @Override
    public SupportedType visitTypeVariable(TypeVariable t, RikshawProcessor rikshawProcessor) {
        rikshawProcessor.message(null, "visited type variable");
        return SupportedType.NONE;
    }

    @Override
    public SupportedType visitUnion(UnionType t, RikshawProcessor rikshawProcessor) {
        rikshawProcessor.message(null, "visited union type");
        return SupportedType.NONE;
    }

    @Override
    public SupportedType visitUnknown(TypeMirror t, RikshawProcessor rikshawProcessor) {
        rikshawProcessor.message(null, "visited unknown type");
        return SupportedType.NONE;
    }

    @Override
    public SupportedType visitWildcard(WildcardType t, RikshawProcessor rikshawProcessor) {
        rikshawProcessor.message(null, "visited wildcard type");
        return SupportedType.NONE;
    }
}
