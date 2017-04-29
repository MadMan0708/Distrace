package cz.cuni.mff.d3s.distrace.storage;


import cz.cuni.mff.d3s.distrace.tracing.Span;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Implementation of Span exporter which sends the Span data in a JSON format directly to the Zipkin user interface
 */
public class DirectZipkinExporter extends SpanExporter {

    private String serverIpPort;

    public DirectZipkinExporter(String args) {
        parseAndSetArgs(args);
    }

    @Override
    public void export(Span span) {
        submitSpanTask(new DirectZipkinExporterTask(span));
    }

    @Override
    public void parseAndSetArgs(String args) {
        serverIpPort = args;
    }

    /**
     * Task for exporting tasks directly to the Zipkin UI.
     */
    public class DirectZipkinExporterTask implements Runnable {

        private Span span;

        public DirectZipkinExporterTask(Span span) {
            this.span = span;
        }

        @Override
        public void run() {
            final String spanStr = span.toJSON().toString();
            try {
                URL url = new URL("http://" + serverIpPort + "/api/v1/spans");
                if (debug) {
                    System.out.println("Sending span: " + span.toJSON().toString());
                }
                HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                httpCon.setRequestMethod("POST");
                httpCon.addRequestProperty("Content-Type", "application/json");
                httpCon.setDoOutput(true);
                OutputStream os = httpCon.getOutputStream();
                BufferedWriter osw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                osw.write(spanStr);
                osw.flush();
                os.flush();
                httpCon.getResponseCode();

            } catch (ConnectException e) {
                System.out.println("Couldn't connect to Zipkin Server, are you sure it is running on " + serverIpPort + "? Span " + span + " won't be sent!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
