package cz.cuni.mff.d3s.distrace.storage;


import cz.cuni.mff.d3s.distrace.api.Span;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class DirectZipkinSaver extends SpanSaver {

    private String serverIpPort;

    public DirectZipkinSaver(String serverIpPort){
        this.serverIpPort = serverIpPort;
    }

    @Override
    public void saveSpan(Span span) {
        final String spanStr = span.toJSON();
        try {
            URL url = new URL("http://"+serverIpPort+"/api/v1/spans");
            System.out.println("\nSending 'POST' request to URL : " + url);
            HttpURLConnection httpCon = (HttpURLConnection)url.openConnection();
            httpCon.setRequestMethod("POST");
            httpCon.addRequestProperty("Content-Type", "application/json");
            httpCon.setDoOutput(true);
            OutputStream os = httpCon.getOutputStream();
            BufferedWriter osw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            osw.write(spanStr);
            osw.flush();
            os.flush();
            System.out.println(spanStr);

            System.out.println("Response Code : " + httpCon.getResponseCode());

        } catch (IOException e) {
            // log could not save span
            e.printStackTrace();
        }

    }

}
