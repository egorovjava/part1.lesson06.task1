package part1.lesson06;

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

    static String getOutput() {
        File dir = new File(".");
        StringBuilder body = new StringBuilder();
        body.append("<!DOCTYPE html>");
        body.append(System.lineSeparator());
        body.append("<html>");
        body.append(System.lineSeparator());
        body.append("<ol>");
        body.append(System.lineSeparator());
        Arrays.stream(dir.list()).forEach(x -> {
            body.append("<li>");
            body.append(x);
            body.append("</li>");
            body.append(System.lineSeparator());
        });
        body.append("</ol>");
        body.append(System.lineSeparator());
        body.append("</html>");
        String output = body.toString();
        StringBuilder out = new StringBuilder();
        out.append("HTTP/1.1 200 OK");
        out.append(System.lineSeparator());
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        out.append("Date: " + timeStamp);
        out.append(System.lineSeparator());
        out.append("Server: MyHttpServer");
        out.append(System.lineSeparator());
        out.append("X-Powered-By: Java SE 1.8");
        out.append(System.lineSeparator());
        out.append("Last-Modified: " + timeStamp);
        out.append(System.lineSeparator());
        out.append("Content-Language: ru");
        out.append(System.lineSeparator());
        out.append("Content-Type: text/html; charset=utf-8");
        out.append(System.lineSeparator());
        out.append("Content-Length: " + output.getBytes().length);
        out.append(System.lineSeparator());
        out.append("Connection: close");
        out.append(System.lineSeparator());
        out.append(System.lineSeparator());
        out.append(output);

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

    void close() throws IOException {
        isRunnable = false;
        serverSocket.close();
    }
}
