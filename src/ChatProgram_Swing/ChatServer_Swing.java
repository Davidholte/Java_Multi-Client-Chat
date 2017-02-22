package ChatProgram_Swing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

/**
 * Created by Dave on 21/02/2017.
 */
public class ChatServer_Swing {


    /** Sets and Variables */
    // Set of names of all the clients
    private static final int PORT = 8080;

    private static HashSet<String> clientNames = new HashSet<>();

    private static HashSet<PrintWriter> printWriters = new HashSet<>();


    /** Main method to create ClientHandler threads */
    public static void main(String[] args) throws IOException {

        System.out.println("Server is operational");
        ServerSocket listener = new ServerSocket(PORT);
        try {
            while (true) {
                new ClientHandler(listener.accept()).start();
            }
        }
        finally {
            listener.close();
        }
    }


    /** ClientHandler class extended from Thread Class to handle each customer paralleled */
    private static class ClientHandler extends Thread {

        private String name;
        private PrintWriter write;
        private BufferedReader read;
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }


    /** How each ClientHandler thread processes each client */
        public void run() {

            try {
                read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                write = new PrintWriter(socket.getOutputStream(), true);

                while (true) {
                    write.println("SUBMITNAME");
                    name = read.readLine();

                    if (name == null) {
                        return;
                    }
                    else {
                        synchronized (clientNames) {
                            if (!clientNames.contains(name)) {
                                clientNames.add(name);
                                break;
                            }
                        }
                    }
                }
                write.println("NAMEACCEPTED");
                printWriters.add(write);

                while (true) {
                    String input = read.readLine();

                    if (input == null) {
                        return;
                    }
                    for (PrintWriter writer : printWriters) {
                        writer.println("MESSAGE " + name + ": " + input);
                    }
                }
            }
            catch (IOException ioException) {
                System.out.println(ioException);
            }
            finally {
                if (name != null) {
                    clientNames.remove(name);
                }
                if (write != null) {
                    printWriters.remove(write);
                }

                try {
                    socket.close();
                }
                catch (IOException ioException) {
                 //   System.out.println(ioException);
                }
            }
        }

    }


}
