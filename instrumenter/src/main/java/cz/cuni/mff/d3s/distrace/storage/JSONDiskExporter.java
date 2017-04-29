package cz.cuni.mff.d3s.distrace.storage;

import cz.cuni.mff.d3s.distrace.tracing.Span;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Implementation of Span exporter which exports the Span data in a JSON format into the provided directory
 */
public class JSONDiskExporter extends SpanExporter {
    private String path;

    public JSONDiskExporter(String args) {
        parseAndSetArgs(args);
    }

    @Override
    public void export(final Span span) {
        submitSpanTask(new JSONDiskExporterTask(span));
    }

    @Override
    public void parseAndSetArgs(String args) {
        this.path = args;
    }

    /**
     * Task for exporting spans, which exports spans on disk into the specified directory
     */
    public class JSONDiskExporterTask implements Runnable {
        private Span span;

        private JSONDiskExporterTask(Span span) {
            this.span = span;
        }

        @Override
        public void run() {
            try (PrintWriter out = new PrintWriter(new File(path, span.getTimestamp() + ".json"))) {
                out.write(span.toJSON().toString());
            } catch (FileNotFoundException e) {
                // span couldn't be save to disk
                e.printStackTrace();
            }
        }
    }
}
