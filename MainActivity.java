package com.example.email;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button  button=null;
    private EditText address=null;
    private EditText content=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button=(Button)findViewById(R.id.send_button);
        address=(EditText)findViewById(R.id.editText3) ;
        content=(EditText)findViewById(R.id.editText);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String get=address.getText().toString();
                String send=content.getText().toString();
                String address[] = get.split(";");
                if (get .equals("")||send.equals("")) {
                    Toast.makeText(getApplicationContext(), "请先输入收件人和内容", 1).show();
                } else if (address.length == 1) {
                    //openDefaultBrowser(get,content);
                    String url="http://3.91.28.195:8080/email?address="+get+"&url="+send;
                    get(url);
                } else if (address.length >= 1) {
                    //System.out.println(address.length);

                    try {
                        post(address,send);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

            }

        });

    }

    private  void post(final String[]address, final String content) throws JSONException, IOException {

        new Thread(new Runnable() {
            @Override
            public void run() {
                //用%分开收件人，用#分开发送内容
                String url="";
                for(String i:address){
                    url+="%"+i;
                }
                url.substring(1);
                url+="#"+content;
                HttpClient httpClient = new DefaultHttpClient();
                //部署在公网上
                HttpPost httpPost=new HttpPost("http://3.91.28.195:8080/sendmessage");
                try {
                    StringEntity entity = new StringEntity(url, "utf-8");
                    entity.setContentType("application/text");
                    httpPost.setEntity(entity);
                    HttpResponse response = httpClient.execute(httpPost);
                    String res = null;
                    res = EntityUtils.toString(response.getEntity());
                    Looper.prepare();
                    if(res.equals("Y")){
                        Toast.makeText(getApplicationContext(), "发送成功", 0).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "发送失败"+res, 0).show();
                    }
                    Looper.loop();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //System.out.println(res);


            }
        }

        ).start();

    }
    private void get(String url){
         final String to=url;

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 生成请求对象
                //SSLSocketFactory.getSocketFactory().setHostnameVerifier(new AllowAllHostnameVerifier());
                HttpGet httpGet = new HttpGet(to);
                HttpClient httpClient = new DefaultHttpClient();

                // 发送请求
                try {

                    HttpResponse response = httpClient.execute(httpGet);
                    String  res = "";
                    HttpEntity entity = response.getEntity();
                    res = EntityUtils.toString(entity, "utf-8");

                    Looper.prepare();
                    if(res.equals("Y")){
                        Toast.makeText(getApplicationContext(), "发送成功", 0).show();
                    }
                    else{
                        Toast.makeText(getBaseContext(), "发送失败"+res, 0).show();
                    }
                    Looper.loop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }



}
