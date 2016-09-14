package cz.cuni.mff.d3s.distrace.stubs;

import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.type.TypeDescription;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by kuba on 09/09/16.
 */
public class FieldDescriptionHolder  extends FieldDescription.InDefinedShape.AbstractBase implements Serializable {

    private TypeDescription declaringType;
    private TypeDescription.Generic type;
    private int modifiers;
    private String name;
    private AnnotationList declaredAnnotations;

    static class FieldListHolder extends FieldList.AbstractBase implements Serializable{

        private ArrayList<FieldDescription.InDefinedShape> fields;
        public FieldListHolder(ArrayList<FieldDescription.InDefinedShape> fields){
            this.fields = fields;
        }
        @Override
        public Object get(int index) {
            return fields.get(index);
        }

        @Override
        public int size() {
            return fields.size();
        }
    }

    public static FieldList<FieldDescription.InDefinedShape> convert(FieldList<FieldDescription.InDefinedShape> fields){

        ArrayList<FieldDescription.InDefinedShape> fieldList = new ArrayList<>();
        for(FieldDescription.InDefinedShape s: fields){
            fieldList.add(create(s));
        }
        return new FieldListHolder(fieldList);
    }


    public static FieldDescription.InDefinedShape create(FieldDescription.InDefinedShape fieldDescription){
        FieldDescriptionHolder holder = new FieldDescriptionHolder();
        holder.declaringType = TypeDescriptionStub.from(fieldDescription.getDeclaringType());
        holder.type = TypeDescriptionGeneric.create(fieldDescription.getType());
        holder.modifiers = fieldDescription.getModifiers();
        holder.name = fieldDescription.getName();

        //TODO: Implement stub for annotations
        //holder.declaredAnnotations = AnnotationListStub.from(fieldDescription.getDeclaredAnnotations());
        holder.declaredAnnotations = null;

        return holder;
    }

    @Override
    public TypeDescription getDeclaringType() {
        return declaringType;
    }

    @Override
    public TypeDescription.Generic getType() {
        return type;
    }

    @Override
    public int getModifiers() {
        return modifiers;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public AnnotationList getDeclaredAnnotations() {
        return declaredAnnotations;
    }
}
