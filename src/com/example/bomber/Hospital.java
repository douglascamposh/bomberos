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

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Hospital extends FragmentActivity implements OnMapClickListener, OnMarkerClickListener {
	ArrayList<DatosHospital> datos_hospitales;
	private GoogleMap mapa;
	private LocationManager locManager;
	private LocationListener locListener;
	private LatLng mi_posicion;
	private CameraPosition cameraPosition;
	int mMode=0;
	final int MODE_DRIVING=0;
	final int MODE_BICYCLING=1;
	final int MODE_WALKING=2;
	
	ArrayList<LatLng> markerPoints;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	   super.onCreate(savedInstanceState);
	   setContentView(R.layout.activity_hospital);
	   recuperarHospitales();
	   markerPoints = new ArrayList<LatLng>(); //inicializamos
	   mapa = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
	   LatLng posicion = new LatLng(-17.40049275908006, -66.1427843109314);
	   mapa.setMyLocationEnabled(true);
       mapa.getUiSettings().setCompassEnabled(true);
       comenzarLocalizacion();
       mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(posicion, 15));
       cameraPosition = CameraPosition.builder()
               .target(mi_posicion)
               .zoom(15)
               .bearing(90)
               .build();
       mapa.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),2000, null);
       llenarMarcadores();
       mapa.setOnMapClickListener(this);
       mapa.setOnMarkerClickListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.hospital, menu);
		return true;
	}
	private void llenarMarcadores(){
		for(int i=0; i<datos_hospitales.size(); i++){
			mapa.addMarker(new MarkerOptions()
	    	.position(new LatLng(datos_hospitales.get(i).getLatitude(), datos_hospitales.get(i).getLongitude()))
	    	.title(datos_hospitales.get(i).getHospital())
	    	.snippet(datos_hospitales.get(i).getDireccion() + " telf. "+ datos_hospitales.get(i).getTelefono() + " nivel "+datos_hospitales.get(i).getNivel()+ " tipo "+datos_hospitales.get(i).getTipo())
	    	.icon(BitmapDescriptorFactory
	        .fromResource(R.drawable.hospital))
	        .anchor(0.5f, 0.5f));
		}
	}
	private void recuperarHospitales(){
		try
	    {
	    HttpClient client = new DefaultHttpClient();
	    HttpPost post = new HttpPost("http://unidad-de-bomberos.herokuapp.com/hospitals/lista_hospitales");
	    
	    HttpResponse response = client.execute(post);
	    String json = EntityUtils.toString(response.getEntity());
	    JSONArray hospitales=new JSONArray(json);
	    //emergencias.getJSONObject(1).getDouble("latitude"); para recuperar lo que quiero del json
	    ArrayList<String> rows = new ArrayList<String>();
	    datos_hospitales = new ArrayList<DatosHospital>();
	    for(int i=0;i<hospitales.length();i++){
	    	JSONObject hospital = hospitales.getJSONObject(i);
	    	rows.add("tipo:"+hospital.getString("nombre_hospital")+ "direccion:"+hospital.getString("direccion"));
	    	datos_hospitales.add(new DatosHospital(hospital.getString("nombre_hospital"),hospital.getString("nivel"), hospital.getString("tipo"), hospital.getString("direccion"), hospital.getInt("telefono"), hospital.getDouble("latitude"),hospital.getDouble("longitude")));
	    }
	   }
	   catch(Exception ex)
	   {}
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
	private void comenzarLocalizacion()
	{
	    //Obtenemos una referencia al LocationManager
	    locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
	    Criteria criteria = new Criteria();
	    String mejorProvedor = locManager.getBestProvider(criteria, false);
	    //Obtenemos la última posición conocida
	    Location loc = locManager.getLastKnownLocation(mejorProvedor);
	 	setMiposicion(loc); 
	    //Nos registramos para recibir actualizaciones de la posición
	    locListener = new LocationListener() {
	        public void onLocationChanged(Location location) {
	            setMiposicion(location);
	            animacion();
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

	@Override
	public void onMapClick(LatLng point) {
		//mapa.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(-17.40049275908006, -66.1427843109314)));
		
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		dibujarRuta(mi_posicion, marker.getPosition());
		return true;
	}
	
	public void dibujarRuta(LatLng origen, LatLng destino){
		// Getting URL to the Google Directions API
				String url = getDirectionsUrl(origen, destino);				
				
				DownloadTask downloadTask = new DownloadTask();
				
				// Start downloading json data from Google Directions API
				downloadTask.execute(url);
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
