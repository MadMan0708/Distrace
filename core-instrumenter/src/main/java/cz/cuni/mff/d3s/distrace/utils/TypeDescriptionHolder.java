package cz.cuni.mff.d3s.distrace.utils;

import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.PackageDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.implementation.bytecode.StackSize;

import java.io.Serializable;

/**
 * Created by kuba on 08/09/16.
 */
public class TypeDescriptionHolder extends TypeDescription.AbstractBase implements Serializable{


    public static TypeDescription create(TypeDescription typeDescr, TypeDescriptionHolder holder){
        System.out.println("TYPE DESCRIPTION TO CONVERT: "+ typeDescr);
        if(typeDescr == null){
            return TypeDescription.UNDEFINED;
        }
        holder.setAnnonymousClass(typeDescr.isAnonymousClass());
        holder.setArray(typeDescr.isArray());
        holder.setCannonicalName(typeDescr.getCanonicalName());
        holder.setComponentType(create(typeDescr.getComponentType(), holder));
        holder.setStackSize(typeDescr.getStackSize());
        holder.setPrimitive(typeDescr.isPrimitive());
        holder.setDeclaringType(create(typeDescr.getDeclaringType(), holder));
        holder.setEnclosingType(create(typeDescr.getEnclosingType(), holder));
        holder.setSimpleName(typeDescr.getSimpleName());
        holder.setLocalClass(typeDescr.isLocalClass());
        holder.setAnnonymousClass(typeDescr.isAnonymousClass());
        holder.setMemberClass(typeDescr.isMemberClass());
        holder.setModifiers(typeDescr.getModifiers());
        holder.setName(typeDescr.getName());
        holder.setDescriptor(typeDescr.getDescriptor());

        holder.setMethods(MethodDescriptionInDefinedShapeHolder.convert(typeDescr.getDeclaredMethods())); //

        holder.setSuperClass(TypeDescriptionGeneric.create(typeDescr.getSuperClass()));

        System.out.println("Declared interfaces: "+ typeDescr.getInterfaces());
        holder.setInterfaces(TypeDescriptionGeneric.convert(typeDescr.getInterfaces())); //

        System.out.println("Declared fields: "+ typeDescr.getDeclaredFields());
        holder.setFields(FieldDescriptionHolder.convert(typeDescr.getDeclaredFields())); //

        System.out.println("Declared types: "+ typeDescr.getDeclaredTypes());
        holder.setDeclaredTypes(typeDescr.getDeclaredTypes()); //

        System.out.println("Enclosing method: "+ typeDescr.getEnclosingMethod());
        holder.setEnclosingMethod(MethodDescriptionHolder.create(typeDescr.getEnclosingMethod()));

        System.out.println("Package: "+ typeDescr.getPackage());
        holder.setPackageDescription(PackageDescriptionHolder.create(typeDescr.getPackage()));


        System.out.println("Declared annotations: "+ typeDescr.getDeclaredAnnotations());
        holder.setDeclaredAnnotations(typeDescr.getDeclaredAnnotations()); //

        System.out.println("Type variables: "+ typeDescr.getTypeVariables());
        holder.setTypeVariables(TypeDescriptionGeneric.convert(typeDescr.getTypeVariables())); //
        return holder;
    }
    void setSuperClass(Generic superClass) {
        this.superClass = superClass;
    }

    void setInterfaces(TypeList.Generic interfaces) {
        this.interfaces = interfaces;
    }

    void setFields(FieldList<FieldDescription.InDefinedShape> fields) {
        this.fields = fields;
    }

    void setMethods(MethodList<MethodDescription.InDefinedShape> methods) {
        this.methods = methods;
    }

    void setComponentType(TypeDescription componentType) {
        this.componentType = componentType;
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

    void setDeclaringType(TypeDescription declaringType) {
        this.declaringType = declaringType;
    }

    void setDeclaredTypes(TypeList declaredTypes) {
        this.declaredTypes = declaredTypes;
    }

    void setEnclosingMethod(MethodDescription enclosingMethod) {
        this.enclosingMethod = enclosingMethod;
    }

    void setEnclosingType(TypeDescription enclosingType) {
        this.enclosingType = enclosingType;
    }

    void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    void setCannonicalName(String cannonicalName) {
        this.cannonicalName = cannonicalName;
    }

    void setAnnonymousClass(boolean annonymousClass) {
        isAnnonymousClass = annonymousClass;
    }

    void setLocalClass(boolean localClass) {
        isLocalClass = localClass;
    }

    void setMemberClass(boolean memberClass) {
        isMemberClass = memberClass;
    }

    void setPackageDescription(PackageDescription packageDescription) {
        this.packageDescription = packageDescription;
    }

    void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    void setName(String name) {
        this.name = name;
    }

    void setTypeVariables(TypeList.Generic typeVariables) {
        this.typeVariables = typeVariables;
    }

    void setDeclaredAnnotations(AnnotationList declaredAnnotations) {
        this.declaredAnnotations = declaredAnnotations;
    }

    void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }

    private Generic superClass;
    private TypeList.Generic interfaces;
    private FieldList<FieldDescription.InDefinedShape> fields;
    private MethodList<MethodDescription.InDefinedShape> methods;
    private TypeDescription componentType;
    private StackSize stackSize;
    private boolean isArray;
    private boolean isPrimitive;
    private TypeDescription declaringType;
    private TypeList declaredTypes;
    private MethodDescription enclosingMethod;
    private TypeDescription enclosingType;
    private String simpleName;
    private String cannonicalName;
    private boolean isAnnonymousClass;
    private boolean isLocalClass;
    private boolean isMemberClass;
    private PackageDescription packageDescription;
    private String descriptor;
    private String name;
    private TypeList.Generic typeVariables;
    private AnnotationList declaredAnnotations;
    private int modifiers;

    @Override
    public Generic getSuperClass() {
        return superClass;
    }

    @Override
    public TypeList.Generic getInterfaces() {
        return interfaces;
    }

    @Override
    public FieldList<FieldDescription.InDefinedShape> getDeclaredFields() {
        System.out.println("AAA BBB CCC");return fields;
    }

    @Override
    public MethodList<MethodDescription.InDefinedShape> getDeclaredMethods() {
        System.out.println("AAA BBB CCC");return methods;
    }

    @Override
    public TypeDescription getComponentType() {
        return componentType;
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
    public TypeDescription getDeclaringType() {
        return declaringType;
    }

    @Override
    public TypeList getDeclaredTypes() {
        return declaredTypes;
    }

    @Override
    public MethodDescription getEnclosingMethod() {
        return enclosingMethod;
    }

    @Override
    public TypeDescription getEnclosingType() {
        return enclosingType;
    }

    @Override
    public String getSimpleName() {
        return simpleName;
    }

    @Override
    public String getCanonicalName() {
        return cannonicalName;
    }

    @Override
    public boolean isAnonymousClass() {
        return isAnnonymousClass;
    }

    @Override
    public boolean isLocalClass() {
        return isLocalClass;
    }

    @Override
    public boolean isMemberClass() {
        return isMemberClass;
    }

    @Override
    public PackageDescription getPackage() {
        return packageDescription;
    }

    @Override
    public String getDescriptor() {
        return descriptor;
    }

    @Override
    public String getName() {
        return name;
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
    public int getModifiers() {
        return modifiers;
    }
}
