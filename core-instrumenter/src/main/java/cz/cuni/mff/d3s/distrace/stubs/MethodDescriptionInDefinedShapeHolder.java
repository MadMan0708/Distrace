package cz.cuni.mff.d3s.distrace.stubs;

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
 * Stub class for MethodDescription.InDefinedShape
 */
public class MethodDescriptionInDefinedShapeHolder extends MethodDescription.InDefinedShape.AbstractBase implements Serializable {

    private static HashMap<MethodDescription.InDefinedShape, MethodDescription.InDefinedShape> cache = new HashMap<>();
    private TypeDescription.Generic returnType;
    private ParameterList<ParameterDescription.InDefinedShape> parameters;
    private TypeList.Generic exceptionTypes;
    private AnnotationValue<?, ?> defaultValue;
    private TypeDescription.Generic receiverType;
    private InDefinedShape defined;
    private TypeDescription declaringType;
    private int modifiers;
    private String internalName;
    private TypeList.Generic typeVariables;
    private AnnotationList declaredAnnotations;

    public static MethodList<MethodDescription.InDefinedShape> convert(MethodList<InDefinedShape> methods) {

        ArrayList<MethodDescription.InDefinedShape> methodList = new ArrayList<>();
        for (MethodDescription.InDefinedShape s : methods) {
            System.out.println("Converting method " + s);
            methodList.add(MethodDescriptionInDefinedShapeHolder.getOrCreate(s));
        }
        return new MethodListHolder(methodList);
    }

    public static MethodDescription.InDefinedShape getOrCreate(MethodDescription.InDefinedShape methodDescription) {
        if (!cache.containsKey(methodDescription)) {
            MethodDescriptionInDefinedShapeHolder holder = new MethodDescriptionInDefinedShapeHolder();
            cache.put(methodDescription, holder);
            MethodDescriptionInDefinedShapeHolder.create(methodDescription, holder);
        }
        return cache.get(methodDescription);
    }

    private static MethodDescription.InDefinedShape create(MethodDescription.InDefinedShape methodDescription, MethodDescriptionInDefinedShapeHolder holder) {

        holder.typeVariables = TypeListGenericStub.from(methodDescription.getTypeVariables());
        holder.internalName = methodDescription.getInternalName();
        holder.modifiers = methodDescription.getModifiers();
        holder.declaringType = TypeDescriptionStub.from(methodDescription.getDeclaringType());
        holder.defined = MethodDescriptionInDefinedShapeHolder.getOrCreate(methodDescription.asDefined());
        holder.receiverType = TypeDescriptionGenericStub.from(methodDescription.getReceiverType());
        holder.exceptionTypes = TypeListGenericStub.from(methodDescription.getExceptionTypes());
        holder.parameters = ParameterDescriptionHolder.convert(methodDescription.getParameters());
        holder.returnType = TypeDescriptionGenericStub.from(methodDescription.getReturnType());

        // TODO: Proper stub
        //holder.defaultValue = methodDescription.getDefaultValue();
        //holder.declaredAnnotations = methodDescription.getDeclaredAnnotations();
        holder.defaultValue = null;
        holder.declaredAnnotations = null;
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

    private static class MethodListHolder extends MethodList.AbstractBase implements Serializable {

        private ArrayList<MethodDescription.InDefinedShape> methods;

        MethodListHolder(ArrayList<MethodDescription.InDefinedShape> methods) {
            this.methods = methods;
        }

        @Override
        public Object get(int index) {
            return methods.get(index);
        }

        @Override
        public int size() {
            return methods.size();
        }
    }

}
