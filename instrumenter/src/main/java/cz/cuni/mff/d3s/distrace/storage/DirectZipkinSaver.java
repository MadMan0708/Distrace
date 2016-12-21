package cz.cuni.mff.d3s.distrace.storage;


import com.google.auto.service.AutoService;
import cz.cuni.mff.d3s.distrace.api.Span;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@AutoService(SpanSaver.class)
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
            System.out.println(url);
            HttpURLConnection httpCon = (HttpURLConnection)url.openConnection();
            httpCon.setRequestMethod("POST");
            httpCon.addRequestProperty("Content-Type", "application/json");
            httpCon.setDoOutput(true);
            OutputStream os = httpCon.getOutputStream();
            BufferedWriter osw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            osw.write(spanStr);
            osw.flush();
            //osw.close();
            os.flush();
            //os.close();

            System.out.println(httpCon.getHeaderFields());
            httpCon.getResponseCode();

            int responseCode = httpCon.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);
            System.out.println(spanStr);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(httpCon.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            System.out.println(response.toString());
        } catch (IOException e) {
            // log could not save span
            e.printStackTrace();
        }

    }

}
