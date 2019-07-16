package com.androstock.myweatherapp;

import android.content.Context;
import android.net.ConnectivityManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class Function {



    public static boolean isNetworkAvailable(Context context) //Wİ-Fİ hakkında bilgi almak için kurulan bir metod.
    {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
        // ConnectivityManager=Ağ bağlantısının durumu hakkındaki sorguları yanıtlayan Sınıf. Ayrıca network'ün değiştiği zaman haber verir.
        // CONNECTIVITY_SERVICE =Veri çekme verileri alma yayınlama başlatma gibi şeyler yapan soyut bir sınıf
    }



    public static String excuteGet(String targetURL) //HttpConnection özelliğini destekleyen bir fonksiyon
    {
        URL url; //Kaynak bulucu anlamına gelir.
        HttpURLConnection connection = null; //Http ye özellikleri destekleyen bir URLCONNECTİON.
        try {

            url = new URL(targetURL); //Yeni bir URL tanımlamak için
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("content-type", "application/json;  charset=utf-8"); //Bağlantı nesnesi, bir URL üzerindeki openConnection yöntemini çağırarak oluşturulur.
            connection.setRequestProperty("Content-Language", "en-TR");
            connection.setUseCaches (false); //Ön belleği Çalıştırmamak için kullanılan bir kod
            connection.setDoInput(true); //Giriş yapmak için kullanılan bir kavram
            connection.setDoOutput(false); //Çıkış yapmayı engelleyen bir kavram

            InputStream is; //Boyut giriş sınıf kısmını temsil eden bir üst sınıftır.
            int status = connection.getResponseCode(); //API Kodlarında yanıt olarak döndürür.
            if (status != HttpURLConnection.HTTP_OK) //HTTP 200 kodu bildirimi verir
                is = connection.getErrorStream(); //Eror sistemi verir
            else
                is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is)); //BufferedReader=Karakter-girdi akışından metin okur,karakterleri tamponlar.Tampon boyutu belirtilebilir veya varsayılan boyut kullanılabilir.
            String line;
            StringBuffer response = new StringBuffer(); //İş parçacığı için güvenli, değişken karakter dizisi.
            while((line = rd.readLine()) != null) {
                response.append(line); //line ye cevap verir
                response.append('\r');
            }
            rd.close(); //Girdi cıktıyı kapat
            return response.toString();
        } catch (Exception e) {
            return null;
        } finally {
            if(connection != null) { //Bağlantı sıfıra eşit olduğunda
                connection.disconnect(); //Bağlantıyı kes.
            }
        }
    }


    public static String setWeatherIcon(int actualId, long sunrise, long sunset){ //SetWeatherIcon için Gündoğumu Gün batımı Tanımlaması
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) { //Eğer bulunduğumuz zaman gün doğumundan büyük ise ve gün batımından küçük ise icon koy
                icon = "&#xf00d;";
            } else { //Değil ise sunu koy
                icon = "&#xf02e;";
            }
        } else {
            switch(id) { //Eğer id 2 ise icon koy vs.
                case 2 : icon = "&#xf01e;";
                    break;
                case 3 : icon = "&#xf01c;";
                    break;
                case 7 : icon = "&#xf014;";
                    break;
                case 8 : icon = "&#xf013;";
                    break;
                case 6 : icon = "&#xf01b;";
                    break;
                case 5 : icon = "&#xf019;";
                    break;
            }
        }
        return icon; //İconu dondur
    }


}