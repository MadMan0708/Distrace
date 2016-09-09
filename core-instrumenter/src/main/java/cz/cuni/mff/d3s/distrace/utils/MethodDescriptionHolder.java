package cz.cuni.mff.d3s.distrace.utils;

import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.annotation.AnnotationValue;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;

import java.io.Serializable;

/**
 * Created by kuba on 08/09/16.
 */
public class MethodDescriptionHolder extends MethodDescription.AbstractBase implements Serializable {
    private TypeDescription.Generic returnType;
    private ParameterList<?> parameters;
    private TypeList.Generic exceptionTypes;
    private AnnotationValue<?,?> defaultValue;
    private TypeDescription.Generic receiverType;
    private InDefinedShape defined;
    private TypeDefinition declaringType;
    private int modifiers;
    private String internalName;
    private TypeList.Generic typeVariables;
    private AnnotationList declaredAnnotations;

    void setReturnType(TypeDescription.Generic returnType) {
        this.returnType = returnType;
    }

    void setParameters(ParameterList<?> parameters) {
        this.parameters = parameters;
    }

    void setExceptionTypes(TypeList.Generic exceptionTypes) {
        this.exceptionTypes = exceptionTypes;
    }

    void setDefaultValue(AnnotationValue<?, ?> defaultValue) {
        this.defaultValue = defaultValue;
    }

    void setReceiverType(TypeDescription.Generic receiverType) {
        this.receiverType = receiverType;
    }

    void setDefined(InDefinedShape defined) {
        this.defined = defined;
    }

    void setDeclaringType(TypeDefinition declaringType) {
        this.declaringType = declaringType;
    }

    void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }

    void setInternalName(String internalName) {
        this.internalName = internalName;
    }

    void setTypeVariables(TypeList.Generic typeVariables) {
        this.typeVariables = typeVariables;
    }

    void setDeclaredAnnotations(AnnotationList declaredAnnotations) {
        this.declaredAnnotations = declaredAnnotations;
    }

    public static MethodDescription create(MethodDescription methodDescription){
        if(methodDescription == null){
            return null;
        }
        MethodDescriptionHolder holder = new MethodDescriptionHolder();
        holder.setDeclaredAnnotations(methodDescription.getDeclaredAnnotations());
        holder.setTypeVariables(methodDescription.getTypeVariables());
        holder.setInternalName(methodDescription.getInternalName());
        holder.setModifiers(methodDescription.getModifiers());

        holder.setDeclaringType(methodDescription.getDeclaringType());

        holder.setDefined(methodDescription.asDefined());
        holder.setReceiverType(methodDescription.getReceiverType());
        holder.setDefaultValue(methodDescription.getDefaultValue());
        holder.setExceptionTypes(methodDescription.getExceptionTypes());
        holder.setParameters(methodDescription.getParameters());
        holder.setReturnType(TypeDescriptionGeneric.create(methodDescription.getReturnType()));
        return holder;
    }


    @Override
    public TypeDescription.Generic getReturnType() {
        return returnType;
    }

    @Override
    public ParameterList<?> getParameters() {
        return null;
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
