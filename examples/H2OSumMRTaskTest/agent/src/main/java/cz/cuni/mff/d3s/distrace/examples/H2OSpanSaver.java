package cz.cuni.mff.d3s.distrace.examples;

import com.google.auto.service.AutoService;
import cz.cuni.mff.d3s.distrace.api.Span;
import cz.cuni.mff.d3s.distrace.storage.DirectZipkinSaver;
import cz.cuni.mff.d3s.distrace.storage.SpanSaver;

@AutoService(SpanSaver.class)
public class H2OSpanSaver extends DirectZipkinSaver {

    public H2OSpanSaver(String args) {
        super(args);
    }

    @Override
    public void saveSpan(Span span) {
        submitSpanTask(new DirectZipkinSaverTask(span.setServiceName("MRTask")));
    }
}
