package com.gmail.egorovsonalexey;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import javax.xml.ws.http.HTTPException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    @Test
    public void test200() throws IOException, InterruptedException {

        String testOutput = MyHttpServer.getOutput();

        final int port = 2020;

        MyHttpServer server = new MyHttpServer(port);
        Thread serverThread = new Thread(server);
        serverThread.start();

        Thread.sleep(100);

        Socket client = new Socket("localhost", port);
        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(client.getInputStream()));
        out.println("GET /some uri HTTP/1.1");

        StringBuilder sb = new StringBuilder();
        String fromServer;
        while ((fromServer = in.readLine()) != null) {
            sb.append(fromServer);
            sb.append(System.lineSeparator());
        }
        sb.delete(sb.length() - 2, sb.length());
        assertEquals(testOutput, sb.toString());
        client.close();
        server.stop();
    }

    @Test
    public void test404() throws IOException, InterruptedException {
        final int port = 2020;

        MyHttpServer server = new MyHttpServer(port);
        Thread serverThread = new Thread(server);
        serverThread.start();

        Thread.sleep(100);

        Socket socket = new Socket("localhost", port);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        out.println("POST / HTTP/1.1");

        String fromServer = in.readLine();
        assertEquals(MyHttpServer.notFound, fromServer);
    }
}
