package cz.cuni.mff.d3s.distrace.utils;

import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.type.PackageDescription;

/**
 * Created by kuba on 08/09/16.
 */
public class PackageDescriptionHolder extends PackageDescription.AbstractBase {

    public static PackageDescription create(PackageDescription packageDescription){
        PackageDescriptionHolder holder = new PackageDescriptionHolder();
        holder.setDeclaredAnnotations(packageDescription.getDeclaredAnnotations());
        holder.setName(packageDescription.getName());
        return holder;
    }

    private String name;
    private AnnotationList declaredAnnotations;


    void setName(String name) {
        this.name = name;
    }

    void setDeclaredAnnotations(AnnotationList declaredAnnotations) {
        this.declaredAnnotations = declaredAnnotations;
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
