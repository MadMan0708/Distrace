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
import java.util.HashMap;
import java.util.Iterator;

/**
 * Stub for TypeDescription.Generic
 */
class TypeDescriptionGenericStub extends TypeDescription.Generic.AbstractBase implements Serializable{
    private TypeList.Generic upperBounds;
    private TypeList.Generic lowerBounds;
    private TypeList.Generic typeArguments;
    private TypeDescription.Generic ownerType;
    private TypeVariableSource typeVariableSource;
    private String symbol;
    private TypeDescription.Generic componentType;
    private Sort sort;
    private String typeName;
    private StackSize stackSize;
    private boolean isArray;
    private boolean isPrimitive;
    private TypeDescription erasure;
    private TypeDescription.Generic superClass;
    private TypeList.Generic interfaces;
    private FieldList<FieldDescription.InGenericShape> fields;
    private MethodList<MethodDescription.InGenericShape> methods;
    private Object visitor;
    private AnnotationList declaredAnnotations;
    private Iterator<TypeDefinition> iterator;
    private String actualName;

    private static HashMap<TypeDescription.Generic, TypeDescription.Generic> cache = new HashMap<>();


    static TypeDescription.Generic from(TypeDescription.Generic typeDescr) {
        if (!cache.containsKey(typeDescr)) {
            TypeDescriptionGenericStub stub = new TypeDescriptionGenericStub();
            cache.put(typeDescr, stub);
            TypeDescriptionGenericStub.from(typeDescr, stub);
        }
        return cache.get(typeDescr);
    }

    private static TypeDescription.Generic from(TypeDescription.Generic typeDescr, TypeDescriptionGenericStub stub){
        if(typeDescr == null){
            return null;
        }
        System.out.print("Creatin typeDesc generic for "+typeDescr);
        //stub.upperBounds = TypeListGenericStub.from(typeDescr.getUpperBounds());
        //stub.lowerBounds = TypeListGenericStub.from(typeDescr.getLowerBounds());
        //stub.typeArguments = TypeListGenericStub.from(typeDescr.getTypeArguments());
        //stub.ownerType = from(typeDescr.getOwnerType());
        //stub.typeVariableSource = typeDescr.getTypeVariableSource(); // ?
        //stub.symbol = typeDescr.getSymbol();
        //stub.componentType = from(typeDescr.getComponentType());
        stub.sort = typeDescr.getSort();
        stub.typeName = typeDescr.getTypeName();
        stub.stackSize = typeDescr.getStackSize();
        stub.isArray = typeDescr.isArray();
        stub.isPrimitive = typeDescr.isPrimitive();
        stub.erasure = TypeDescriptionStub.from(typeDescr.asErasure());
        //stub.superClass = from(typeDescr.getSuperClass());
        stub.interfaces = TypeListGenericStub.from(typeDescr.getInterfaces());
        //stub.fields = FieldDescriptionHolder.convert(typeDescr.getDeclaredFields());
        //stub.methods =
        //stub.declaredAnnotations = AnnotationListStub.from(typeDescr.getDeclaredAnnotations());
        //stub.iterator = typeDescr.i
        stub.actualName = typeDescr.getActualName();

        return stub;
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
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public AnnotationList getDeclaredAnnotations() {
        return declaredAnnotations;
    }

    @Override
    public Iterator<TypeDefinition> iterator() {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public String getActualName() {
        return actualName;
    }
}
