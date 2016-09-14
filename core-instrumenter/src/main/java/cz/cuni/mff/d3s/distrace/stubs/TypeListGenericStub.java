package cz.cuni.mff.d3s.distrace.stubs;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Stub for TypeList.Generic
 */
class TypeListGenericStub extends TypeList.Generic.AbstractBase implements Serializable {

    private static HashMap<TypeList.Generic, TypeList.Generic> cache = new HashMap<>();
    private List<TypeDescription.Generic> typeDescriptions = new ArrayList<>();

    static TypeList.Generic from(TypeList.Generic typeList) {
        System.out.println("HERE");
        if (!cache.containsKey(typeList)) {
            TypeListGenericStub stub = new TypeListGenericStub();
            cache.put(typeList, stub);
            TypeListGenericStub.from(typeList, stub);
        }
        return cache.get(typeList);
    }

    private static TypeList.Generic from(TypeList.Generic typeList, TypeListGenericStub stub) {
        for (TypeDescription.Generic typeDescription : typeList) {
            stub.typeDescriptions.add(TypeDescriptionGenericStub.from(typeDescription));
        }
        return stub;
    }

    @Override
    public TypeDescription.Generic get(int index) {
        return typeDescriptions.get(index);
    }

    @Override
    public int size() {
        return typeDescriptions.size();
    }
}
