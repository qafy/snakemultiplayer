package sample;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Client extends Connection {

    int direction = 0;


    public Client(String ip, int port) {

        connector = new Thread(() -> {

            try {

                this.socket = new Socket(ip, port);

                input = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                output = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
                connected = true;
            } catch (Exception e) {
                System.err.println("Connection error");
            }


        });
        connector.start();
    }
    // For remote control currently not working
    public static void main(String[] args) {
        Client DEFAULT = new Client("localhost", 8081);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while(true) {


            try {
                String s = br.readLine();
                System.out.println(s);
                DEFAULT.write(s);
            } catch (Exception e) {
           }
        }
    }

}
