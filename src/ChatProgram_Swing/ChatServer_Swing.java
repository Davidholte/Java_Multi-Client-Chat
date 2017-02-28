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
    private static final int PORT = 1234;

    private static HashSet<String> clientNames = new HashSet<>();

    private static HashSet<PrintWriter> printWriters = new HashSet<>();


    /** Main method to create ClientHandler threads on client joins */
    public static void main(String[] args) throws IOException {

        System.out.println("Server is operational..");
        ServerSocket serverSocket = new ServerSocket(PORT);
        try {
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        }
        finally {
            serverSocket.close();
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

            // creates new BufferedReader and PrintWriter, to be used as streaming channels pr client
            try {
                read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                write = new PrintWriter(socket.getOutputStream(), true);

                while (true) {
                    // send JOIN protocol-message to client, prompting for a username
                    write.println("JOIN");
                    name = read.readLine();

                    // if name is empty, do nothing
                    if (name == null) {
                        return;
                    }
                    else {
                        synchronized (clientNames) {
                            // add client's username to HashSet if statement is true, and stops while loop
                            if (!clientNames.contains(name) && name.matches("^[a-zA-Z0-9]*$")) {
                                clientNames.add(name);
                                break;
                            }
                            // checks if client's username is only alphanumeric, using a regular expression
                            // send J_ERROR_ILLCHAR protocol-message to client, trigger error message on client side
                            else if (!name.matches("^[a-zA-Z0-9]*$")) {
                                write.println("J_ERROR_ILLCHAR");
                            }
                            // checks if client's username doesnt exist in HashSet
                            // send J_ERROR_EXIUSER protocol-message to client, trigger error message on client side
                            else if (clientNames.contains(name)) {
                                write.println("J_ERROR_EXIUSER");
                            }
                        }
                    }
                }
                // sends message to ChatClient end system that writing to server is now OK
                write.println("J_OK");
                printWriters.add(write);

                while (true) {
                    String input = read.readLine();

                    // if client sends no input, do nothing
                    if (input == null) {
                        return;
                    }
                    // if client message is QUIT, close socket
                    if (input.startsWith("QUIT")) {
                        socket.close();
                    }
                    // if client message is LIST, print a list of all usernames to client
                    if (input.startsWith("LIST")) {
                        write.println(clientNames);
                    }
                    // prints one client's message to all clients
                    for (PrintWriter writer : printWriters) {
                        writer.println("DATA " + name + ": " + input);
                    }
                }
            }
            catch (IOException ioException) {
                System.out.println(ioException);
            }
            /** Flushing - removing the objects from the respective HashSets */
            finally {
                // when flushing, clients username is removed from clientName HashSet
                if (name != null) {
                    clientNames.remove(name);
                }
                // when flushing, printWriter is removed from printWriters HashSet
                if (write != null) {
                    printWriters.remove(write);
                }
                // closing socket
                try {
                    socket.close();
                }
                catch (IOException ioException) {
                    System.out.println(ioException);
                }
            }
        }

    }


}
