package ChatProgram_Swing;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Dave on 21/02/2017.
 */
public class ChatClient_Swing {

    // Textfields and Frames
    private BufferedReader read;
    private PrintWriter write;
    private JFrame frame = new JFrame("ChatWindow");
    private JTextField txtField = new JTextField(40);
    private JTextArea txtArea = new JTextArea(8, 40);
    private long startTime = 0;


    /** Main method - Runs the client end system */
    public static void main(String[] args) throws IOException {
        try {
            ChatClient_Swing client = new ChatClient_Swing();
            client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            client.frame.setVisible(true);
            client.frame.setSize(1200, 900);
            client.serverConn();
        }
        catch (NullPointerException nullex) {
            System.out.println("Closed application");
            System.exit(0);
        }
    }

    /** Client Constructor */
    public ChatClient_Swing() {

    // Graphical User Interface - frames / textfields
        txtField.setEditable(false);
        txtArea.setEditable(false);
        frame.getContentPane().add(txtField, "North");
        frame.getContentPane().add(new JScrollPane(txtArea), "Center");
        frame.pack();

    // Action Listeners
        txtField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                write.println(txtField.getText());
                txtField.setText("");
            }
        });
    }

    /** Prompts the user for the servers IP address */
    private String promptForServerIP() {
        return JOptionPane.showInputDialog(frame,
                "Enter IP Address of the server:",
                "Welcome to the Chat Client",
                JOptionPane.QUESTION_MESSAGE
        );
    }

    /** Prompts the user for a temporary name */
    private String getClientName() {
        return JOptionPane.showInputDialog(frame,
                "Choose a chat name:",
                "Screen name selection",
                JOptionPane.PLAIN_MESSAGE
        );
    }

    /** Method that starts a timer using milliseconds */
    private long startHeartBeat() {
        this.startTime = System.currentTimeMillis();
        return startTime;
    }
    /** Method that returns elapsed time of the startHeartBeat() method */
    private double elapsedTime() {
        long t = System.currentTimeMillis();
        return (t - startTime / 6000);
    }

    /** Connects to the ChatServer end system */
    private void serverConn() throws IOException {

        // Initializing connection - constructing socket using TCP Protocol, using IP address and Port number
        String serverIP = promptForServerIP();
        Socket socket = new Socket(serverIP, 6660);

        // Start counting the elapsed time of the connection
        startHeartBeat();

        // calling BufferedReader and PrintWriter with Input/OutputStreams to read/write data through sockets
        read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        write = new PrintWriter(socket.getOutputStream(), true);

        // while loop - processing protocol-messages from the server
        while (true) {
            String line = read.readLine();

            // recieve JOIN protocol-message from ChatServer, creating client as thread at ChatServer
            if (line.startsWith("JOIN")) {
                write.println(getClientName());
            }

            // recieve J_OK protocol-message from ChatServer, acknowledges client as thread at ChatServer
            else if (line.startsWith("J_OK")) {
                txtField.setEditable(true);
            }

            // recieve DATA or LIST protocol-message from ChatServer, enables writing a message
            else if (line.startsWith("DATA") || line.startsWith("LIST")) {
                txtArea.append(line.substring(8) + "\n");
            }

            // recieve J_ERROR protocol-message from ChatServer, prompts user for different username
            else if (line.startsWith("J_ERROR")) {
                write.println(getClientName());
            }

            // send  ALIVE protocol-message from ChatClient, displaying elapsed time the client has been connected to the server
            else if (line.startsWith("ALIVE")) {
                write.println(elapsedTime());
            }
        }
    }
}
