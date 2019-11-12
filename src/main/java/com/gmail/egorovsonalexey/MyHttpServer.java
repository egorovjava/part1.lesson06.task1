package com.gmail.egorovsonalexey;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Pattern;

public class MyHttpServer implements Runnable {

    private ServerSocket serverSocket;
    private boolean isRunnable = true;

    MyHttpServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    static final String notFound = "HTTP/1.1 404 Not Found";

    private static String getRequestBody() {

        File dir = new File(".");
        StringBuilder body = new StringBuilder();
        body.append("<!DOCTYPE html>").append(System.lineSeparator())
            .append("<html>").append(System.lineSeparator())
            .append("<ol>").append(System.lineSeparator());
        Arrays.stream(dir.list()).forEach(x -> {
            body.append("<li>").append(x).append("</li>").append(System.lineSeparator());
        });
        body.append("</ol>").append(System.lineSeparator())
            .append("</html>");
        return body.toString();
    }

    static String getOutput() {

        String body = getRequestBody();
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm").format(new Date());
        StringBuilder out = new StringBuilder();
        out.append("HTTP/1.1 200 OK").append(System.lineSeparator())
            .append("Date: " + timeStamp).append(System.lineSeparator())
            .append("Server: MyHttpServer").append(System.lineSeparator())
            .append("X-Powered-By: Java SE 1.8").append(System.lineSeparator())
            .append("Last-Modified: " + timeStamp).append(System.lineSeparator())
            .append("Content-Language: ru").append(System.lineSeparator())
            .append("Content-Type: text/html; charset=utf-8").append(System.lineSeparator())
            .append("Content-Length: " + body.getBytes().length).append(System.lineSeparator())
            .append("Connection: close").append(System.lineSeparator())
            .append(System.lineSeparator())
            .append(body);

        return out.toString();
    }

    @Override
    public void run() {
        Socket clientSocket;
        while(isRunnable) {
            try {
                if (serverSocket.isClosed()) {
                    break;
                }

                clientSocket = serverSocket.accept();

                PrintWriter out =
                        new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
                String inputLine = in.readLine();
                java.util.regex.Pattern pattern = Pattern.compile("^(GET)\\s+(.+)\\s+(HTTP)");

                if (pattern.matcher(inputLine).find()) {
                    out.println(getOutput());
                } else {
                    out.println(notFound);
                }
                in.close();
            } catch (SocketException e) {
                System.err.println("Socket is closed.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void stop() throws IOException {
        isRunnable = false;
        serverSocket.close();
    }
}
