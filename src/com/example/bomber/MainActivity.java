package com.example.bomber;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.R.string;
import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;


@TargetApi(Build.VERSION_CODES.GINGERBREAD) @SuppressLint("NewApi") public class MainActivity extends ActionBarActivity {
	TextView t ;
    @SuppressLint("NewApi") @TargetApi(Build.VERSION_CODES.GINGERBREAD) @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        t = (TextView)findViewById(R.id.textView2);
        //mayor a 0 para que este logueado
        if(SaveSharedPreference.getUserName(getApplicationContext()).length() > 0){
        	//Creamos el Intent para ir a la vista de menu principal
        	menu_principal();
        }
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
       /* t.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				t.setText("mi nuevo texto al hacer click");
				
			}
		});*/
    }
    private void menu_principal(){
    	//Intent intent = new Intent(MainActivity.this, Emergencia.class);
    	Intent intent = new Intent(MainActivity.this, MenuPrincipal.class);
        //Iniciamos la nueva actividad
        startActivity(intent);
        finish(); // sirve para no volver atras
    }

    private void lista_de_emergencias() {
    	//Intent intent = new Intent(MainActivity.this, Emergencia.class);
    	Intent intent = new Intent(MainActivity.this, EmergenciasCurso.class);
        //Creamos la información a pasar entre actividades
        Bundle b = new Bundle();
        b.putString("pasando", t.getText().toString());

        //Añadimos la información al intent
        intent.putExtras(b);

        //Iniciamos la nueva actividad
        startActivity(intent);
        finish(); // sirve para no volver atras
		
	}

	private boolean Login(String username,String pass)
    {
    try
    {
    	HttpClient client = new DefaultHttpClient();
    	HttpPost post = new HttpPost("http://unidad-de-bomberos.herokuapp.com/users/login_service");
    	List<NameValuePair> pairs = new ArrayList<NameValuePair>();
    	pairs.add(new BasicNameValuePair("username", username));
    	pairs.add(new BasicNameValuePair("password",pass));
    	post.setEntity(new UrlEncodedFormEntity(pairs));
    	HttpResponse response = client.execute(post);
    	String json = EntityUtils.toString(response.getEntity());
    	JSONObject result=new JSONObject(json);
    	boolean isLogin=result.getBoolean("res");	
    	return isLogin;
    }
    catch(Exception ex)
    {
    	return false;
    }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void loguearse(View v){
    	EditText user = (EditText)findViewById(R.id.editText1);
    	EditText pass = (EditText)findViewById(R.id.editText2);
    	String usuario = user.getText().toString();
    	boolean res=Login(usuario,pass.getText().toString());
		if(res){
			SaveSharedPreference.setUserName(getApplicationContext(), usuario);
			menu_principal();
		}
		else
			t.setText("nombre de usuario o contraseña incorrecto");
    }
}
