package ro.pub.cs.systems.eim.practicaltest02.Threads;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import ro.pub.cs.systems.eim.practicaltest02.InformationClass;
import ro.pub.cs.systems.eim.practicaltest02.Utilities;

public class CommunicationThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e("[PracticalTest02]", "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e("[PracticalTest02]", "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            Log.i("[PracticalTest02]", "[COMMUNICATION THREAD] Waiting for parameters from client (IP)");
            String ip = bufferedReader.readLine();
            if (ip == null || ip.isEmpty()) {
                Log.e("[PracticalTest02]", "[COMMUNICATION THREAD] Error receiving parameters from client (city / information type!");
                return;
            }

            Log.i("[PracticalTest02]", "[COMMUNICATION THREAD] Getting the information from the webservice...");
            HttpClient httpClient = new DefaultHttpClient();
            String pageSourceCode = "";

            String sign = "/";
            if (ip.equals("myip")) {
                sign = "?";
            }

            HttpGet httpGet = new HttpGet("https://api.ipgeolocationapi.com/geolocate" + sign + ip);
            HttpResponse httpGetResponse = httpClient.execute(httpGet);
            HttpEntity httpGetEntity = httpGetResponse.getEntity();
            if (httpGetEntity != null) {
                pageSourceCode = EntityUtils.toString(httpGetEntity);
            }


            if (pageSourceCode == null) {
                Log.e("[PracticalTest02]", "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                return;
            } else
                Log.i("[PracticalTest02]", pageSourceCode );


            JSONObject content = new JSONObject(pageSourceCode);

//            JSONArray weatherArray = content.getJSONArray(Constants.WEATHER);
//            JSONObject weather;
//            String condition = "";
//            for (int i = 0; i < weatherArray.length(); i++) {
//                weather = weatherArray.getJSONObject(i);
//                condition += weather.getString(Constants.MAIN) + " : " + weather.getString(Constants.DESCRIPTION);
//
//                if (i < weatherArray.length() - 1) {
//                    condition += ";";
//                }
//            }

            String countryCode = content.getString("alpha2");
            String countryName = content.getString("name");
            String continentName = content.getString("continent");

            JSONObject geo = content.getJSONObject("geo");
            String latitude = geo.getString("latitude");
            String longitude = geo.getString("longitude");

            InformationClass information = new InformationClass(
                    countryName, continentName, countryCode, Double.parseDouble(latitude), Double.parseDouble(longitude)
            );

            if (information == null) {
                Log.e("[PracticalTest02]", "[COMMUNICATION THREAD] Weather Forecast Information is null!");
                return;
            }
            String result = information.toString();

            printWriter.println(result);
            printWriter.flush();
        } catch (IOException ioException) {
            Log.e("[PracticalTest02]", "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (true) {
                ioException.printStackTrace();
            }
        } catch (JSONException jsonException) {
            Log.e("[PracticalTest02]", "[COMMUNICATION THREAD] An exception has occurred: " + jsonException.getMessage());
            if (true) {
                jsonException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e("[PracticalTest02]", "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (true) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }

}
