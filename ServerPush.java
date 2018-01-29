

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ServerPush {

    public static void main(String[] args) {

        if (args[0].equals("h")) {
            System.exit(0);
        }

        String fileLocation = args[0];
        String whereFileIsGoing = args[1];
        File file = new File(fileLocation);
        List<String> comps = new ArrayList();
        if (!file.exists() || !file.isDirectory()) {
            System.out.println("Please enter a valid file, enter h for help");
            System.exit(0);
        }
        try {

            if (System.getProperty("os.name").contains("Windows")) {
                Scanner compNames = new Scanner(new File("C:/Users/Tallennar/Desktop/compNames.txt"));
                while (compNames.hasNextLine())
                    comps.add(compNames.nextLine());
            } else {
                Scanner compNames = new Scanner(new File("/home/tally/Desktop/compNames.txt"));
                while (compNames.hasNextLine())
                    comps.add(compNames.nextLine());
            }

        } catch (FileNotFoundException e) {
            System.out.println("failed to read in names");
            e.printStackTrace();
            System.exit(1);
        }

        Socket socket;

        for (int i = 0; i < comps.size(); i++) {
            try {
                socket = new Socket(comps.get(i), 16796);
                DataOutputStream output;
                DataInputStream input;
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());

                output.writeChars(whereFileIsGoing + "\n");
                if (input.readChar() == 'E') {
                    System.out.println("Location does not exist in specified location");
                    System.exit(0);
                }

                output.writeBytes(file.getName() + "\n");
                output.flush();
                input.readByte();
                FileInputStream fos = new FileInputStream(file);
                BufferedInputStream in = new BufferedInputStream(fos);
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

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
                        socket.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                } // Ignore
            } catch (IOException e) {
                System.out.println("failed to connect to host");
                System.exit(2);
                e.printStackTrace();
            }
        }

    }

}
