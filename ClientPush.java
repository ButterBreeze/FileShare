import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientPush {

    /**
     * TODO: have dev password
     *
     * @param args
     */
    public static void main(String[] args) {
        while (true) {
            try {
                PrintWriter output;
                BufferedReader input;
                ServerSocket serverSocket = new ServerSocket(16796);
                Socket socket = null;


                socket = serverSocket.accept();
                output = new PrintWriter(socket.getOutputStream(), true);
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String PASSWORD = "asdfgh";
                if (input.readLine().equals(PASSWORD)) {
                    output.write("PasswordIncorrect");
                    return;
                }

                if (input.readLine().equals("recieveFile"))
                    getFile(socket, output, input);
                else if (input.readLine().equals("sendFile"))
                    sendFile(socket, output, input);
            } catch (IOException e) {

                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

    }


    public static void getFile(Socket socket, PrintWriter output, BufferedReader input) throws Exception {
        String fileLocation = input.readLine();
        String folderLocation = "";
        for (int i = fileLocation.length() - 1; i > 0; i--) {
            if (fileLocation.charAt(i) == '\\') {
                if (System.getProperty("os.name").equals("Linux")) {
                    output.write("Wrong os");
                    return;
                }//end os check
                folderLocation = fileLocation.substring(0, i);
                Path path = Paths.get(folderLocation);
                if (!(Files.exists(path))) {
                    boolean success = new File(folderLocation).mkdirs();
                    System.out.println(success);
                }
                writeFile(fileLocation, socket);


            } else if (fileLocation.charAt(i) == '/') {
                if (!System.getProperty("os.name").equals("Linux")) {
                    output.write("Wrong os");
                    return;
                }//end os check

                folderLocation = fileLocation.substring(0, i);
                Path path = Paths.get(folderLocation);
                if (!(Files.exists(path))) {
                    boolean success = new File(folderLocation).mkdirs();
                    System.out.println(success);
                }
                writeFile(fileLocation, socket);
            }
        }
    }

    public static void sendFile(Socket socket, PrintWriter output, BufferedReader input) throws Exception {

    }

    public static void writeFile(String fileLocation, Socket socket) throws Exception {
        File file = new File(fileLocation);
        DataOutputStream output;
        DataInputStream input;
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());
        ServerSocket ss2 = new ServerSocket(14587);
        output.writeByte('k');
        Socket socket2 = ss2.accept();
        input.readByte();
        output.flush();
        FileOutputStream fos = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        DataInputStream in = new DataInputStream(socket2.getInputStream());
        DataOutputStream out = new DataOutputStream(bos);

        byte[] buf = new byte[1024];
        int bytesRead = 0;

        try {

            while (-1 != (bytesRead = in.read(buf, 0, buf.length))) {
                out.write(buf, 0, bytesRead);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.flush();
                out.close();
                in.close();
                ss2.close();
                socket2.close();

            } catch (Exception e) {
                e.printStackTrace();
            } // Ignore
        }

    }


}
