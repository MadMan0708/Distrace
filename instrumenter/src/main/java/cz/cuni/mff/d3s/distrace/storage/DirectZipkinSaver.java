package cz.cuni.mff.d3s.distrace.storage;


import cz.cuni.mff.d3s.distrace.tracing.Span;
import cz.cuni.mff.d3s.distrace.json.JSONPrettyStringBuilder;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

public class DirectZipkinSaver extends SpanSaver {

    private String serverIpPort;

    public DirectZipkinSaver(String args){
        parseAndSetArgs(args);
    }

    @Override
    public void saveSpan(Span span) {
        submitSpanTask(new DirectZipkinSaverTask(span));
    }

    @Override
    public void parseAndSetArgs(String args) {
        serverIpPort = args;
    }

    public class DirectZipkinSaverTask implements Runnable {

        private Span span;
        public DirectZipkinSaverTask(Span span){
            this.span = span;
        }

        @Override
        public void run() {
            final String spanStr = span.toJSON().toString();
            try {
                URL url = new URL("http://" + serverIpPort + "/api/v1/spans");
                if(debug) {
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
            } catch (ConnectException e){
                System.out.println("Couldn't connect to Zipkin Server, are you sure it is running on " + serverIpPort + "? Span " + span + " won't be sent!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
