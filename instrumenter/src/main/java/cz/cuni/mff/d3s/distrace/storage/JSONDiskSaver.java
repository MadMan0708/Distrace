package cz.cuni.mff.d3s.distrace.storage;

import cz.cuni.mff.d3s.distrace.tracing.Span;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class JSONDiskSaver extends SpanSaver {
    private String path;

    public JSONDiskSaver(String args){
        parseAndSetArgs(args);
    }

    @Override
    public void saveSpan(final Span span) {
        submitSpanTask(new JSONDiskSaverTask(span));
    }

    @Override
    public void parseAndSetArgs(String args) {
        this.path = args;
    }

    public class JSONDiskSaverTask implements Runnable{
        private Span span;
        private JSONDiskSaverTask(Span span){
            this.span = span;
        }
        @Override
        public void run() {
            try(PrintWriter out = new PrintWriter(new File(path, span.getTimestamp() + ".json"))){
                out.write(span.toJSON().toString());
            } catch (FileNotFoundException e) {
                // span couldn't be saved to disk
                e.printStackTrace();
            }
        }
    }
}
