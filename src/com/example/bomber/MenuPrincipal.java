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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class MenuPrincipal extends Activity {
	// Url del servicio REST donde se almacenara los id de los telefonos en Rails (registration_id)
	public static final String URL_REGISTRO_ID = "http://unidad-de-bomberos.herokuapp.com/androids/register_phone";
	// Seña númerica que se utiliza cuando se verifica la disponibilidad de los
	// google play services
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	// Una simple Tag utilizada en los logs
	private static final String TAG = "Demo GCM";
	 
	public static final String EXTRA_MESSAGE = "message";
	// Clave que permite recuperar de las preferencias compartidas de la
	// aplicación el dentificador de registro en GCM
	private static final String PROPERTY_REG_ID = "registration_id";
	// Clave que permite recuperar de las preferencias compartidas de la
	// aplicación el dentificador de la versión de la aplicación
	private static final String PROPERTY_APP_VERSION = "appVersion";
	// Identificador de la instancia del servicio de GCM al cual accedemos
	private static final String SENDER_ID = "937468662773";
	// Clase que da acceso a la api de GCM
	private GoogleCloudMessaging gcm;
	// Identificador de registro
	private String regid;
	// Contexto de la aplicación
	private Context contexto;
	public TextView clave;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu_principal);
		contexto = this;
		  // Se comprueba que Play Services APK estan disponibles, Si lo esta se
		  // proocede con el registro en GCM
		  if (chequearPlayServices()) {
		    gcm = GoogleCloudMessaging.getInstance(contexto);
		    // Se recupera el "registration Id" almacenado en caso que la
		    // aplicación ya se hubiera registrado previamente
		    regid = obtenerIdentificadorDeRegistroAlmacenado();
		    //clave = (TextView)findViewById(R.id.display);
		    //clave.setText(regid.toString()+" este es el id del cel");
		    // Si no se ha podido recuperar el id del registro procedemos a
		    // obtenerlo mediante el proceso de registro
		    if (regid.isEmpty()) {
		      // Se inicia el proceso de registro
		      registroEnSegundoPlano();
		    }
		  } else {
		    Log.i(TAG, "No valid Google Play Services APK found.");
		  }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_principal, menu);
		return true;
	}
	public void hospitales(View v){
		Intent intent = new Intent(MenuPrincipal.this, Hospital.class);
        //Iniciamos la nueva actividad
        startActivity(intent);
    }
	public void emergencias(View v){
    	//Intent intent = new Intent(MainActivity.this, Emergencia.class);
    	Intent intent = new Intent(MenuPrincipal.this, EmergenciasCurso.class);
        //Iniciamos la nueva actividad
        startActivity(intent);
        //finish(); // sirve para no volver atras
		
	}
	public void cerrar(View v){
		SaveSharedPreference.clearUserName(getApplicationContext());
		Intent intent = new Intent(MenuPrincipal.this, MainActivity.class);
		startActivity(intent);
		finish();
	}
	/**
	 * Este metodo comprueba si Google Play Services esta disponible, ya que
	 * este requiere que el terminal este asociado a una cuenta de google.Esta
	 * verificación es necesaria porque no todos los dispositivos Android estan
	 * asociados a una cuenta de Google ni usan sus servicios, por ejemplo, el
	 * Kindle fire de Amazon, que es una tablet Android pero no requiere de una
	 * cuenta de Google.
	 * 
	 * @return Indica si Google Play Services esta disponible.
	 */
	 private boolean chequearPlayServices() {
	  int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(contexto);
	  if (resultCode != ConnectionResult.SUCCESS) {
	    if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	      GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	          PLAY_SERVICES_RESOLUTION_REQUEST).show();
	    } else {
	      Log.i(TAG, "Dispositivo no soportado.");
	      finish();
	    }
	    return false;
	  }
	  return true;
	}
	
	 /**
	   * Metodo que recupera el registration ID que fue almacenado la ultima vez
	   * que la aplicación se registro, En caso que la aplicación este
	   * desactualizada o no se haya registrado previamente no se recuperara
	   * ningón registration ID
	   * 
	   * @return identificador del registro, o vacio("") si no existe o esta
	   *         desactualizado dicho registro
	   */
	  private String obtenerIdentificadorDeRegistroAlmacenado() {
	    final SharedPreferences prefs = getPreferenciasCompartidas();
	    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	    if (registrationId.isEmpty()) {
	      Log.i(TAG, "Registration not found.");
	      return "";
	    }
	    // Comprueba si la aplicación esta actualizada
	    int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
	        Integer.MIN_VALUE);
	    int currentVersion = getVersionDeLaAplicacion();
	    if (registeredVersion != currentVersion) {
	      Log.i(TAG, "App version changed.");
	      return "";
	    }
	    return registrationId;
	  }
	 
	/**
	   * Metodo que sirve para recupera las preferencias compartidas en modo privado
	   * 
	   * @return Application's {@code SharedPreferences}.
	   */
	  private SharedPreferences getPreferenciasCompartidas() {
	    return getSharedPreferences(MainActivity.class.getSimpleName(),
	        Context.MODE_PRIVATE);
	  }
	 
	  /**
	   * Recupera la versión aplicación que identifica a cada una de las
	   * actualizaciones de la misma.
	   * 
	   * @return La versión del codigo de la aplicación
	   */
	  private int getVersionDeLaAplicacion() {
	    try {
	      PackageInfo packageInfo = contexto.getPackageManager()
	          .getPackageInfo(contexto.getPackageName(), 0);
	      return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	      // should never happen
	      throw new RuntimeException("Could not get package name: " + e);
	    }
	  }
	  
	  /**
	   * En este método se procede al registro de la aplicación obteniendo el
	   * identificador de registro que se almacena en la tarjeta de memoria para
	   * no tener que repetir el mismo proceso la próxima vez. Adicionalmente se
	   * envía el identificador de registro al a la aplicación jee , invocando un
	   * servicio REST.
	   */
	  private void registroEnSegundoPlano() {
	    new AsyncTask<Object, Object, Object>() {
	      @Override
	      protected void onPostExecute(final Object result) {
	        Log.i(TAG, result.toString());
	      }
	   
	      @Override
	      protected String doInBackground(final Object... params) {
	        String msg = "";
	        try {
	          if (gcm == null) {
	            gcm = GoogleCloudMessaging.getInstance(contexto);
	          }
	          // En este metodo se invoca al servicio de registro de los
	          // servicios GCM
	          regid = gcm.register(SENDER_ID);
	          msg = "Dispositivo registrado, registration ID=" + regid;
	          Log.i(TAG, msg);
	          // Una vez se tiene el identificador de registro se manda a
	          // la aplicacion jee
	          // ya que para que esta envie el mensaje de la notificación
	          // a los servidores
	          // de GCM es necesario dicho identificador
	          enviarIdentificadorRegistroALaAplicacionRor();
	          // Se persiste el identificador de registro para que no sea
	          // necesario repetir el proceso de
	          // registro la proxima vez
	          //clave.setText(regid.toString()+" este es el id del cel");
	          //Toast.makeText(MenuPrincipal.this, regid.toString()+" este es el id del cel", Toast.LENGTH_LONG).show();
	          almacenarElIdentificadorDeRegistro(regid);
	        } catch (Exception e) {
	          msg = "Error :" + e.getMessage();
	          e.printStackTrace();
	        }
	        return msg;
	      }
	   
	    }.execute(this, null, null);
	  }
	   
	  /**
	   * Se almacena el identificador de registro de "Google Cloud Message" y la
	   * versión de la aplicación
	   * 
	   * @param regId identificador de registro en GCM
	   */
	  private void almacenarElIdentificadorDeRegistro(String regId) {
	    final SharedPreferences prefs = getPreferenciasCompartidas();
	    int appVersion = getVersionDeLaAplicacion();
	    Log.i(TAG, "Saving regId on app version " + appVersion);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(PROPERTY_REG_ID, regId);
	    editor.putInt(PROPERTY_APP_VERSION, appVersion);
	    editor.commit();
	  }
	  /**
	   * Se envía el identificador de registro de GCM mediante la invocación de un
	   * servicio REST por el método POST, pasándole por parámetro un objeto json
	   * que envuelve dicho identificador
	   * 
	   * @param url
	   *            URL del servicio REST al cual invocar
	   * @param json
	   *            Objeto json que contiene el identificador de registro a enviar
	   * @return Devuelve un objeto json que contiene un mensaje de confirmación
	   *         del envio del identificador del registro
	   * @throws Exception
	   */
	   /*
	  private void enviarIdentificadorRegistroALaAplicacionRor()
	      throws Exception {
	    JSONObject requestRegistrationId = new JSONObject();
	    requestRegistrationId.put("registrationId", regid);
	    BufferedReader in = null;
	    try {
	      HttpClient client = new DefaultHttpClient();
	      HttpPost httpPost = new HttpPost();
	      httpPost.setURI(new URI(URL_REGISTRO_ID));
	      httpPost.setEntity(new StringEntity(requestRegistrationId
	          .toString(), "UTF-8"));
	      httpPost.setHeader("Accept", "application/json");
	      httpPost.setHeader("content-type", "application/json");
	   
	      HttpResponse response = client.execute(httpPost);
	      InputStreamReader lectura = new InputStreamReader(response
	          .getEntity().getContent());
	      in = new BufferedReader(lectura);
	      StringBuffer sb = new StringBuffer("");
	      String line = "";
	      while ((line = in.readLine()) != null) {
	        sb.append(line);
	      }
	      in.close();
	      Log.i("INFO", sb.toString());
	    } catch (Exception e) {
	      Log.e("ERROR", e.getMessage(), e);
	    } finally {
	      if (in != null) {
	        try {
	          in.close();
	        } catch (IOException e) {
	          e.printStackTrace();
	        }
	      }
	    }
	  }*/
	  private void enviarIdentificadorRegistroALaAplicacionRor(){
	    try{
	    	HttpClient client = new DefaultHttpClient();
	    	HttpPost post = new HttpPost(URL_REGISTRO_ID);
	    	List<NameValuePair> pairs = new ArrayList<NameValuePair>();
	    	String nombre =SaveSharedPreference.getUserName(getApplicationContext());
	    	pairs.add(new BasicNameValuePair("registrationId", regid));
	    	pairs.add(new BasicNameValuePair("nombre", nombre));
	    	post.setEntity(new UrlEncodedFormEntity(pairs));
	    	HttpResponse response = client.execute(post);
	    	String json = EntityUtils.toString(response.getEntity());
	    	JSONObject result=new JSONObject(json);
	    	boolean isLogin=result.getBoolean("res");
	    }
	    catch(Exception ex){
	    }

	 }

}
