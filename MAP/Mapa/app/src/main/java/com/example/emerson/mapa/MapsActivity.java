package com.example.emerson.mapa;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marcador;
    double lat = 0.0;
    double lng = 0.0;
    Marker Punto;
    RequestQueue queue;
    List<Lugar> lugares;
    int j = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        queue = Volley.newRequestQueue(this);
        lugares = new ArrayList<>();
        Button boton = (Button) findViewById(R.id.button);
       // Puntos();
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(MapsActivity.this, "JAJAJAJa", Toast.LENGTH_SHORT).show();
            }
        });
        //Puntos();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        miUbicacion();
        //Puntos();
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.getPosition();
                Punto = marker;
                Toast.makeText(MapsActivity.this,marker.getPosition().toString(),Toast.LENGTH_SHORT).show();
                return true;


            }
        });

        // Add a marker in Sydney and move the camera
        // LatLng CEI = new LatLng(-34.905407, -54.955797);
        // mMap.addMarker(new MarkerOptions().position(CEI).title("Marca en el CEI"));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(CEI));
    }

    private void AgregarMarca(double lat, double lng) {

        LatLng coordenadas = new LatLng(lat, lng);
        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 16);
        if (marcador != null) {
            marcador.remove();
        }
        marcador = mMap.addMarker(new MarkerOptions()
                        .position(coordenadas)
                        .title("Mi Posición")
                   /* .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher))*/);
        if (j == 0)
        {
            mMap.animateCamera(miUbicacion);
            j=1;
        }

    }

    private void actualizarUbicacion(Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            AgregarMarca(lat, lng);
        }
    }

    LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //Toast.makeText(MapsActivity.this, "Tu ubicacion ha cambiado", Toast.LENGTH_SHORT).show();
            actualizarUbicacion(location);
            Puntos();
            AgregarLugares();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private void miUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        actualizarUbicacion(location);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locListener);
    }
    private void Puntos() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        String URL = "http://192.168.1.43:3000/api/points?lng="+location.getLatitude()+"&lat="+location.getLongitude();
        JsonArrayRequest req = new JsonArrayRequest(URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject object = (JSONObject) response.get(i);
                                Lugar lugar = new Lugar();
                                lugar.setId(object.getInt("id"));
                                lugar.setDescription("description");
                                JSONObject location = object.getJSONObject("location");
                                JSONArray array = location.getJSONArray("coordinates");
                                double[] coords = new double[2];
                                coords[0] = (double) array.get(0);
                                coords[1] = (double) array.get(1);
                                lugar.setCoordenadas(coords);

                                lugares.add(lugar);
                                System.out.println(lugar.getDescription());

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(req);
    }

    private void AgregarLugares()
    {
        int i = 0;


        for (Lugar l : lugares) {
            double[] coordenadas = l.getCoordenadas();
            LatLng Posicion = new LatLng(coordenadas[0],coordenadas[1]);
            mMap.addMarker(new MarkerOptions().position(Posicion).title(l.getDescription()));
        }
    }
   /* private void Check()
    {
       // String URL = "http://10.0.2.2:3000/api/points?idPunto="+Punto.getId()+"&idUsuario="+Usuario+"/check";
        String idPunto= ultimoMarker.getSnippet();//mandamos los parametros a manopla
        final String url = "http://10.0.2.2:3000/api/points/check?idUsuario="+miUser.getId()+"&idPunto="+idPunto; // aca tenemos que pasar los parametros de donde estamos parados
        RequestQueue queue = Volley.newRequestQueue(this);
        mMap.clear();

        StringRequest checkPunto = new StringRequest(Request.Method.POST, url,


                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        cargarPuntos(lat,lng);
                        cargarScores();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        );
        queue.add(checkPunto);

    }*/
}

