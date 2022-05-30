package Networking2;

import java.io.*;
import java.net.*;

public class MyServer {
    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(54324);
            Socket s = ss.accept();
            DataInputStream dis = new DataInputStream(s.getInputStream());
            while(true) {
                String str = (String) dis.readUTF();
                System.out.println("Client say = " + str);
            }
            // ss.close();  
        }

        catch (Exception e) {
            System.out.println(e);
        }
    }
}