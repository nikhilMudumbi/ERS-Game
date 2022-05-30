package Networking2;
import java.io.*;
import java.net.*;

public class MyClient {
    public static void main(String[] args) {
        try {
            Socket s = new Socket("10.62.1.197", 54324);
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            dout.writeUTF("Hello Server");
            dout.writeUTF("click: 0.344 seconds");
            dout.flush();
            dout.close();
            s.close();
        }

        catch (Exception e) {
            System.out.println(e);
        }
    }
}