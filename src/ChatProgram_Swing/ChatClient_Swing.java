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

    /** Textfields and Frames */
    private BufferedReader read;
    private PrintWriter write;
    private JFrame frame = new JFrame("ChatWindow");
    private JTextField txtField = new JTextField(40);
    private JTextArea txtArea = new JTextArea(8, 40);


    /** MAIN - Runs the client */
    public static void main(String[] args) throws IOException {
        ChatClient_Swing client = new ChatClient_Swing();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.serverConn();
    }

    /** Client Constructor */
    public ChatClient_Swing() {

    /** GUI */
        txtField.setEditable(false);
        txtArea.setEditable(false);
        frame.getContentPane().add(txtField, "North");
        frame.getContentPane().add(new JScrollPane(txtArea), "Center");
        frame.pack();

    /** Listeners */
        txtField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                write.println(txtField.getText());
                txtField.setText("");
            }
        });
    }

    /** Prompts the user for the servers IP address */
    private String promptforServerIP() {
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

    /** Connects to the server program */
    private void serverConn() throws IOException {

        //Initialiing connection
        String serverIP = promptforServerIP();
        Socket socket = new Socket(serverIP, 8080);

        read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        write = new PrintWriter(socket.getOutputStream(), true);

        // while loop processing messages from the server
        while (true) {
            String line = read.readLine();

            if (line.startsWith("SUBMITNAME")) {
                write.println(getClientName());
            }
            else if (line.startsWith("NAMEACCEPTED")) {
                txtField.setEditable(true);
            }
            else if (line.startsWith("MESSAGE")) {
                txtArea.append(line.substring(8) + "\n");
            }
        }
    }
}
