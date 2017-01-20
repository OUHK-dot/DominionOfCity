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

public class HttpPostTask extends AsyncTask<String, String, String> {
    private String url = "";

    public HttpPostTask(String url) {
        super();
        this.url = url;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... queryStrings) {
        try {
            //connect url
            URL url = new URL(this.url);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            //send data
            os.write(queryStrings[0].getBytes());
            os.close();
            //receive response
            InputStream is = conn.getInputStream();
            Scanner scanner = new Scanner(is);
            StringBuilder sb = new StringBuilder();
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine() + "\n");
            }
            scanner.close();
            //onPostExecute(response)
            return sb.toString();

        }
        catch (IOException e) {}
        return null;
    }

    @Override
    protected void onProgressUpdate(String... params) {
        super.onProgressUpdate(params);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}
