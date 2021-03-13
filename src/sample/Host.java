package sample;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;

public class Host extends Connection{

    private ServerSocket server;

    public static void main(String[] args) {
        new Host();
    }


    final int PORT = 8081;

    public Host() {

        connector = new Thread(() -> {

            try {
                this.server = new ServerSocket(PORT);
                System.out.println(server.getLocalPort());
                System.out.println(server.getInetAddress().getHostName());
                socket = server.accept();


                input = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                output = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
                connected = true;
            } catch (Exception e) {
                System.err.println("Connection error");
            }


        });
        connector.start();
    }





}
