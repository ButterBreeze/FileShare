import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientPush {

    public static void main(String[] args) {
        try {
            DataOutputStream output;
            DataInputStream input;
            ServerSocket serverSocket = new ServerSocket(16796);
            Socket socket = null;
            socket = serverSocket.accept();
            output = new DataOutputStream(socket.getOutputStream());
            input = new DataInputStream(socket.getInputStream());

            String fileLocation = input.readLine();
            String folderLocation = "";
            for (int i = fileLocation.length(); i > 0; i++) {
                if (fileLocation.charAt(i) == '\\') {

                }
            }

            File file = new File(fileLocation);

        } catch (IOException e) {

            e.printStackTrace();
        }

    }

}
