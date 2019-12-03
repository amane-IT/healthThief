package com.example.myapplication;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpMgrThread extends Thread {

    public HttpMgrThread(){

    }
    @Override
    public void run(){
        reqHttp();

    }

    public void reqHttp() {
        URL url = null;
        try{
            url = new URL("www.naver.com"); //나중에 아마존 서버로 바꿔야함,,
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.connect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
