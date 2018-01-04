#FileShare
(note only works on lan/wlan and Windows as of now)

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
