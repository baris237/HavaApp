package com.androstock.myweatherapp;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //Uygulamada bulunan TextView Loader TypeFce ve String işlemlerini tanımlama Bölümü //

    TextView selectCity, cityField, detailsField, currentTemperatureField, humidity_field, pressure_field, weatherIcon, updatedField;
    ProgressBar loader;
    Typeface weatherFont;
    String city = "";

    String OPEN_WEATHER_MAP_API = "ce3f2be7a586693deaff6fafa5be11b9"; //OpenWeather tarafından verilen Apı key kodu kullanmak için yapılan kod



    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        //Nesneleri Tanımlamak için Oluşturulan Bir Bölüm//

        loader = (ProgressBar) findViewById(R.id.loader); //Loader nesnesi için
        selectCity = (TextView) findViewById(R.id.selectCity); //Şehir seçimi  nesnesi için
        cityField = (TextView) findViewById(R.id.city_field); //Şehir güncellemesi için
        updatedField = (TextView) findViewById(R.id.updated_field); //Güncelleme nesnesi için
        detailsField = (TextView) findViewById(R.id.details_field); //Ayrıntıları gösteren nesne için
        currentTemperatureField = (TextView) findViewById(R.id.current_temperature_field); //Sıcaklık nesnesı için
        humidity_field = (TextView) findViewById(R.id.humidity_field); //Nem nesnesini tanımlamak için
        pressure_field = (TextView) findViewById(R.id.pressure_field); //Basınç nesnesini tanımlamak için
        weatherIcon = (TextView) findViewById(R.id.weather_icon); //İnternetten indirilen hava iconu için

        weatherFont = Typeface.createFromAsset(getAssets(), "fonts/weathericons-regular-webfont.ttf"); //Hava durumunun stilini fontunu belirtir
        weatherIcon.setTypeface(weatherFont); //Hava durumu ikonunu belirtir


        taskLoadUp(city);

        //selectCity içinde oluşturulan OnClickListener Metodu içinde yazılan kod bölümü//

        selectCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //selectCity hakkında oluşturulan OneClickListener Metodu ve içine yazılan kodlar
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this); //Oluşan uyarılar için oluşturlan bir oluşturucu
                alertDialog.setTitle("");
                final EditText input = new EditText(MainActivity.this); //Yeni EditText'in Nerden geldiğinin bilgisini verir.
                input.setText(city);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams( //Tek satırda oluşturulan düzenleyen bir düzen
                        LinearLayout.LayoutParams.MATCH_PARENT, //LinearLayout için girilen özel hight ve weight ayarlar
                        LinearLayout.LayoutParams.MATCH_PARENT); //LinearLayout için girilen özel hight ve weight ayarlar
                input.setLayoutParams(lp);
                alertDialog.setView(input);

                alertDialog.setPositiveButton("Değiştir", //Oluşturulan dialogda şehir değiştirmeye yarayan buton
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                city = input.getText().toString();
                                taskLoadUp(city);
                            }
                        });
                alertDialog.setNegativeButton("İptal", //Oluşturulan dialogda şehirin değişmek istemediği zaman kullanılan buton
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.show();
            }
        });


    }

    //Network'ün yani internetin var olup olmadğını olduğunda Open Weather'den verileri indiriyor eğer yoksa indirmiyor//
    public void taskLoadUp(String query) {
        if (Function.isNetworkAvailable(getApplicationContext())) { //Uygulama eğer Network ile yani İternete bağlanırsa
            DownloadWeather task = new DownloadWeather(); //Yeni hava durumu bilgisi indir
            task.execute(query); //Eğer yukarıdakilerin hepsi oldu ise execute yani gerçekleştirme kodu ile bu durumu indir ve gerçekleştir
        } else {
            Toast.makeText(getApplicationContext(), "İnternet Baglantısı Yok", Toast.LENGTH_LONG).show(); //İnternet Bağlantısı olmadığı zaman oluşturulan toast bölümü
        }
    }


    //DownloadWeather Sınıfında oluşturulan sınıf bölümü//

    class DownloadWeather extends AsyncTask < String, Void, String > { // AsyncTask arka bölümde oluşturulan olayları manipüle etmeye gerek kalmadan gerçeleştiren bir sınıf
        @Override
        protected void onPreExecute() {
            super.onPreExecute(); //Görev çağırılmadan önce iş parçacığını yönlendirilmeyi sağlar
            loader.setVisibility(View.VISIBLE); //Loader ikonunun görülesini sağlanır

        }
        //Arkaplanda Hangi programın Çalışacağını belirler//
        protected String doInBackground(String...args) {
            String xml = Function.excuteGet("http://api.openweathermap.org/data/2.5/weather?q=" + args[0] +
                    "&units=metric&appid=" + OPEN_WEATHER_MAP_API); //Veriyi openweather sitesinden çekmeyi sağlar
            return xml;
        }
        @Override
        protected void onPostExecute(String xml) {

            //JSON ile veri çekmek için kullanılan sınıf ama hata çıktığında emulator hata vermesın dıye catch ve try da kullanılır.
            try {
                JSONObject json = new JSONObject(xml); //JSONObject internetten veri çekmeye ve gödermeye yarar
                if (json != null) {
                    JSONObject details = json.getJSONArray("weather").getJSONObject(0);
                    JSONObject main = json.getJSONObject("main");
                    DateFormat df = DateFormat.getDateTimeInstance(); //Tarih Formatını Saat formatını girer

                    cityField.setText(json.getString("name").toUpperCase(Locale.US) + ", " + json.getJSONObject("sys").getString("country"));
                    detailsField.setText(details.getString("description").toUpperCase(Locale.US)); //Açıklamanın yazılmasını sağlar
                    currentTemperatureField.setText(String.format("%.2f", main.getDouble("temp")) + "°"); //Ekranda sıcaklığın yazmasını sağlar
                    humidity_field.setText("Nem: " + main.getString("humidity") + "%"); //Ekranda nemim yazmasını sağlar
                    pressure_field.setText("Basınç: " + main.getString("pressure") + " hPa"); //Ekranda basıncın yazılmasını sağlar
                    updatedField.setText(df.format(new Date(json.getLong("dt") * 1000))); //Ekranda verilerin güncellenmesi sağlanır
                    weatherIcon.setText(Html.fromHtml(Function.setWeatherIcon(details.getInt("id"),  //Weather iconlarının değişmesi sağlanır
                            json.getJSONObject("sys").getLong("sunrise") * 1000, //İnternetten(OpenWeather) gün doğumu verisi çeker
                            json.getJSONObject("sys").getLong("sunset") * 1000))); //İnternetten(OpenWeather) gün batımı verisi çeker

                    loader.setVisibility(View.GONE); //Loader Imajını Ekranda kaybeder.

                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Hata!!", Toast.LENGTH_SHORT).show(); //Hata meydana geldiğinde toast olarak "hata" mesajı verir
            }


        }



    }



}