package cz.cuni.mff.d3s.distrace.stubs;

import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationValue;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;

import java.lang.annotation.Annotation;

/**
 * Created by kuba on 12/09/16.
 */
public class AnnotationDescriptionHolder extends AnnotationDescription.AbstractBase {


    public static AnnotationDescriptionHolder create(AnnotationDescription annotationDescription){

        AnnotationDescriptionHolder holder = new AnnotationDescriptionHolder();
        //annotationDescription.
        return holder;
    }

    @Override
    public AnnotationValue<?, ?> getValue(MethodDescription.InDefinedShape property) {
        //this.getValue()
        //return property
    return null;
    }

    @Override
    public TypeDescription getAnnotationType() {
        return null;
    }

    @Override
    public <T extends Annotation> Loadable<T> prepare(Class<T> annotationType) {
        return null;
    }
}
