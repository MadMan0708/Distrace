package cz.cuni.mff.d3s.distrace.stubs;

import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.type.PackageDescription;

import java.io.Serializable;

/**
 * Created by kuba on 08/09/16.
 */
public class PackageDescriptionHolder extends PackageDescription.AbstractBase implements Serializable {

    public static PackageDescription create(PackageDescription packageDescription){
        PackageDescriptionHolder holder = new PackageDescriptionHolder();

        //TODO: PROPER ANNOtation handling
        //holder.setDeclaredAnnotations(packageDescription.getDeclaredAnnotations());
        holder.setName(packageDescription.getName());
        return holder;
    }

    private String name;
    private AnnotationList declaredAnnotations;


    private void setName(String name) {
        this.name = name;
    }

    private void setDeclaredAnnotations(AnnotationList declaredAnnotations) {
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
