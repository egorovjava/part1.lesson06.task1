package part1.lesson06;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException, InterruptedException {

        final int port = 2020;

        MyHttpServer server = new MyHttpServer(port);
        Thread serverThread = new Thread(server);
        serverThread.start();

        Thread.sleep(100);

        Socket socket = new Socket("localhost", port);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        out.println("GET / HTTP/1.1");
        ArrayList<String> list = new ArrayList<>();
        String fromServer;
        while((fromServer = in.readLine()) != null) {
            list.add(fromServer);
        }
        list.forEach(System.out::println);
        socket.close();
    }
}
