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
import org.json.JSONArray;
import org.json.JSONObject;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class EmergenciasCurso extends ActionBarActivity {
	ListView lista;
	ArrayList<DatosEmergencia> datos_emergencias;
	private ArrayAdapter adaptador;
	private String URL = "http://unidad-de-bomberos.herokuapp.com/emergencies/emergencias_en_curso";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_emergencias_curso);
	    String json = conexion(URL);
	    setDatosEmergencia(json);

	    //le paso al adaptador los strings que quiero que se muestre en la lista
	 /*   ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.two_line_list_item,rows);
	    lista = (ListView)findViewById(R.id.listView1);
	    //adaptador = new EmergenciaArrayAdapter(this,datos_emergencias);
	    adaptador = new EmergenciaArrayAdapter<DatosEmergencia>(
                this,
                datos_emergencias);
	    //relacionamos el adapter con la lista
	    lista.setAdapter(adaptador);*/
	    //con imagenes
	    lista = (ListView)findViewById(R.id.listView1);
	    lista.setAdapter(new EmergenciaAdapter(this, datos_emergencias));
	 
	    lista.setOnItemClickListener(new OnItemClickListener() {

	    	/*@Override
	    	public void onItemClick(AdapterView<?> parent, View view,
	    	int position, long id) {

	    	String itemValue = (String) lista.getItemAtPosition(position);
	    	Order l=filteredOrders.get(position);
	    	Intent myIntent = new Intent(OrderManager.this, ShippingManager.class);
	    	myIntent.putExtra("Order",l);
	    	startActivity(myIntent);
	    	finish();
	    	}*/

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//para mostrar una ventana emergente
				//Toast.makeText(EmergenciasCurso.this, position+"", Toast.LENGTH_LONG).show();
		    	Intent intent = new Intent(EmergenciasCurso.this, Emergencia.class);
		        //Creamos la información a pasar entre actividades
		        Bundle b = new Bundle();
		        b.putSerializable("emergencia", datos_emergencias.get(position));

		        //Añadimos la información al intent
		        intent.putExtras(b);

		        //Iniciamos la nueva actividad
		        startActivity(intent);
			}

	    	});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.emergencias_curso, menu);
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
	private String conexion(String url){
		String json = null; 
		try
	    {
	    HttpClient client = new DefaultHttpClient();
	    HttpPost post = new HttpPost(url);
	    
	    HttpResponse response = client.execute(post);
	    json = EntityUtils.toString(response.getEntity());
	    }
		catch(Exception ex)
	    {
	    	 
	    }
		return json;
	}
	private void setDatosEmergencia(String json){
		try
	    {
	    
	    JSONArray emergencias=new JSONArray(json);
	    //emergencias.getJSONObject(1).getDouble("latitude"); para recuperar lo que quiero del json
	    ArrayList<String> rows = new ArrayList<String>();
	    datos_emergencias = new ArrayList<DatosEmergencia>();
	    //cargo informacion de las emergencias para el adapter y al objeto DatoEmergencia
	    for(int i=0;i<emergencias.length();i++){
	    	JSONObject emergencia = emergencias.getJSONObject(i);
	    	rows.add("tipo:"+emergencia.getString("tipo")+ ",direccion:"+emergencia.getString("lugar"));
	    	datos_emergencias.add(new DatosEmergencia(emergencia.getInt("id"),emergencia.getString("lugar"),emergencia.getString("tipo"), emergencia.getString("telefono_denunciante"), emergencia.getDouble("latitude"),emergencia.getDouble("longitude")));
	    	if(datos_emergencias.get(i).getTipo().equals("incendio"))
	    		datos_emergencias.get(i).setImagen(R.drawable.incendio);
	    	if(datos_emergencias.get(i).getTipo().equals("explosivo"))
	    		datos_emergencias.get(i).setImagen(R.drawable.explosivo);
	    	if(datos_emergencias.get(i).getTipo().equals("rescate"))
	    		datos_emergencias.get(i).setImagen(R.drawable.rescate);
	    	if(datos_emergencias.get(i).getTipo().equals("prehospitalaria"))
	    		datos_emergencias.get(i).setImagen(R.drawable.cruz);
	    }
	    }
		catch(Exception ex)
	    {
	    	 
	    }
	}
}
