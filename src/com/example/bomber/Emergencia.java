package com.example.bomber;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class Emergencia extends FragmentActivity {
	private GoogleMap mapa;
	private LocationManager locManager;
	private LocationListener locListener;
	private LatLng mi_posicion;
	private CameraPosition cameraPosition;
	private static final String PROPERTY_REG_ID = "registration_id";
	private static final String TAG = "GCM";
	private String regid;
	private int emergencia_id;
	
	ArrayList<LatLng> markerPoints;
	int mMode=0;
	final int MODE_DRIVING=0;
	final int MODE_BICYCLING=1;
	final int MODE_WALKING=2;
	private Timer timer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_emergencia);
		//Localizar los controles
        //TextView txtSaludo = (TextView)findViewById(R.id.textView1);
		//recupero el identificador del telefono
		regid = obtenerIdentificadorDeRegistroAlmacenado();
		
		markerPoints = new ArrayList<LatLng>(); //inicializamos
        //Recuperamos la información pasada en el intent
        Bundle bundle = this.getIntent().getExtras();

        //Construimos el mensaje a mostrar
        //txtSaludo.setText("Hola " + bundle.getString("pasando"));
        DatosEmergencia emergencia = (DatosEmergencia) bundle.getSerializable("emergencia");
        //recupera la emergencia id para saber que emeregencia es esta
      	emergencia_id=emergencia.getEmergencia_id();
        mapa = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        LatLng posicion = new LatLng(emergencia.getLatitude(), emergencia.getLongitude());
        
        mapa.setMyLocationEnabled(true);
        mapa.getUiSettings().setCompassEnabled(true);
        comenzarLocalizacion();
       // irPosicion();
        //LatLng mi_posicion = new LatLng(mapa.getMyLocation().getLatitude(), mapa.getMyLocation().getLongitude());
        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(posicion, 15));
        cameraPosition = CameraPosition.builder()
                .target(mi_posicion)
                .zoom(15)
                .bearing(90)
                .build();
        mapa.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),2000, null);
        //me devuelve mi posiocion
        //mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mapa.getMyLocation().getLatitude(), mapa.getMyLocation().getLongitude()), 15));
        mapa.addMarker(new MarkerOptions()
        	.position(posicion)
        	.title(emergencia.getTipo())
        	.snippet(emergencia.getLugar() + " telf. "+ emergencia.getTelefono())
        	.icon(BitmapDescriptorFactory
            .fromResource(R.drawable.emergencia))
            .anchor(0.5f, 0.5f));
        markerPoints.add(mi_posicion); //posicion origen
        markerPoints.add(posicion); // posicio destino
        //inicializando marcadores de origen y destino
        LatLng origin = markerPoints.get(0);
		LatLng dest = markerPoints.get(1);
		dibujarRuta(origin,dest);
		temporizador();
		
	    timer = new Timer();
	    TimerTask task = new TimerTask() {

	        @Override
	        public void run()
	        {
	            markerPoints.set(0,mi_posicion);
	            dibujarRuta(markerPoints.get(0),markerPoints.get(1));
	        }
	        };
	        // Empezamos dentro de 10ms y luego lanzamos la tarea cada 5000ms
	    timer.schedule(task, 10, 5000);
	}
		
	public void temporizador(){
		
	}
	public void dibujarRuta(LatLng origen, LatLng destino){
		// Getting URL to the Google Directions API
				String url = getDirectionsUrl(origen, destino);				
				
				DownloadTask downloadTask = new DownloadTask();
				
				// Start downloading json data from Google Directions API
				downloadTask.execute(url);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.emergencia, menu);
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
	public void irPosicion() {
	      if (mapa.getMyLocation() != null)
	         mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(
	            new LatLng( mapa.getMyLocation().getLatitude(), mapa.getMyLocation().getLongitude()), 15));
	}
	
	public void setMiposicion(Location loc){
		try {
			mi_posicion = new LatLng(loc.getLatitude(), loc.getLongitude());
	    } catch (NullPointerException e) {
	    	mi_posicion = new LatLng(-1.0, -1.0);
	    }
		
	}
	public void animacion(){
		// Animate the change in camera view over 2 seconds
        mapa.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),2000, null);
	}
	public void enviarPosicionAlServidor(Location loc){
		try{
			HttpClient client = new DefaultHttpClient();
	    	HttpPost post = new HttpPost("http://unidad-de-bomberos.herokuapp.com/androids/update_position");
	    	List<NameValuePair> pairs = new ArrayList<NameValuePair>();
	    	pairs.add(new BasicNameValuePair("latitude", String.valueOf(loc.getLatitude())));
	    	pairs.add(new BasicNameValuePair("longitude", String.valueOf(loc.getLongitude())));
	    	pairs.add(new BasicNameValuePair("registrationId", String.valueOf(regid)));
	    	pairs.add(new BasicNameValuePair("id", String.valueOf(emergencia_id)));
	    	post.setEntity(new UrlEncodedFormEntity(pairs));
	    	client.execute(post);
	    }
	    catch(Exception ex){
	    }
	}
	//recupera identificador del telefono
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private String obtenerIdentificadorDeRegistroAlmacenado() {
	    final SharedPreferences prefs = getPreferenciasCompartidas();
	    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	    if (registrationId.isEmpty()) {
	      Log.i(TAG, "Registration not found.");
	      return "";
	    }
	    
	    return registrationId;
	  }
	private SharedPreferences getPreferenciasCompartidas() {
	    return getSharedPreferences(MainActivity.class.getSimpleName(),
	        Context.MODE_PRIVATE);
	  }
	private void comenzarLocalizacion()
	{
	    //Obtenemos una referencia al LocationManager
	    locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
	    Criteria criteria = new Criteria();
	    String mejorProvedor = locManager.getBestProvider(criteria, false);
	    //Obtenemos la última posición conocida
	    Location loc = locManager.getLastKnownLocation(mejorProvedor);
	 	setMiposicion(loc);
	 	enviarPosicionAlServidor(loc);
	    //Nos registramos para recibir actualizaciones de la posición
	    locListener = new LocationListener() {
	        public void onLocationChanged(Location location) {
	            setMiposicion(location);
	            animacion();
	            enviarPosicionAlServidor(location);
	        }

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub
				
			}
	 
	        
	    };
	 
	    locManager.requestLocationUpdates(
	        LocationManager.GPS_PROVIDER, 30000, 0, locListener);
	}
	
	// Drawing Start and Stop locations
		private void drawStartStopMarkers(){
			
			for(int i=0;i<markerPoints.size();i++){
			
				// Creating MarkerOptions
				MarkerOptions options = new MarkerOptions();			
				
				
				// Setting the position of the marker
				options.position(markerPoints.get(i) );
				
				/** 
				 * For the start location, the color of marker is GREEN and
				 * for the end location, the color of marker is RED.
				 */
				if(i==0){
					options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
				}else if(i==1){
					options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
				}			
				
				// Add new marker to the Google Map Android API V2
				mapa.addMarker(options);
			}		
		}
		
		
		private String getDirectionsUrl(LatLng origin,LatLng dest){
						
			// Origin of route
			String str_origin = "origin="+origin.latitude+","+origin.longitude;
			
			// Destination of route
			String str_dest = "destination="+dest.latitude+","+dest.longitude;	
						
			// Sensor enabled
			String sensor = "sensor=false";			
			
			// Travelling Mode
			String mode = "mode=driving";	
			mMode = 0 ;
						
			// Building the parameters to the web service
			String parameters = str_origin+"&"+str_dest+"&"+sensor+"&"+mode;
						
			// Output format
			String output = "json";
			
			// Building the url to the web service
			String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
			
			
			return url;
		}
		
		/** A method to download json data from url */
	    private String downloadUrl(String strUrl) throws IOException{
	        String data = "";
	        InputStream iStream = null;
	        HttpURLConnection urlConnection = null;
	        try{
	                URL url = new URL(strUrl);

	                // Creating an http connection to communicate with url 
	                urlConnection = (HttpURLConnection) url.openConnection();

	                // Connecting to url 
	                urlConnection.connect();

	                // Reading data from url 
	                iStream = urlConnection.getInputStream();

	                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

	                StringBuffer sb  = new StringBuffer();

	                String line = "";
	                while( ( line = br.readLine())  != null){
	                        sb.append(line);
	                }
	                
	                data = sb.toString();

	                br.close();

	        }catch(Exception e){
	                Log.d("Exception while downloading url", e.toString());
	        }finally{
	                iStream.close();
	                urlConnection.disconnect();
	        }
	        return data;
	     }

		
		
		// Fetches data from url passed
		private class DownloadTask extends AsyncTask<String, Void, String>{			
					
			// Downloading data in non-ui thread
			@Override
			protected String doInBackground(String... url) {
					
				// For storing data from web service
				String data = "";
						
				try{
					// Fetching the data from web service
					data = downloadUrl(url[0]);
				}catch(Exception e){
					Log.d("Background Task",e.toString());
				}
				return data;		
			}
			
			// Executes in UI thread, after the execution of
			// doInBackground()
			@Override
			protected void onPostExecute(String result) {			
				super.onPostExecute(result);			
				
				ParserTask parserTask = new ParserTask();
				
				// Invokes the thread for parsing the JSON data
				parserTask.execute(result);
					
			}		
		}
		
		/** A class to parse the Google Places in JSON format */
	    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
	    	
	    	// Parsing the data in non-ui thread    	
			@Override
			protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
				
				JSONObject jObject;	
				List<List<HashMap<String, String>>> routes = null;			           
	            
	            try{
	            	jObject = new JSONObject(jsonData[0]);
	            	DirectionsJSONParser parser = new DirectionsJSONParser();
	            	
	            	// Starts parsing data
	            	routes = parser.parse(jObject);    
	            }catch(Exception e){
	            	e.printStackTrace();
	            }
	            return routes;
			}
			
			// Executes in UI thread, after the parsing process
			@Override
			protected void onPostExecute(List<List<HashMap<String, String>>> result) {
				ArrayList<LatLng> points = null;
				PolylineOptions lineOptions = null;
				MarkerOptions markerOptions = new MarkerOptions();
				
				// Traversing through all the routes
				for(int i=0;i<result.size();i++){
					points = new ArrayList<LatLng>();
					lineOptions = new PolylineOptions();
					
					// Fetching i-th route
					List<HashMap<String, String>> path = result.get(i);
					
					// Fetching all the points in i-th route
					for(int j=0;j<path.size();j++){
						HashMap<String,String> point = path.get(j);					
						
						double lat = Double.parseDouble(point.get("lat"));
						double lng = Double.parseDouble(point.get("lng"));
						LatLng position = new LatLng(lat, lng);	
						
						points.add(position);						
					}
					
					// Adding all the points in the route to LineOptions
					lineOptions.addAll(points);
					lineOptions.width(5);
					
					// Changing the color polyline according to the mode
					if(mMode==MODE_DRIVING)
						lineOptions.color(Color.RED);
				}
				
				if(result.size()<1){
					Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
					return;
				}
				
				// Drawing polyline in the Google Map for the i-th route
				mapa.addPolyline(lineOptions);
				
			}			
	    }   
	    
	
}
