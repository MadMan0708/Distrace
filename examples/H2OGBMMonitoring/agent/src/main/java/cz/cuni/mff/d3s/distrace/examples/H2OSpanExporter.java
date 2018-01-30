package cz.cuni.mff.d3s.distrace.examples;

import com.google.auto.service.AutoService;
import cz.cuni.mff.d3s.distrace.storage.DirectZipkinExporter;
import cz.cuni.mff.d3s.distrace.storage.SpanExporter;
import cz.cuni.mff.d3s.distrace.tracing.Span;
import water.H2O;

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
        span.setServiceName("GBM");
        span.add("commit", H2O.ABV.lastCommitHash());
        span.add("h2o_version", H2O.ABV.projectVersion());
        submitSpanTask(new DirectZipkinExporterTask(span));
    }
}
