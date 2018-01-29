/**
 * Programmer:  ButterBreeze
 * Date:        Jan/2018
 * Purpose:     To start a backup task every night, wait for incoming connections and deal with them.
 *              Also can push a file to all computers on the list it creates.
 *              TODO: create a file push and get the backup to keep a list of all computers that backed up to it.
 *
 *
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


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

            if (instructions == "DeviceBackup") { //Backup the files sent

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Loops the backup cycle calls initialize backup
     */
    public static void backupLoop() {


        Thread backupThread = new Thread() {
            public void run() {
                initializeBackup();
            }
        };
        backupThread.start();
    }

    /**
     * Creates a scheduled task so that backup starts at midnight
     */
    public static void initializeBackup() {


        long untilBackupMills = getMillsToMidnight();
        final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.schedule(new Runnable() {
            @Override
            public void run() {
                doBackup();

            }
        }, untilBackupMills, TimeUnit.MILLISECONDS);


        ses.shutdown();


    }//end method

    /**
     * Calls the backup of ServerBackup class
     */
    public static void doBackup() {
        try{
            ServerBackup.main();
        }catch(Exception ex){

        }

    }

    /**
     *
     * @return time till midnight, returns int because int will be able to handle the amount we need
     *              (upto 24 days which is past midnight from anytime)
     */
    public static long getMillsToMidnight(){


        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return  (c.getTimeInMillis()-System.currentTimeMillis());

    }

}//end class
