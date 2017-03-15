package dot.dominionofcity;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Scanner;

/**
 * Created by dixon on 20/1/2017.
 */

public abstract class HttpTask extends AsyncTask<String, Void, Boolean> {
    private String url = "";
    private int method;
    public static final int GET = 0;
    public static final int POST = 1;

    private StringBuilder response;

    public HttpTask(String url) {
        this(url, GET);
    }

    public HttpTask(String url, int method) {
        super();
        this.url = url;
        this.method = method;
        response = new StringBuilder();
    }

    @Override
    protected Boolean doInBackground(String... queryStrings) {
        try {
            URL url;
            InputStream is;
            //connect url
            if (method == POST) {
                url = new URL(this.url);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                //send data
                OutputStream os = conn.getOutputStream();
                os.write(queryStrings[0].getBytes());
                os.close();
                //receive response
                is = conn.getInputStream();
            }
            else {
                url = new URL(this.url + "?" + queryStrings[0]);
                //receive response
                is = url.openStream();
            }
            Scanner scanner = new Scanner(is);
            while (scanner.hasNextLine()) {
                response.append(scanner.nextLine() + "\n");
            }
            scanner.close();
            doMoreInBackground();
            publishProgress((Void) null);
            //onPostExecute(response)
            return true;

        }
        catch (IOException ignored) {}
        return false;
    }

    //extra task to do in background
    protected void doMoreInBackground() {}
}
