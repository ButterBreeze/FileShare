#FileShare
(note only works on lan/wlan and default is Windows)

This is is a few java classes that make a project.
Starting with the server, it waits for the clients to connect.
After the clients connect it starts the backup by getting the computer name.
The server then saves the files under the computer name and saves the computer name
in a textfile for later use.
The second part is a way to request a file from a computer. You need to know the
computer name. The great part about this is you can push a file to all the
computers that are in the text file saved on the server. As of right now
you need to know the exact path to the file for this to work and access 
to where you want to place that file.


For the Backup part:

the computer that you want to store the backups should run the ServerBackup
for serverbackup, if you are on linux there is commented out lines of code that you uncomment and the lines
that are above or below ( they should say the same stuff but contain a different path or a / instead of a \)
comment them out.
To change where you want this stored there are a few strings that contain a path that you need to change and you need to change the user Ex: C://Users/THISPART/Desktop/BackedUpStuffs to yours. You don't need to but for the minimum amount of changes to get this to work that part is needed. The default is that the files are stored on the desktop under BackedUpStuffs with the computer that sent them. There is a wait time on the socket manager. So if there is no connections it sleeps for 5 mins and then checks if there are any. If none have come in it will got to sleep. This allows the computers who are off by a min or two not to miss the backup.
So far the server can hold 100 connections.

The client side
You need a file in C:/ProgramData that is called BackupFile.txt that contains all the files that you want to backup. So to backup the Documents folder you would place in there without the quotes "C:/Users/THEUSER/Documents". For every entry start a new line.
If there is any error in the program it will write the stack trace to C:/ProgramData/ErrorLog.txt.
You will need to change the socket connection to the server name/ip (for dynamic ip i would go with the name). Note that there are 2 sockets one for the main and one for the file transfer MAKE SURE TO CHANGE BOTH otherwise server will listen and client will crash because there is no server to connect to. 
The filetransfer part coming soon
