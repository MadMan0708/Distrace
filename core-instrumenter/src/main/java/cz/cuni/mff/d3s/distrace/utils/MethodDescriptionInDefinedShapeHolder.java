package cz.cuni.mff.d3s.distrace.utils;

import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.annotation.AnnotationValue;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kuba on 08/09/16.
 */
public class MethodDescriptionInDefinedShapeHolder extends MethodDescription.InDefinedShape.AbstractBase implements Serializable {
    private TypeDescription.Generic returnType;
    private ParameterList<ParameterDescription.InDefinedShape> parameters;
    private TypeList.Generic exceptionTypes;
    private AnnotationValue<?,?> defaultValue;
    private TypeDescription.Generic receiverType;
    private InDefinedShape defined;
    private TypeDescription declaringType;
    private int modifiers;
    private String internalName;
    private TypeList.Generic typeVariables;
    private AnnotationList declaredAnnotations;

    void setReturnType(TypeDescription.Generic returnType) {
        this.returnType = returnType;
    }

    void setParameters(ParameterList<ParameterDescription.InDefinedShape> parameters) {
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

    void setDeclaringType(TypeDescription declaringType) {
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

    public static MethodList<MethodDescription.InDefinedShape> convert(MethodList<InDefinedShape> methods){

        ArrayList<MethodDescription.InDefinedShape> methodList = new ArrayList<>();
        for(MethodDescription.InDefinedShape s: methods){
            System.out.println("Converting method "+ s);
            methodList.add(MethodDescriptionInDefinedShapeHolder.getOrCreate(s));
        }
       return new MethodList.Explicit<>(methodList);
    }

    private static HashMap<MethodDescription.InDefinedShape,MethodDescription.InDefinedShape> cache = new HashMap<>();

    public static MethodDescription.InDefinedShape getOrCreate(MethodDescription.InDefinedShape methodDescription){
        if(!cache.containsKey(methodDescription)){
            MethodDescriptionInDefinedShapeHolder holder = new MethodDescriptionInDefinedShapeHolder();
            cache.put(methodDescription, holder);
            MethodDescriptionInDefinedShapeHolder.create(methodDescription, holder);
        }
        return cache.get(methodDescription);
    }

    public static MethodDescription.InDefinedShape create(MethodDescription.InDefinedShape methodDescription, MethodDescriptionInDefinedShapeHolder holder){
        System.out.println("Declared annotations: " + methodDescription.getDeclaredAnnotations());
        holder.setDeclaredAnnotations(methodDescription.getDeclaredAnnotations());
        System.out.println("Type variables: " + methodDescription.getTypeVariables());
        holder.setTypeVariables(methodDescription.getTypeVariables());
        System.out.println("Internal name: " + methodDescription.getInternalName());
        holder.setInternalName(methodDescription.getInternalName());
        System.out.println("Modifiers: " + methodDescription.getModifiers());
        holder.setModifiers(methodDescription.getModifiers());

        System.out.println("declaring type: " + methodDescription.getDeclaringType());
        holder.setDeclaringType(TypeDescriptionTransformer.getOrCreate(methodDescription.getDeclaringType()));

        holder.setDefined(MethodDescriptionInDefinedShapeHolder.getOrCreate(methodDescription.asDefined()));
        holder.setReceiverType(TypeDescriptionGeneric.create(methodDescription.getReceiverType()));

        System.out.println("default value: " + methodDescription.getDefaultValue());

        // todo: fix - it needs proper stub
        holder.setDefaultValue(methodDescription.getDefaultValue());

        holder.setExceptionTypes(TypeDescriptionGeneric.convert(methodDescription.getExceptionTypes()));

        System.out.println("parameters: " + methodDescription.getParameters());
        holder.setParameters(ParameterDescriptionHolder.convert(methodDescription.getParameters()));

        holder.setReturnType(TypeDescriptionGeneric.create(methodDescription.getReturnType()));
        return holder;
    }


    @Override
    public TypeDescription.Generic getReturnType() {
        return returnType;
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
    public TypeDescription getDeclaringType() {
        return declaringType;
    }

    @Override
    public ParameterList<ParameterDescription.InDefinedShape> getParameters() {
        return parameters;
    }

}
