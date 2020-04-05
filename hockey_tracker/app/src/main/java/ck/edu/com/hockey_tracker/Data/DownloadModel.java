package ck.edu.com.hockey_tracker.Data;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class DownloadModel extends AsyncTask<Void, Void, String> {
    Context context;
    private String mode;
    private MatchModel matchModel;

    public DownloadModel(Context context, String mode, MatchModel matchModel) {
        this.context = context;
        this.mode = mode;
        this.matchModel = matchModel;
    }

    public DownloadModel(Context context, String mode) {
        this.context = context;
        this.mode = mode;
    }

    @Override
    protected String doInBackground(Void... params) {
        String matchListLoader;
        String answer = "failed";
        String IP_COMPUTER = "192.168.1.3";
        String IP_Mobile = "10.0.2.2";
        try (Socket socket = new Socket(IP_COMPUTER, 9876)){
            String message = "";
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            PrintWriter pw = new PrintWriter(dos);
            if (this.mode.equals("INSERT")) {
                message = matchModel.toJSONNew();
                pw.println(message);
                pw.flush();
                try {
                    answer = dis.readUTF();
                    Log.d("ASNWER", answer);
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    // We were cancelled; stop sleeping!
                }

            } else if (this.mode.equals("GETALL")) {
                String messageGet = "";
                JSONObject jsonObject= new JSONObject();
                try {
                    jsonObject.put("mode", "getAll");
                    messageGet = jsonObject.toString();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return "";
                }

                pw.println(messageGet);
                pw.flush();

                BufferedReader in = new BufferedReader(new InputStreamReader(dis));
                String jsonMatchArray = in.readLine();
                matchListLoader = jsonMatchArray;
                answer = matchListLoader;
                Log.d("JSONMATCH", jsonMatchArray);
            }
            dis.close();
            dos.close();

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("ERROR", e.toString());
        }

        return answer;
    }
}