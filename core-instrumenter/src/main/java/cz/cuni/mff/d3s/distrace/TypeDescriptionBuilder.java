package cz.cuni.mff.d3s.distrace;

import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.PackageDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.implementation.bytecode.StackSize;

/**
 * Created by kuba on 06/09/16.
 */
public class TypeDescriptionBuilder {


    private TypeDescription typeDefinitions = new TypeDescription.AbstractBase(){
        @Override
        public FieldList<FieldDescription.InDefinedShape> getDeclaredFields() {
          return null;
        }

        @Override
        public MethodList<MethodDescription.InDefinedShape> getDeclaredMethods() {
            return null;
        }

        @Override
        public TypeDescription getComponentType() {
            return null;
        }

        @Override
        public TypeDescription getDeclaringType() {
            return null;
        }

        @Override
        public TypeList getDeclaredTypes() {
            return null;
        }

        @Override
        public MethodDescription getEnclosingMethod() {
            return null;
        }

        @Override
        public TypeDescription getEnclosingType() {
            return null;
        }

        @Override
        public String getSimpleName() {
            return null;
        }

        @Override
        public String getCanonicalName() {
            return null;
        }

        @Override
        public boolean isAnonymousClass() {
            return false;
        }

        @Override
        public boolean isLocalClass() {
            return false;
        }

        @Override
        public boolean isMemberClass() {
            return false;
        }

        @Override
        public PackageDescription getPackage() {
            return null;
        }

        @Override
        public String getDescriptor() {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public TypeList.Generic getTypeVariables() {
            return null;
        }

        @Override
        public AnnotationList getDeclaredAnnotations() {
            return null;
        }

        @Override
        public Generic getSuperClass() {
            return null;
        }

        @Override
        public TypeList.Generic getInterfaces() {
            return null;
        }

        @Override
        public StackSize getStackSize() {
            return null;
        }

        @Override
        public boolean isArray() {
            return false;
        }

        @Override
        public boolean isPrimitive() {
            return false;
        }

        @Override
        public int getModifiers() {
            return 0;
        }
    };
}
