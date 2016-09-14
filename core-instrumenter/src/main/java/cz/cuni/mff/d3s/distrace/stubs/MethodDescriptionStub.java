package cz.cuni.mff.d3s.distrace.stubs;

import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.annotation.AnnotationValue;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Stub for MethodDescription
 */
class MethodDescriptionStub extends MethodDescription.AbstractBase implements Serializable {
    // <original type description, holder>
    private static HashMap<MethodDescription, MethodDescription> descriptions = new HashMap<>();
    private TypeDescription.Generic returnType;
    private ParameterList<?> parameters;
    private TypeList.Generic exceptionTypes;
    private AnnotationValue<?, ?> defaultValue;
    private TypeDescription.Generic receiverType;
    private InDefinedShape defined;
    private TypeDefinition declaringType;
    private int modifiers;
    private String internalName;
    private TypeList.Generic typeVariables;
    private AnnotationList declaredAnnotations;

    static MethodDescription from(MethodDescription description) {
        if (!descriptions.containsKey(description)) {
            MethodDescriptionStub stub = new MethodDescriptionStub();
            descriptions.put(description, stub);
            MethodDescriptionStub.from(description, stub);
        }
        return descriptions.get(description);
    }

    private static MethodDescription from(MethodDescription methodDescription, MethodDescriptionStub stub) {
        if (methodDescription == null) {
            return null;
        }
        stub.declaredAnnotations = methodDescription.getDeclaredAnnotations();
        stub.typeVariables = methodDescription.getTypeVariables();
        stub.internalName = methodDescription.getInternalName();
        stub.modifiers = methodDescription.getModifiers();
        stub.declaringType = methodDescription.getDeclaringType();
        stub.defined = methodDescription.asDefined();
        stub.receiverType = methodDescription.getReceiverType();
        stub.defaultValue = methodDescription.getDefaultValue();
        stub.exceptionTypes = methodDescription.getExceptionTypes();
        stub.parameters = methodDescription.getParameters();
        stub.returnType = TypeDescriptionGenericStub.from(methodDescription.getReturnType());
        return stub;
    }


    @Override
    public TypeDescription.Generic getReturnType() {
        return returnType;
    }

    @Override
    public ParameterList<?> getParameters() {
        return parameters;
    }

    @Override
    public TypeList.Generic getExceptionTypes() {
        return exceptionTypes;
    }

    @Override
    public AnnotationValue<?, ?> getDefaultValue() {
        return defaultValue;
    }

    @Override
    public TypeDescription.Generic getReceiverType() {
        return receiverType;
    }

    @Override
    public InDefinedShape asDefined() {
        return defined;
    }

    @Override
    public int getModifiers() {
        return modifiers;
    }

    @Override
    public String getInternalName() {
        return internalName;
    }

    @Override
    public TypeList.Generic getTypeVariables() {
        return typeVariables;
    }

    @Override
    public AnnotationList getDeclaredAnnotations() {
        return declaredAnnotations;
    }

    @Override
    public TypeDefinition getDeclaringType() {
        return declaringType;
    }


}
