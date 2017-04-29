package cz.cuni.mff.d3s.distrace.examples;

import com.google.auto.service.AutoService;
import cz.cuni.mff.d3s.distrace.storage.DirectZipkinExporter;
import cz.cuni.mff.d3s.distrace.storage.SpanExporter;
import cz.cuni.mff.d3s.distrace.tracing.Span;

/**
 * Custom H2O Span exporter which overrides default service name
 */
@AutoService(SpanExporter.class)
public class H2OSpanExporter extends DirectZipkinExporter {

    public H2OSpanExporter(String args) {
        super(args);
    }

    @Override
    public void export(Span span) {
        span.setServiceName("MRTask");
        submitSpanTask(new DirectZipkinExporterTask(span));
    }
}
