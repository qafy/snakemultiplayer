package sample;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class Connection {
    PrintWriter output;
    BufferedReader input;
    Socket socket;
    protected boolean connected = false;
    protected Thread connector;

    public void write(String... strings) {
        output.println(strings.length);
        for (String s : strings)
            output.println(s);
    }
/*
    public String[] read() {
        try {
            int i = Integer.parseInt(input.readLine());
            String[] arr = new String[i];
            for (int n = 0; n < i; n++)
                arr[n] = input.readLine();
            return arr;
        } catch (Exception e) { e.printStackTrace();}
        return null;
    }

    public void cancelConnection() {
        connector.stop();
    }
*/
    public boolean isConnected() {
        return connected;
    }

}
