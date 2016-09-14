package cz.cuni.mff.d3s.distrace.stubs;


import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDescription;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kuba on 09/09/16.
 */
public class ParameterDescriptionHolder extends ParameterDescription.InDefinedShape.AbstractBase implements Serializable {
    private TypeDescription.Generic type;
    private MethodDescription.InDefinedShape declaringMethod;
    private int index;
    private boolean hasModifiers;
    private boolean isNamed;
    private AnnotationList declaredAnnotations;

    void setType(TypeDescription.Generic type) {
        this.type = type;
    }

    void setDeclaringMethod(MethodDescription.InDefinedShape declaringMethod) {
        this.declaringMethod = declaringMethod;
    }

    void setIndex(int index) {
        this.index = index;
    }

    void setModifiers(boolean hasModifiers) {
        this.hasModifiers = hasModifiers;
    }

    void setNamed(boolean named) {
        isNamed = named;
    }

    void setDeclaredAnnotations(AnnotationList declaredAnnotations) {
        this.declaredAnnotations = declaredAnnotations;
    }

    private static class ParameterListHolder extends ParameterList.AbstractBase implements Serializable{
        private ArrayList<InDefinedShape> params;

        public ParameterListHolder(ArrayList<InDefinedShape> params){
            this.params = params;
        }
        @Override
        public Object get(int index) {
            return params.get(index);
        }

        @Override
        public int size() {
            return params.size();
        }
    }
    public static ParameterList<ParameterDescription.InDefinedShape> convert(ParameterList<InDefinedShape> parameters){

        ArrayList<InDefinedShape> parameterList = new ArrayList<>();
        for(InDefinedShape param: parameters){
            parameterList.add(ParameterDescriptionHolder.getOrCreate(param));
        }
        return new ParameterListHolder(parameterList);
    }

    private static HashMap<InDefinedShape,InDefinedShape> cache = new HashMap<>();

    public static InDefinedShape getOrCreate(InDefinedShape paramDescription){
        if(!cache.containsKey(paramDescription)){
           ParameterDescriptionHolder holder = new ParameterDescriptionHolder();
            cache.put(paramDescription, holder);
            ParameterDescriptionHolder.create(paramDescription, holder);
        }
        return cache.get(paramDescription);
    }

    public static InDefinedShape create(InDefinedShape paramDescription, ParameterDescriptionHolder holder){
        holder.setDeclaringMethod(MethodDescriptionInDefinedShapeHolder.getOrCreate(paramDescription.getDeclaringMethod()));
        holder.setIndex(paramDescription.getIndex());
        holder.setModifiers(paramDescription.hasModifiers());
        holder.setNamed(paramDescription.isNamed());
        holder.setType(TypeDescriptionGeneric.create(paramDescription.getType()));
        // improve annotations handling
        //holder.setDeclaredAnnotations(paramDescription.getDeclaredAnnotations());
        return holder;
    }
    @Override
    public TypeDescription.Generic getType() {
        return type;
    }

    @Override
    public MethodDescription.InDefinedShape getDeclaringMethod() {
        return declaringMethod;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public boolean hasModifiers() {
        return hasModifiers;
    }

    @Override
    public boolean isNamed() {
        return isNamed;
    }

    @Override
    public AnnotationList getDeclaredAnnotations() {
        return declaredAnnotations;
    }
}
