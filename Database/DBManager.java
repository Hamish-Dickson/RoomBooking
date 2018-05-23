package Database;

import ClientModule.CustomAlert;

import java.io.IOException;


/**
 * Class that manages the switching on and off of the database
 *
 * @author Hamish Dickson
 */
public class DBManager {
    private Process launch;
    private int connectionTries = 0;

    /**
     * Starts up the database
     */
    public void startup() {
        String fp = System.getProperty("user.dir") + "/libs/mySQL/mysql-5.7.21-winx64/bin/mysqld.exe";//relative directory for the executable file
        try {
            Runtime runtime = Runtime.getRuntime();
            launch = runtime.exec("cmd.exe /c start " + fp);//add arg  "--console" to see console for this process. debugging.
        } catch (IOException e) {
            if (connectionTries == 0) {
                CustomAlert.alert(4, e);
            }
            connectionTries += 1;
            if (connectionTries >= 3) {
                CustomAlert.alert(5, e);
                return;
            }
            startup();

        }
    }

    /**
     * Shuts down the database
     */
    public void shutdown() {
        String shutdownCmd = "./libs/MySQL/mysql-5.7.21-winx64/bin/mysql.exe" +
                " --user=root --password=root --database=\"roombooking\" --execute=\"SHUTDOWN;\"";
        try {
            Runtime.getRuntime().exec(shutdownCmd);
        } catch (IOException e) {
            CustomAlert.alert(4, e);
        }
        launch.destroy();
    }
}
