package cz.cuni.mff.d3s.distrace.examples;

import com.google.auto.service.AutoService;
import cz.cuni.mff.d3s.distrace.api.Span;
import cz.cuni.mff.d3s.distrace.storage.SpanSaver;

@AutoService(SpanSaver.class)
public class H2OSpanSaver extends SpanSaver {
    @Override
    public void saveSpan(Span span) {

    }
}
