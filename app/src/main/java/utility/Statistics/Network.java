package utility.Statistics;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Shadilan on 23.06.2016.
 */
public class Network {
    private int readTimeout=10000;
    private int conTimeout=10000;
    private static Network instance;
    private Context context;
    private boolean checkConnection(){
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
    public static void init(Context context){
        instance=new Network();
        instance.context=context;
    }

    private String sendRequest(String url, HashMap<String, String> params) throws IOException {
        InputStream is;
        String par="";
        if (params!=null && params.size()>0){
            par="?";
            for (String key:params.keySet()){
                par+=key+"="+params.get(key)+"&";
            }
            par=par.substring(0,par.length());
        }
        URL requestUrl=new URL(url+par);
        HttpsURLConnection con= (HttpsURLConnection) requestUrl.openConnection();
        con.setReadTimeout(readTimeout);
        con.setConnectTimeout(conTimeout);
        con.setRequestMethod("GET");

        con.setDoInput(true);
        con.connect();
        int response=con.getResponseCode();
        is = con.getInputStream();
        String contentAsString = readIt(is);
        return contentAsString;
    }
    private String readIt(InputStream stream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = stream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString("UTF-8");

        }

}
