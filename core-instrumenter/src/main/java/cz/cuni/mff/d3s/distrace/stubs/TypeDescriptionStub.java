package cz.cuni.mff.d3s.distrace.stubs;

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
import java.util.HashMap;

/**
 * Stub for TypeDescription
 */
public class TypeDescriptionStub extends TypeDescription.AbstractBase implements Serializable {

    private static HashMap<TypeDescription, TypeDescription> cache = new HashMap<>();
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
    private String canonicalName;
    private boolean isAnonymousClass;
    private boolean isLocalClass;
    private boolean isMemberClass;
    private PackageDescription packageDescription;
    private String descriptor;
    private String name;
    private TypeList.Generic typeVariables;
    private AnnotationList declaredAnnotations;
    private int modifiers;

    public static TypeDescription from(TypeDescription typeDescr) {
        if (!cache.containsKey(typeDescr)) {
            TypeDescriptionStub holder = new TypeDescriptionStub();
            cache.put(typeDescr, holder);
            TypeDescriptionStub.from(typeDescr, holder);
        }
        return cache.get(typeDescr);
    }

    private static TypeDescription from(TypeDescription typeDescr, TypeDescriptionStub holder) {
        if (typeDescr == null) {
            return TypeDescription.UNDEFINED;
        }
        holder.isAnonymousClass = typeDescr.isAnonymousClass();
        holder.isArray = typeDescr.isArray();
        holder.canonicalName = typeDescr.getCanonicalName();
        holder.componentType = from(typeDescr.getComponentType());
        holder.stackSize = typeDescr.getStackSize();
        holder.isPrimitive = typeDescr.isPrimitive();
        holder.declaringType = from(typeDescr.getDeclaringType());
        holder.enclosingType = from(typeDescr.getEnclosingType());
        holder.simpleName = typeDescr.getSimpleName();
        holder.isLocalClass = typeDescr.isLocalClass();
        holder.isMemberClass = typeDescr.isMemberClass();
        holder.modifiers = typeDescr.getModifiers();
        holder.name = typeDescr.getName();
        holder.descriptor = typeDescr.getDescriptor();
        holder.methods = MethodDescriptionInDefinedShapeHolder.convert(typeDescr.getDeclaredMethods());
        holder.superClass = TypeDescriptionGenericStub.from(typeDescr.getSuperClass());
        //holder.interfaces = TypeListGenericStub.from(typeDescr.getInterfaces());
        //holder.fields = FieldDescriptionHolder.convert(typeDescr.getDeclaredFields());
        //holder.declaredTypes = TypeListStub.from(typeDescr.getDeclaredTypes());
        //holder.enclosingMethod = MethodDescriptionStub.from(typeDescr.getEnclosingMethod());
        //holder.packageDescription = PackageDescriptionHolder.create(typeDescr.getPackage());
        //holder.typeVariables = TypeListGenericStub.from(typeDescr.getTypeVariables());
        //holder.declaredAnnotations = AnnotationListStub.from(typeDescr.getDeclaredAnnotations());

        return holder;
    }

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
        return fields;
    }

    @Override
    public MethodList<MethodDescription.InDefinedShape> getDeclaredMethods() {
        return methods;
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
        return canonicalName;
    }

    @Override
    public boolean isAnonymousClass() {
        return isAnonymousClass;
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
