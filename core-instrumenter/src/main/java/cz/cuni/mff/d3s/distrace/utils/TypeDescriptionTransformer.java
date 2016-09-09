package cz.cuni.mff.d3s.distrace.utils;

import net.bytebuddy.description.type.TypeDescription;

import java.util.HashMap;

/**
 * Storage of already create type description holders
 */
public class TypeDescriptionTransformer {

    // <old, new>
    private static HashMap<TypeDescription, TypeDescription> descriptions = new HashMap<>();

    public static TypeDescription getOrCreate(TypeDescription typeDescr){
        if(!descriptions.containsKey(typeDescr)){
            TypeDescriptionHolder holder = new TypeDescriptionHolder();
            descriptions.put(typeDescr, holder);
            TypeDescriptionHolder.create(typeDescr, holder);
        }
        return descriptions.get(typeDescr);
    }
}
