package cz.cuni.mff.d3s.distrace.stubs;


import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Stub for AnnotationList
 */
class AnnotationListStub extends AnnotationList.AbstractBase implements Serializable {

    private static HashMap<AnnotationList, AnnotationList> cache = new HashMap<>();
    private List<AnnotationDescriptionHolder> annotationList = new ArrayList<>();

    static AnnotationList from(AnnotationList annotationList) {
        if (!cache.containsKey(annotationList)) {
            AnnotationListStub stub = new AnnotationListStub();
            cache.put(annotationList, stub);
            AnnotationListStub.from(annotationList, stub);
        }
        return cache.get(annotationList);
    }

    private static AnnotationList from(AnnotationList annotationList, AnnotationListStub stub) {
        //TODO: Implement annotation stub
        //for (AnnotationDescription an : annotationList) {
        //    stub.add(AnnotationDescriptionHolder.from(an));
        //}
        return stub;
    }

    @Override
    public AnnotationDescription get(int index) {
        return annotationList.get(index);
    }

    @Override
    public int size() {
        return annotationList.size();
    }
}
