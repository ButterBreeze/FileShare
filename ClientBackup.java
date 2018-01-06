import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ClientTest {

	static Socket connection;
	static File errorFile;
	static PrintWriter pw;

	public static void main(String[] args) throws UnknownHostException, IOException {
		try {
			errorFile = new File("C:\\ProgramData\\ErrorLog.txt");
			// errorFile = new File("/home/tally/ProgramData/BackUpInfo/ErrorLog.txt");
			pw = new PrintWriter(errorFile);
			connection = new Socket("windy", 2048);// change the windy to your server's name
			DataOutputStream output;
			DataInputStream input;
			String computerName;

			List<String> pathsList = new ArrayList<String>();

			input = new DataInputStream(connection.getInputStream());
			output = new DataOutputStream(connection.getOutputStream());

			Scanner pathsFile = new Scanner(new File("C:\\ProgramData\\BackUpFile.txt"));
			// Scanner pathsFile = new Scanner(new
			// File("/home/tally/ProgramData/BackUpInfo/BackUpFile.txt"));

			pathsFile.useDelimiter("\r\n");
			while (pathsFile.hasNextLine()) {
				pathsList.add(pathsFile.nextLine());
			}
			pathsFile.close();
			computerName = getComputerName();
			System.out.println(computerName);

			System.out.println("Waiting in queue...");
			
			input.readChar();

			output.writeChar('k');
			System.out.println("Out of queue");
			output.flush();

			//server places all files into a file that is this computers name on the desktop in meh
			output.writeBytes(computerName + "\n");
			output.flush();

			//send all the dirs we got from the file above
			sendPaths(pathsList, computerName);

			output.close();
			input.close();
			connection.close();
		} catch (Exception e) {
			
			//all uncaught errors will be caught here
			//this will write to a file in ProgramData in C:/
			//and will also output to cmd i
			e.printStackTrace();
			e.printStackTrace(pw);
			pw.close();
			System.exit(1);
		}
	}

	public static void sendPaths(List<String> pathsList, String computerName) throws IOException {
		DataOutputStream output;
		//The reason this and current path is separated
		//is that if we have multiple file names inside the 
		//what to backup file, it can iterate through them
		//without it being messy
		String runningPath = computerName;
		output = new DataOutputStream(connection.getOutputStream());
		System.out.println("Sending paths");
		for (int i = 0; i < pathsList.size(); i++) {
			//this for loop goes through the files that we collected from the backup file
			//current path is the path that we have
			//so if we entered Documents/pdfs
			//and open another folder, that folder's name
			//will be tacked onto that, and when we exit
			//that folder it will go back to Documents/pdfs
			String currentPath = pathsList.get(i);
			boolean checkResult = checkFileOrFolder(currentPath);
			
			int whatToExpect = 0; // 5 is file 7 is folder
			if (checkResult) {
				whatToExpect = 7;
				output.writeInt(whatToExpect);
				output.flush();
				sendFolder(currentPath, runningPath);
			} else {
				whatToExpect = 5;
				output.writeInt(whatToExpect);
				output.flush();
				sendFile(currentPath, runningPath);
			}

		}
		output.writeInt(4);// for the loop is done
		output.flush();
	}

	private static boolean checkFileOrFolder(String currentPath) {
		File file = new File(currentPath);

		// boolean exists = file.exists(); // Check if the file exists
		boolean isDirectory = file.isDirectory(); // Check if it's a directory
		boolean isFile = file.isFile(); // Check if it's a regular file

		if (isDirectory)
			return true;
		if (isFile)
			return false;
		return false;

	}

	public static void sendFile(String filePath, String runningPath) throws IOException {
		System.out.println("Sending file");
		File filetbs = new File(filePath);
		DataInputStream input;
		DataOutputStream output;
		input = new DataInputStream(connection.getInputStream());
		output = new DataOutputStream(connection.getOutputStream());
		output.writeBytes(runningPath + "\\" + "\n");
		System.out.println(runningPath + "\\");
		// output.writeBytes(runningPath +"/" + "\n");
		// System.out.println(runningPath +"/"+ "\n");

		output.flush();
		output.writeBytes(filetbs.getName() + "\n");
		System.out.println(filetbs.getName());
		output.flush();
		input.readByte();
		Socket socket3 = new Socket("windy", 14587);

		output.writeByte('k');

		FileInputStream fos = new FileInputStream(filetbs);
		BufferedInputStream in = new BufferedInputStream(fos);
		DataOutputStream out = new DataOutputStream(socket3.getOutputStream());

		byte[] buf = new byte[1024];
		int bytesRead = 0;

		try {

			while (-1 != (bytesRead = in.read(buf, 0, buf.length))) {
				out.write(buf, 0, bytesRead);

			}

		} catch (IOException e) {

			e.printStackTrace(pw);
			pw.close();
			System.exit(1);
		} finally {
			try {
				out.flush();
				out.close();
				in.close();
				socket3.close();

			} catch (Exception e) {
				e.printStackTrace(pw);
				pw.close();
				System.exit(1);
			}
		} // Ignore
		output.writeByte('k');
		input.readByte();

	}

	public static String sendFolder(String path, String runningPath) throws IOException {
		DataOutputStream output;
		DataInputStream input;
		input = new DataInputStream(connection.getInputStream());
		output = new DataOutputStream(connection.getOutputStream());
		String folderName = getFolderName(path);
		runningPath += folderName;
		output.writeBytes(runningPath + "\n");
		output.flush();
		input.readByte();

		enterFolder(path, runningPath);

		return runningPath;

	}

	public static void enterFolder(String path, String runningPath) throws IOException {
		System.out.println("entering folder:" + path);
		// opens folder
		File f = new File(path);
		// get an array of the stuff inside
		File[] list = f.listFiles();
		DataOutputStream output;
		output = new DataOutputStream(connection.getOutputStream());

		// loops through what is inside breaks here with nullPointer because
		// list uninitialized
		for (int i = 0; i < list.length; i++) {

			String currentPath = list[i].getPath();
			boolean checkResult = checkFileOrFolder(currentPath);

			int whatToExpect = 0; // 5 is file 7 is folder
			if (checkResult) {
				whatToExpect = 7;
				output.writeInt(whatToExpect);
				output.flush();
				sendFolder(currentPath, runningPath);
			} else {
				whatToExpect = 5;
				output.writeInt(whatToExpect);
				output.flush();
				sendFile(currentPath, runningPath);
			}
		}

	}

	public static String getFolderName(String path) {
		String folderName = "";
		int index = path.lastIndexOf("\\");
		// int index = path.lastIndexOf("/");
		folderName = path.substring(index);

		return folderName;
	}

	public static String getComputerName() {
		Map<String, String> env = System.getenv();
		if (env.containsKey("COMPUTERNAME"))
			return env.get("COMPUTERNAME");
		else if (env.containsKey("HOSTNAME"))
			return env.get("HOSTNAME");
		else
			return "Unknown Computer";
	}

}
