package cz.cuni.mff.d3s.distrace.storage;

import com.google.auto.service.AutoService;
import cz.cuni.mff.d3s.distrace.api.Span;

@AutoService(SpanSaver.class)
public class JSONDiskSaver extends SpanSaver {
    private String path;

    public JSONDiskSaver(String path){
        this.path = path;
    }

    @Override
    public void saveSpan(Span span) {

    }
}
