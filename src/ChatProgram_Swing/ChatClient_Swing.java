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


    /** Main method-  Runs the client end system */
    public static void main(String[] args) throws IOException {
        ChatClient_Swing client = new ChatClient_Swing();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.serverConn();
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
            public void actionPerformed(ActionEvent e) {
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

    /** Connects to the ChatServer end system */
    private void serverConn() throws IOException {

        // Initializing connection - constructing socket using TCP Protocol, using IP address and Port number
        String serverIP = promptForServerIP();
        Socket socket = new Socket(serverIP, 8080);

        // calling BufferedReader and PrintWriter with Input/OutputStreams to read/write data through sockets
        read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        write = new PrintWriter(socket.getOutputStream(), true);

        // while loop - processing protocol-messages from the server
        while (true) {
            String line = read.readLine();

            // recieve JOIN protocol-message from ChatServer end system, creating client
            if (line.startsWith("JOIN")) {
                write.println(getClientName());
            }

            // recieve J_OK protocol-message from ChatServer end system, acknowledges client
            else if (line.startsWith("J_OK")) {
                txtField.setEditable(true);
            }

            // recieve DATA protocol-message from ChatServer end system, enables writing a message
            else if (line.startsWith("DATA")) {
                txtArea.append(line.substring(8) + "\n");
            }

            // recieve J_ERROR_ILLCHAR protocol-message from ChatServer end system, prompts cient for different username
            else if (line.startsWith("J_ERROR_ILLCHAR")) {
                write.println("Illegal characters in username, please try again");
                write.println(getClientName());
            }

            // recieve J_ERROR_EXIUSER protocol-message from ChatServer end system, prompts cient for different username
            else if (line.startsWith("J_ERROR_EXIUSER")) {
                write.println("Username already exists, please try again");
                write.println(getClientName());
            }
        }
    }
}
