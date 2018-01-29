import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;


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


    /**
     * this will see what the connection is asking the server to do
     *
     * @param socket ~ the socket the computer connects
     *               TODO: connect using username and password
     */
    public static void instructions(Socket socket) {
        PrintWriter output = null;
        BufferedReader input = null;

        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);


            output.write("ready");
            String instructions = input.readLine();

            if (instructions == "TabletBackup") { //Backup the tablet files/oasis

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static void startBackupCountdown() {


        Thread backupThread = new Thread() {
            public void run() {
                initializeBackup();
            }
        };
        backupThread.start();
    }

    public static void initializeBackup() {


        int untilBackupMills = getMillsToMidnight();
        ActionListener actionListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                doBackup();
            }
        };

        Timer timer = new Timer(untilBackupMills, actionListener);
        timer.start();

    }//end method

    public static void doBackup() {


    }

    /**
     *
     * @return time till midnight, returns int because int will be able to handle the amount we need
     *              (upto 24 days which is past midnight from anytime)
     */
    public static int getMillsToMidnight(){


        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return  (int) (c.getTimeInMillis()-System.currentTimeMillis());

    }

}//end class
