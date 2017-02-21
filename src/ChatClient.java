import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Created by Dave on 21/02/2017.
 */
public class ChatClient {

    private static InetAddress host;
    private static final int PORT = 8080;

    public static void main(String[] args) {

        try {
            host = InetAddress.getLocalHost();
        }
        catch (UnknownHostException uex) {
            System.out.println("Unable to establish connection to host, try again.");
            System.exit(1);
        }
        accessServer();
    }

    private static void accessServer() {

        Socket socket = null;

        try {
            socket = new Socket(host, PORT);

            Scanner read = new Scanner(socket.getInputStream());
            PrintWriter write = new PrintWriter(socket.getOutputStream(), true);

            // set up stream for key entry
            Scanner userInput = new Scanner(System.in);

            String message = "";
            String response = "";



            while (!message.equals("*CLOSE*")) {
                System.out.print("Enter Message: ");
                message = userInput.nextLine();
                write.println(message);

                response = read.nextLine();
                System.out.println("\nSERVER> "+ response);



            }

        }
        catch (IOException ioex) {
            ioex.printStackTrace();
        }
        finally {

            try {
                System.out.println("\n* Closing connection... *");
                socket.close();
            }
            catch (IOException ioex) {
                System.out.println("Unable to disconnect!");
                System.exit(1);
            }
        }
    }
}
