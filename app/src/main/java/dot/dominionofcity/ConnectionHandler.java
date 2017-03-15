package dot.dominionofcity;

import android.content.SharedPreferences;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.Scanner;

public class ConnectionHandler {
    private String sessionId;
    private SharedPreferences sharedPreferences;
    public static final String SESSION_KEY = "SessionID";
    private HttpURLConnection connection;

    public ConnectionHandler(SharedPreferences sharedPreferences, URLConnection connection) {
        this.sharedPreferences = sharedPreferences;
        this.connection = (HttpURLConnection) connection;
    }

    public ConnectionHandler useSession() {
        sessionId = sharedPreferences.getString(SESSION_KEY, "");
        connection.addRequestProperty("Cookie", sessionId);
        return this;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public String post(String data) throws IOException {
        return post(data.getBytes());
    }

    public String post(byte[] data) throws IOException {
        //set method
        connection.setRequestMethod("POST");
        //output data
        connection.setDoOutput(true);
        OutputStream os = connection.getOutputStream();
        os.write(data);
        os.close();
        if (null != sessionId)
            storeSession();
        connection.disconnect();
        return getResponse();
    }

    public String get() throws IOException {
        connection.setRequestMethod("GET");
        connection.connect();
        if (null != sessionId)
            storeSession();
        connection.disconnect();
        return getResponse();
    }

    //get session
    private void storeSession() {
        if (null != connection.getHeaderField("Set-Cookie")) {
            String[] sc =
                    connection.getHeaderField("Set-Cookie").split(";");
            if (sc.length > 0 && !sessionId.equals(sc[0])) {
                sessionId = sc[0];
                if (null != sharedPreferences) {
                    sharedPreferences.edit()
                            .putString(SESSION_KEY, sessionId)
                            .apply();
                }
            }
        }
    }

    //receive response
    private String getResponse() throws IOException {
        InputStream is = connection.getInputStream();
        Scanner scanner = new Scanner(is);
        StringBuilder response = new StringBuilder();
        while (scanner.hasNextLine()) {
            response.append(scanner.nextLine()).append("\n");
        }
        scanner.close();
        is.close();
        return response.toString();
    }
}
