import java.io.*;
import java.net.Socket;

public class ServerPush {

    static String computerName;
    public static void main(String[] args) {
        try {
            if (args.length == 1) {
                if (args[0].equals("h")) {
                    System.out.println("Please enter 3 arguments, the location of the file, the computer name, and" +
                            "the location of where it is supposed to go, the later is os specific so us different syntax for " +
                            "Windows and Unix based operating systems.");
                    System.out.println("Or please enter a -g to get a file from a computer, where it is on that computer, " +
                            "the computer name, and where you want to put it on your computer.");
                } else {
                    System.out.println("Please enter the correct arguments enter -h to get help");
                    System.exit(0);
                }
            } else if (args.length == 3) {
                computerName = args[1];
                sendFile(args[0], args[2]);

            } else if (args.length == 4) {
                computerName=args[2];
                receiveFile(args[1], args[0]);

            } else {
                System.out.println("Please enter the correct arguments enter -h to get help");
                System.exit(0);
            }

        } catch (Exception ex) {

        } finally {

        }


    }


    public static void sendFile(String internalLocation, String externalLocation) throws Exception {

        Socket socket = new Socket(computerName, 16796);
        PrintWriter output;
        BufferedReader input;

        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintWriter(socket.getOutputStream());

        output.write("asdfgh");
        output.write("recieveFile");
        output.write(externalLocation);

        if(input.readLine().equals("Wrong os")){
            System.out.println("Wrong path os wise in other computer please try again");
            System.exit(0);
        }
        sendData(socket, internalLocation);




    }

    public static void receiveFile(String externalLocation, String internalLocation) {

    }

    public static void sendData(Socket socket, String filePath) throws Exception{
        File filetbs = new File(filePath);
        DataInputStream input;
        DataOutputStream output;
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());
        input.readByte();
        Socket socket2 = new Socket(computerName, 14587);

        output.writeByte('k');

        FileInputStream fos = new FileInputStream(filetbs);
        BufferedInputStream in = new BufferedInputStream(fos);
        DataOutputStream out = new DataOutputStream(socket2.getOutputStream());

        byte[] buf = new byte[1024];
        int bytesRead = 0;


        try {

            while (-1 != (bytesRead = in.read(buf, 0, buf.length))) {
                out.write(buf, 0, bytesRead);

            }

        } catch (IOException e) {

            e.printStackTrace();
            System.exit(1);
        } finally {
            try {
                out.flush();
                out.close();
                in.close();
                socket2.close();

            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        } // Ignore
        output.writeByte('k');
        input.readByte();

    }


}
