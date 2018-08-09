|  \/  |  \/  |/ __ \|  __ \|  __ \ / ____| /_ |/_ |
| \  / | \  / | |  | | |__) | |__) | |  __   | | | |
| |\/| | |\/| | |  | |  _  /|  ___/| | |_ |  | | | |
| |  | | |  | | |__| | | \ \| |    | |__| |  | |_| |
|_|  |_|_|  |_|\____/|_|  \_\_|     \_____|  |_(_)_|


Authored by:
    Sean Beecroft

Contributors:
    Mike Sebele

Copyright (c) 2012.

Contact:
    seanbeecroft@gmail.com

Running Instructions:

    A pre-configured default run script called run.sh is included. By default the application will
    run on linux.

    run as (windows):    
    java -classpath "resolver.jar;xercesImpl.jar;serializer.jar;xml-apis.jar;mysql.jar" -jar MMORPG.jar

    run as (linux)
    java -classpath "resolver.jar:xercesImpl.jar:serializer.jar:xml-apis.jar:mysql.jar" -jar MMORPG.jar

To run the program in the background on linux, I suggest

nohup java -classpath "resolver.jar:xercesImpl.jar:serializer.jar:xml-apis.jar:mysql.jar" -jar MMORPG.jar &

Installation:
    A pre-configured install script called install.sh is included. By default, the application will 
    install Small SQL.
    
    If you wish to run mysql, you must manually run the install script, using:
    java -jar MMORPG.jar mmorpg.core.Installer -install ./sql/create-mysql.sql

    You will also need to modify configure.properties to supply your database connection information.

Information:
    default port is 4000.

    commands are available by typing 'help'

Note:
    Unfortunately, due to changes to the dynamic class loading which was added in the last build,
    you may not be able to run the application from a sealed jar. If this is the case, you will need to
    run the server by running the main class, "mmorpg.core.Server" directly.

    You will NEED to supply your own mysql driver in the above line as I don't know which driver you are using.

This application requires the program to be run with the following 
jars on the class path:
 * resolver.jar
 * xercesImpl.jar
 * serializer.jar
 * xml-apis.jar
 * mysql driver


  For more information on this project and others, please visit my google code 
  repository:
  https://code.google.com/u/seanbeecroft@gmail.com/
