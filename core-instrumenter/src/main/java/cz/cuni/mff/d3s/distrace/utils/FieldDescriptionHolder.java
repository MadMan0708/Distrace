package cz.cuni.mff.d3s.distrace.utils;

import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;

/**
 * Created by kuba on 09/09/16.
 */
public class FieldDescriptionHolder  extends FieldDescription.InDefinedShape.AbstractBase implements Serializable {

    void setDeclaringType(TypeDescription declaringType) {
        this.declaringType = declaringType;
    }

    void setType(TypeDescription.Generic type) {
        this.type = type;
    }

    void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }

    void setName(String name) {
        this.name = name;
    }

    void setDeclaredAnnotations(AnnotationList declaredAnnotations) {
        this.declaredAnnotations = declaredAnnotations;
    }

    private TypeDescription declaringType;
    private TypeDescription.Generic type;
    private int modifiers;
    private String name;
    private AnnotationList declaredAnnotations;


    public static FieldList<FieldDescription.InDefinedShape> convert(FieldList<FieldDescription.InDefinedShape> fields){

        ArrayList<FieldDescription.InDefinedShape> fieldList = new ArrayList<>();
        for(FieldDescription.InDefinedShape s: fields){
            fieldList.add(create(s));
        }
        return new FieldList.Explicit<>(fieldList);
    }


    public static FieldDescription.InDefinedShape create(FieldDescription.InDefinedShape fieldDescription){
        FieldDescriptionHolder holder = new FieldDescriptionHolder();
        holder.setDeclaringType(TypeDescriptionTransformer.getOrCreate(fieldDescription.getDeclaringType()));
        holder.setType(TypeDescriptionGeneric.create(fieldDescription.getType()));
        holder.setModifiers(fieldDescription.getModifiers());
        holder.setName(fieldDescription.getName());
        holder.setDeclaredAnnotations(fieldDescription.getDeclaredAnnotations());

        return holder;
    }

    @Override
    public TypeDescription getDeclaringType() {
        return null;
    }

    @Override
    public TypeDescription.Generic getType() {
        return null;
    }

    @Override
    public int getModifiers() {
        return 0;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public AnnotationList getDeclaredAnnotations() {
        return null;
    }
}
