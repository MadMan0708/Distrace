package cz.cuni.mff.d3s.distrace.stubs;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Stub class for TypeList
 */
class TypeListStub extends TypeList.AbstractBase implements Serializable {

    private static HashMap<TypeList, TypeList> cache = new HashMap<>();
    private int stackSize;
    private String[] internalNames;
    private int size;
    private List<TypeDescription> typeDescriptions = new ArrayList<>();

    static TypeList from(TypeList typeList) {
        if (!cache.containsKey(typeList)) {
            TypeListStub stub = new TypeListStub();
            cache.put(typeList, stub);
            TypeListStub.from(typeList, stub);
        }
        return cache.get(typeList);
    }

    private static TypeListStub from(TypeList typeList, TypeListStub stub) {
        stub.stackSize = typeList.getStackSize();
        stub.internalNames = typeList.toInternalNames();
        stub.size = typeList.size();

        for (TypeDescription descr : typeList) {
            stub.typeDescriptions.add(TypeDescriptionStub.from(descr));
        }
        return stub;
    }

    @Override
    public TypeDescription get(int index) {
        return typeDescriptions.get(index);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public String[] toInternalNames() {
        return internalNames;
    }

    @Override
    public int getStackSize() {
        return stackSize;
    }
}
