package cz.cuni.mff.d3s.distrace.stubs;

import net.bytebuddy.description.TypeVariableSource;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.implementation.bytecode.StackSize;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by kuba on 08/09/16.
 */
public class TypeDescriptionGeneric implements TypeDescription.Generic, Serializable{

    private TypeDescription.Generic rawType;
    private TypeList.Generic upperBounds;
    private TypeList.Generic lowerBounds;
    private TypeList.Generic typeArguments;
    private TypeDescription.Generic ownerType;
    private String symbol;
    private TypeDescription.Generic componentType;
    private Sort sort;
    private String typeName;
    private StackSize stackSize;
    private boolean isArray;
    private boolean isPrimitive;
    private boolean representsType;
    private TypeDescription.Generic genericType;
    private TypeDescription erasure;
    private TypeDescription.Generic superClass;
    private TypeList.Generic interfaces;
    private FieldList<FieldDescription.InGenericShape> fields;
    private MethodList<MethodDescription.InGenericShape> methods;
    private Object visitor;
    private AnnotationList declaredAnnotations;
    private Iterator<TypeDefinition> iterator;
    private boolean isInterface;
    private boolean isAnnotation;
    private boolean isAbstract;
    private boolean isEnum;
    private boolean isPublic;
    private boolean isProtected;
    private boolean isPackagePrivate;
    private boolean isPrivate;
    private boolean isStatic;
    private boolean isDeprecated;
    private boolean isFinal;
    private boolean isSynthetic;
    private String actualName;
    private int modifiers;
    private TypeVariableSource typeVariableSource;

    void setTypeVariableSource(TypeVariableSource typeVariableSource){
        this.typeVariableSource = typeVariableSource;
    }
    void setRawType(TypeDescription.Generic rawType) {
        this.rawType = rawType;
    }

    void setUpperBounds(TypeList.Generic upperBounds) {
        this.upperBounds = upperBounds;
    }

    void setLowerBounds(TypeList.Generic lowerBounds) {
        this.lowerBounds = lowerBounds;
    }

    void setTypeArguments(TypeList.Generic typeArguments) {
        this.typeArguments = typeArguments;
    }

    void setOwnerType(TypeDescription.Generic ownerType) {
        this.ownerType = ownerType;
    }

    void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    void setComponentType(TypeDescription.Generic componentType) {
        this.componentType = componentType;
    }

    void setSort(Sort sort) {
        this.sort = sort;
    }

    void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    void setStackSize(StackSize stackSize) {
        this.stackSize = stackSize;
    }

    void setArray(boolean array) {
        isArray = array;
    }

    void setPrimitive(boolean primitive) {
        isPrimitive = primitive;
    }

    void setRepresentsType(boolean representsType) {
        this.representsType = representsType;
    }

    void setGenericType(TypeDescription.Generic genericType) {
        this.genericType = genericType;
    }

    void setErasure(TypeDescription erasure) {
        this.erasure = erasure;
    }

    void setSuperClass(TypeDescription.Generic superClass) {
        this.superClass = superClass;
    }

    void setInterfaces(TypeList.Generic interfaces) {
        this.interfaces = interfaces;
    }

    void setFields(FieldList<FieldDescription.InGenericShape> fields) {
        this.fields = fields;
    }

    void setMethods(MethodList<MethodDescription.InGenericShape> methods) {
        this.methods = methods;
    }

    void setVisitor(Object visitor) {
        this.visitor = visitor;
    }

    void setDeclaredAnnotations(AnnotationList declaredAnnotations) {
        this.declaredAnnotations = declaredAnnotations;
    }

    void setIterator(Iterator<TypeDefinition> iterator) {
        this.iterator = iterator;
    }


    private static class TypeListGenericHolder extends TypeList.Generic.AbstractBase implements Serializable{
        private ArrayList<TypeDescription.Generic> types;

        public TypeListGenericHolder(ArrayList<TypeDescription.Generic> types){
            this.types = types;
        }


        @Override
        public int size() {
            return types.size();
        }

        @Override
        public TypeDescription.Generic get(int index) {
           return types.get(index);
        }
    }

    public static TypeList.Generic convert(TypeList.Generic list){

        ArrayList<TypeDescription.Generic> methodList = new ArrayList<>();
        for(TypeDescription.Generic s: list){
            System.out.println("Converting method "+ s);
            methodList.add(create(s));
        }
        return new TypeListGenericHolder(methodList);
    }

    public static TypeDescription.Generic create(TypeDescription.Generic typeDescr){
        if(typeDescr == null){
            return null;
        }
        TypeDescriptionGeneric holder = new TypeDescriptionGeneric();
        holder.setActualName(typeDescr.getActualName());
        holder.setSynthetic(typeDescr.isSynthetic());
        holder.setFinal(typeDescr.isFinal());
        holder.setDeprecated(typeDescr.isDeprecated());
        holder.setPrivate(typeDescr.isPrivate());
        holder.setStatic(typeDescr.isStatic());
        holder.setPackagePrivate(typeDescr.isPackagePrivate());
        holder.setProtected(typeDescr.isProtected());
        holder.setPublic(typeDescr.isPublic());
        holder.setEnum(typeDescr.isEnum());
        holder.setAbstract(typeDescr.isAbstract());
        holder.setInterface(typeDescr.isInterface());

        //TODO: proper stub
        holder.setAnnotation(typeDescr.isAnnotation());
        //holder.setDeclaredAnnotations(typeDescr.getDeclaredAnnotations());
        
        //holder.setIterator(typeDescr.iterator());
        //holder.setVisitor(typeDescr.ac);
        //holder.setMethods(MethodDescriptionInDefinedShapeHolder.convert(typeDescr.getDeclaredMethods()));
        //holder.setFields
        return holder;
    }

    void setInterface(boolean anInterface) {
        isInterface = anInterface;
    }

    void setAnnotation(boolean annotation) {
        isAnnotation = annotation;
    }

    void setAbstract(boolean anAbstract) {
        isAbstract = anAbstract;
    }

    void setEnum(boolean anEnum) {
        isEnum = anEnum;
    }

    void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    void setProtected(boolean aProtected) {
        isProtected = aProtected;
    }

    void setPackagePrivate(boolean packagePrivate) {
        isPackagePrivate = packagePrivate;
    }

    void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }

    void setDeprecated(boolean deprecated) {
        isDeprecated = deprecated;
    }

    void setFinal(boolean aFinal) {
        isFinal = aFinal;
    }

    void setSynthetic(boolean synthetic) {
        isSynthetic = synthetic;
    }

    void setActualName(String actualName) {
        this.actualName = actualName;
    }




    @Override
    public TypeDescription.Generic asRawType() {
        return rawType;
    }

    @Override
    public TypeList.Generic getUpperBounds() {
        return upperBounds;
    }

    @Override
    public TypeList.Generic getLowerBounds() {
        return lowerBounds;
    }

    @Override
    public TypeList.Generic getTypeArguments() {
        return typeArguments;
    }

    @Override
    public TypeDescription.Generic getOwnerType() {
        return ownerType;
    }

    @Override
    public TypeVariableSource getTypeVariableSource() {
        return typeVariableSource;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public TypeDescription.Generic getComponentType() {
        return componentType;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public String getTypeName() {
        return typeName;
    }

    @Override
    public StackSize getStackSize() {
        return stackSize;
    }

    @Override
    public boolean isArray() {
        return isArray;
    }

    @Override
    public boolean isPrimitive() {
        return isPrimitive;
    }

    @Override
    public boolean represents(Type type) {
        return representsType;
    }

    @Override
    public TypeDescription.Generic asGenericType() {
        return genericType;
    }

    @Override
    public TypeDescription asErasure() {
        return erasure;
    }

    @Override
    public TypeDescription.Generic getSuperClass() {
        return superClass;
    }

    @Override
    public TypeList.Generic getInterfaces() {
        return interfaces;
    }

    @Override
    public FieldList<FieldDescription.InGenericShape> getDeclaredFields() {
        return fields;
    }

    @Override
    public MethodList<MethodDescription.InGenericShape> getDeclaredMethods() {
        return methods;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return null;
        //throw new RuntimeException("NOT IMPLEMENTD");
    }

    @Override
    public AnnotationList getDeclaredAnnotations() {
        return declaredAnnotations;
    }

    @Override
    public Iterator<TypeDefinition> iterator() {
        return iterator;
    }

    @Override
    public boolean isInterface() {
        return isInterface;
    }

    @Override
    public boolean isAnnotation() {
        return isAnnotation;
    }

    @Override
    public boolean isAbstract() {
        return isAbstract;
    }

    @Override
    public boolean isEnum() {
        return isEnum;
    }

    @Override
    public boolean isPublic() {
        return isPublic;
    }

    @Override
    public boolean isProtected() {
        return isProtected;
    }

    @Override
    public boolean isPackagePrivate() {
        return isPackagePrivate;
    }

    @Override
    public boolean isPrivate() {
        return isPrivate;
    }

    @Override
    public boolean isStatic() {
        return isStatic;
    }

    @Override
    public boolean isDeprecated() {
        return isDeprecated;
    }

    @Override
    public int getModifiers() {
        return modifiers;
    }

    @Override
    public boolean isFinal() {
        return isFinal;
    }

    @Override
    public boolean isSynthetic() {
        return isSynthetic;
    }

    @Override
    public String getActualName() {
        return actualName;
    }
}
