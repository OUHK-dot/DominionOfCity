package dot.dominionofcity;

import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

/**
 * This class implement HttpURLConnection to perform request and get back response
 * To send GET request with query string, construct an instance with String url
 * and call get(String query) without "?" at the beginning.
 * To send POST request or GET request without dynamic query string, construct with
 * URL or URL.openConnection() and call get(), post(), post(String data).
 * Note: Connection can only be used once
 * Note: Connection cannot be establish on Main Thread. Child thread should be used.
 *
 * Example:
 public boolean send2() {
    ConnectionHandler handler;
    try {
        //connect url
        handler = new ConnectionHandler(url.openConnection()) //use pre-set URL)
            .useSession(getSharedPreferences("TEST", MODE_PRIVATE)); //set session available, use sharedPreferences to store it
        String response = handler.post("messages=sth&name=someone");
        Log.i("STH", response);
        if (response.equals("{status:OK}")) return true;
    } catch (java.io.IOException ignored) {}
    return false;
 }
 */

public class ConnectionHandler {
    private String url;
    private String sessionId;
    private SharedPreferences sharedPreferences;
    public static final String SESSION_KEY = "SessionID";
    private HttpURLConnection connection;
    private static final String TAG = "Internet";

    public ConnectionHandler(String url) {
        this.url = url;
    }

    public ConnectionHandler(URL url) throws IOException {
        this(url.openConnection());
    }

    public ConnectionHandler(URLConnection connection) {
        this.connection = (HttpURLConnection) connection;
    }

    public ConnectionHandler useSession(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        sessionId = sharedPreferences.getString(SESSION_KEY, "");
        if (null != connection)
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
        Log.v(TAG, connection.getURL().toString());
        Log.v(TAG, "POST-> " + new String(data));
        //set method
        connection.setRequestMethod("POST");
        //output data
        connection.setDoOutput(true);
        OutputStream os = connection.getOutputStream();
        os.write(data);
        os.close();
        if (null != sessionId)
            storeSession();
        return getResponse();
    }

    public String get(String query) throws IOException {
        connection = (HttpURLConnection) new URL(url + "?" + query).openConnection();
        connection.addRequestProperty("Cookie", sessionId);
        return get();
    }

    public String get() throws IOException {
        Log.v(TAG, connection.getURL().toString());
        connection.setRequestMethod("GET");
        connection.connect();
        if (null != sessionId)
            storeSession();
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
        Log.v(TAG, response.toString());
        return response.toString();
    }
}
