import com.sun.corba.se.spi.activation.Server;

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
public class AltChatClient {

    private BufferedReader read;
    private PrintWriter write;
    private JFrame frame = new JFrame("ChatWindow");
    private JTextField txtfield = new JTextField(40);
    private JTextArea messageArea = new JTextArea(8, 40);



    public static void main(String[] args) throws IOException {
        AltChatClient client = new AltChatClient();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.serverConn();
    }



    public AltChatClient() {

    /** GUI */

    txtfield.setEditable(false);
    messageArea.setEditable(false);
    frame.getContentPane().add(txtfield, "North");
    frame.getContentPane().add(new JScrollPane(messageArea), "Center");
    frame.pack();

    txtfield.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            write.println(txtfield.getText());
            txtfield.setText("");
        }
    });
    }

    private String promptforServerIP() {
        return JOptionPane.showInputDialog(frame,
                "Enter IP Address of the server:",
                "Welcome to the Chat Client",
                JOptionPane.QUESTION_MESSAGE
        );
    }

    private String getClientName() {
        return JOptionPane.showInputDialog(frame,
                "Choose a chat name:",
                "Screen name selection",
                JOptionPane.PLAIN_MESSAGE
        );
    }

    private void serverConn() throws IOException {

        String serverIP = promptforServerIP();
        Socket socket = new Socket(serverIP, 8080);

        read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        write = new PrintWriter(socket.getOutputStream(), true);

        while (true) {

            String line = read.readLine();

            if (line.startsWith("SUBMITNAME")) {
                write.println(getClientName());
            }
            else if (line.startsWith("NAMEACCEPTED")) {
                txtfield.setEditable(true);
            }
            else if (line.startsWith("MESSAGE")) {
                messageArea.append(line.substring(8) + "\n");
            }
        }
    }
}
