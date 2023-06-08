package com.aluvesqe.producingwebservice.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.UUID;

public class HttpPostMultipart {
    private final String boundary;
    private static final String LINE = "\r\n";
    private final HttpURLConnection httpConn;
    private final String charset;
    private final OutputStream outputStream;
    private final PrintWriter writer;

    /**
     * This constructor initializes a new HTTP POST request with content type
     * is set to multipart/form-data
     *
     * @param requestURL
     * @param charset
     * @param headers
     * @throws IOException
     */
    public HttpPostMultipart(String requestURL, String charset, Map<String, String> headers) throws IOException {
        this.charset = charset;
        boundary = UUID.randomUUID().toString();
        URL url = new URL(requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);

        httpConn.setInstanceFollowRedirects(false);

        httpConn.setDoOutput(true);    // indicates POST method
        httpConn.setDoInput(true);
        httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        if (headers != null && headers.size() > 0) {
            for (String key : headers.keySet()) {
                String value = headers.get(key);
                httpConn.setRequestProperty(key, value);
            }
        }
        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);
    }


    /**
     * Adds a form field to the request
     *
     * @param name  field name
     * @param value field value
     */
    public void addFormField(String name, String value) {
        writer.append("--").append(boundary).append(LINE);
        writer.append("Content-Disposition: form-data; name=\"").append(name).append("\"").append(LINE);
        writer.append("Content-Type: text/plain; charset=").append(charset).append(LINE);
        writer.append(LINE);
        writer.append(value).append(LINE);
        writer.flush();
    }

    /**
     * Adds a upload file section to the request
     *
     * @param fieldName
     * @param uploadFile
     * @throws IOException
     */
    public void addFilePart(String fieldName, File uploadFile)
            throws IOException {
        String fileName = uploadFile.getName();
        writer.append("--").append(boundary).append(LINE);
        writer.append("Content-Disposition: form-data; name=\"").append(fieldName).append("\"; filename=\"").append(fileName).append("\"").append(LINE);
        writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(fileName)).append(LINE);
        writer.append("Content-Transfer-Encoding: binary").append(LINE);
        writer.append(LINE);
        writer.flush();

        FileInputStream inputStream = new FileInputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();
        writer.append(LINE);
        writer.flush();
    }

    /**
     * Completes the request and receives response from the server.
     *
     * @return String as response in case the server returned
     * status OK, otherwise an exception is thrown.
     * @throws IOException
     */
    public String sendLoginRequest() throws IOException {
        String response = "";
        writer.flush();
        writer.append("--").append(boundary).append("--").append(LINE);
        writer.close();

        // checks server's status code first
        int status = httpConn.getResponseCode();

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = httpConn.getInputStream().read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        //response = result.toString(this.charset);
        String cookie = httpConn.getHeaderField("Set-Cookie");
        String location = httpConn.getHeaderField("Location");
        httpConn.disconnect();
        if(location.contains("login")){
            return null;
        }else{
            return cookie;
        }
    }


    public String sendPostRequest() throws IOException {
        String response;
        writer.flush();
        writer.append("--").append(boundary).append("--").append(LINE);
        writer.close();

        // checks server's status code first
        int status = httpConn.getResponseCode();

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = httpConn.getInputStream().read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        response = result.toString(this.charset);
        httpConn.disconnect();
        return response;
    }
}