import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

public class ServerBackup extends Thread {

	static int activeConnectionNumber = 0;
	static boolean[] connectionOpen = new boolean[100];
	static Socket[] connections = new Socket[100];
	static File errorFile;
	static PrintWriter pw;

	public static void main() throws IOException {
		try {

			// The error file that the computer writes to
			errorFile = new File("C:\\ProgramData\\ErrorLog.txt");
			// commented out for linux
			// errorFile = new File("/home/tally/ProgramData/BackUpInfo/ErrorLog.txt");
			pw = new PrintWriter(errorFile);
			// to please ide that loop will exit
			boolean loop = true;

			ServerSocket serverSocket;
			serverSocket = new ServerSocket(2048);
			Socket socket = null;



			// sets all sockets to false and in future can add more stuff there
			init();

			// first connection and starts worker thread
			startInitialConnection(serverSocket);

			// this thread just Listens for connections and then adds them to the socket
			// list
			while (true) {

				socket = serverSocket.accept();
				initializeConnection(socket, false);

				if (loop == false) {
					break;
				}
			}

			serverSocket.close();
		} catch (Exception e) {
			// any error not handled get caught here, writes to a file in ProgramData
			// and closes the system, if in cmd/terminal will also output there

			e.printStackTrace();
			e.printStackTrace(pw);
			pw.close();
			System.exit(1);
		}
	}

	public static void init() {
		for (int i = 0; i < 100; i++)
			connectionOpen[i] = false;

	}

	public static void startInitialConnection(ServerSocket serverSocket) throws IOException {

		// This is where this gets fun Socket is accepted and assigned a number
		// then start a thread to recieve all the files/folders from a client
		// then all the other clients wait to send until they recieve something

		Socket socket = serverSocket.accept();
		initializeConnection(socket, true);

		// start new thread/ the main of the new thread.
		Thread copyThread = new Thread() {
			public void run() {
				try {
					while (true) {

						// will loop until the all connections are closed
						// and 5 mins of no new connections that number can be
						// changed in connectionManager
						serverThread(activeConnectionNumber);
						connectionManager(activeConnectionNumber, false);
					}

				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
					e.printStackTrace(pw);
					pw.close();
					System.exit(1);
				}
			}
		};

		copyThread.start();
	}

	public static void initializeConnection(Socket newConnection, boolean firstConnection) {
		// this will assign a number to the thread
		// since vars are global doesn't return anything
		int newConnectionNumber = 0;

		// checks for first connection otherwise it will throw a null exception error
		// Because activeConnectionNumber is set to 0 uptop. the socket is written to
		// array
		// number 1 and active connectionNumber stays at 0 this is because this is
		// a socket initalizer not a socket manager thats lower.
		if (!firstConnection) {
			for (int i = activeConnectionNumber + 1; i < 100; i++) {
				if (i == 100) {
					i = -1;
				}
				if (connectionOpen[i] == false) {
					newConnectionNumber = i;
					break;
				} else if (i == activeConnectionNumber) {
					activeConnectionNumber = -1;
					break;
				}
			}
		}

		if (activeConnectionNumber == -1) {
			activeConnectionNumber = 0;
			System.out.println("Client Connected, Connection: " + activeConnectionNumber);
			connections[activeConnectionNumber] = newConnection;
			connectionOpen[activeConnectionNumber] = true;

		} else {
			connectionOpen[newConnectionNumber] = true;
			System.out.println("Client Connected, Awaiting turn in queue Connection: " + newConnectionNumber);
			connections[newConnectionNumber] = newConnection;
		}

	}

	@SuppressWarnings("deprecation")
	public static void serverThread(int connectionNumber) throws IOException, InterruptedException {
		DataOutputStream output;
		DataInputStream input;
		String computerName;
		int whatToExpect = 0; // 5 is file 7 is folder




		input = new DataInputStream(connections[connectionNumber].getInputStream());
		output = new DataOutputStream(connections[connectionNumber].getOutputStream());
		System.out.println("Serving Client Number:" + connectionNumber);

		// all waiting clients wait for this to start sending stuff
		// so this is the goAhead part
		char goAhead = 'k';
		output.writeChar(goAhead);

		input.readChar();

		computerName = input.readLine();
        checkComputerName(computerName);
        output.writeChar('k');


		String filePathString = "C:\\Users\\User\\Desktop\\BackedUpStuffs\\" + computerName;
		String runningPath = filePathString;
		new File(filePathString).mkdirs();

		whatToExpect = input.read();
		while (true) {
			if (whatToExpect == 7) {
				runningPath = recieveFolder();
			} else if (whatToExpect == 5) {
				recieveFile(runningPath);
			} else if (whatToExpect == 4) {
				break;
			}
			whatToExpect = input.read();
		}
		connectionManager(connectionNumber, false);

	}

	public static void connectionManager(int connectionNumber, boolean secondTimeThrough)
			throws InterruptedException, IOException {
		// this gets the next socket in the array and if none is available will wait for
		// 5 min then will exit the entire program
		int nextConnection = -1;
		connectionOpen[connectionNumber] = !true; // if the connection is
		connections[connectionNumber].close(); // finished then set the
		// connectionOpen to false
		for (int i = connectionNumber + 1; i < 100; i++) {
			if (i == 100) {
				i = 0;
			}
			if (connectionOpen[i] == true) {
				nextConnection = i;
				break;
			}

			else if (i == connectionNumber) {
				nextConnection = -1;
				break;
			}
		}

		if (nextConnection != -1) {
			connectionOpen[nextConnection] = true;
			activeConnectionNumber = nextConnection;

		} else {
			if (secondTimeThrough == false) {
				Thread.sleep(300000);
				connectionManager(connectionNumber, true);
			} else {
				System.out.println("Server has recieved all connections, exiting program");
				System.exit(0);
			}

		}

	}

	@SuppressWarnings({ "deprecation" })
	public static void recieveFile(String runningPath) throws IOException {
		DataOutputStream output;
		DataInputStream input;
		Socket socket = connections[activeConnectionNumber];
		input = new DataInputStream(socket.getInputStream());
		output = new DataOutputStream(socket.getOutputStream());
		String filePath = input.readLine();
		String fileName = input.readLine();
		File file = new File("C:\\Users\\User\\Desktop\\BackedUpStuffs\\" + filePath + "\\" + fileName);
		// File file = new File("/home/tally/Desktop/BackedUpStuffs/" + filePath + "/" + fileName);
		System.out.println(filePath);
		System.out.println(fileName);
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
			e.printStackTrace(pw);
			pw.close();
			System.exit(1);
		} finally {
			try {
				out.flush();
				out.close();
				in.close();
				ss2.close();
				socket2.close();

			} catch (Exception e) {
				e.printStackTrace();
				e.printStackTrace(pw);
				pw.close();
				System.exit(1);
			} // Ignore
		}
		input.readByte();
		output.writeByte('k');

	}

	@SuppressWarnings("deprecation")
	private static String recieveFolder() throws IOException {
		DataOutputStream output;
		DataInputStream input;
		Socket socket = connections[activeConnectionNumber];
		input = new DataInputStream(socket.getInputStream());
		output = new DataOutputStream(socket.getOutputStream());
		String folderPath = input.readLine();
		String absoluteFolderPath = "C:\\Users\\User\\Desktop\\BackedUpStuffs\\" + folderPath;
		// String absoluteFolderPath = "/home/tally/Desktop/BackedUpStuffs/" + folderPath;
		Path path = Paths.get(absoluteFolderPath);
		if (!(Files.exists(path))) {
			boolean success = new File(absoluteFolderPath).mkdirs();
			System.out.println(success);
		}
		output.flush();
		output.writeByte('k');
		output.flush();
		return folderPath;
	}

	public static void checkComputerName(String computerName){
        String url = "jdbc:mysql://localhost:3306/ihc";
        String username = "root";
        String password = "";

        System.out.println("Connecting database...");
        Connection connection= null;
        try {

            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Database connected!");

            PreparedStatement stmt = connection.prepareStatement("SELECT COMP_NAME FROM COMPUTERS WHERE COMP_NAME = ? ");
            stmt.setString(1, computerName);
            ResultSet rs = stmt.executeQuery();
            if(!rs.next()){
                PreparedStatement stmt2 = connection.prepareStatement("INSERT INTO COMPUTERS (COMP_NAME) VALUES (?)");
                stmt.setString(1, computerName);
            }


        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }catch(Exception ex){

        }

    }

}
