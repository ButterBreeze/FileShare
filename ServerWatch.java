import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerWatch {

    public static void main(String[] args) {
        try {
            ServerSocket instructionServerSocket = new ServerSocket(2048);
            instructionServerSocket.accept();
            Socket instructionSocket = null;

            while (true) {

                instructionSocket = instructionServerSocket.accept();
                socketConnection(instructionSocket);

            }


        } catch (Exception ex) {
            System.exit(-1);
        }//end main loop
    }//end main

    /**
     * Purpose: this will see what the connection needs, and then opens a thread for it
     *
     * @param socket ~ the socket that will be dealt with. will open a thread
     *               and kill the socket
     */
    public static void socketConnection(Socket socket) {
        Thread instructionThread = new Thread() {
            public void run() {
                instructions(socket);
            }
        };

        instructionThread.start();
    }

    public static void instructions(Socket socket) {
        PrintWriter output = null;
        BufferedReader input = null;

        try{
            input = new BufferedReader( new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);




        output.write("ready");
        String instructions = input.readLine();

        }catch(Exception ex){
            ex.printStackTrace();
        }

    }


}//end class
