package com.example.seguimientoderutas;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private List<UbicacionPunto> puntosDeRuta;
    private UbicacionPunto ubicacionInicial;
    private UbicacionPunto ubicacionFinal;
    private boolean rutaEnProgreso;

    private FusedLocationProviderClient fusedLocationClient;
    private Location lastKnownLocation;
    private LocationCallback locationCallback;
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Ruta rutaSeleccionada = (Ruta) getIntent().getSerializableExtra("rutaSeleccionada");
        // Inicializar la lista de puntos de la ruta
        puntosDeRuta = new ArrayList<>();

        // Inicializar el mapa
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Si los permisos no están otorgados, solicitarlos al usuario
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            // Si los permisos están otorgados, proceder con la lógica relacionada con la ubicación
            iniciarMapa();
        }

        // Inicializar el estado de la ruta
        rutaEnProgreso = false;

        // Inicializar el proveedor de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Configurar los botones
        Button btnIniciarRuta = findViewById(R.id.btnIniciarRuta);
        Button btnPausarRuta = findViewById(R.id.btnPausarRuta);
        Button btnReanudarRuta = findViewById(R.id.btnReanudarRuta);
        Button btnTerminarRuta = findViewById(R.id.btnTerminarRuta);

        btnIniciarRuta.setOnClickListener(v -> iniciarRuta());
        btnPausarRuta.setOnClickListener(v -> pausarRuta());
        btnReanudarRuta.setOnClickListener(v -> reanudarRuta());
        btnTerminarRuta.setOnClickListener(v -> terminarRuta());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Configurar el mapa según sea necesario
        // ...

        // Obtener la ubicación actual al inicio de la actividad
        obtenerUbicacionActual();
    }

    private void obtenerUbicacionActual() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        lastKnownLocation = location; // Almacena la última ubicación conocida
                        LatLng ubicacionActual = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.addMarker(new MarkerOptions().position(ubicacionActual).title("Ubicación Actual"));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionActual, 15));
                    }
                });
    }

    private void iniciarRuta() {
        // Lógica para iniciar la grabación de la ruta
        rutaEnProgreso = true;
        puntosDeRuta.clear();
        googleMap.clear(); // Limpiar los marcadores existentes en el mapa


        // Obtener la ubicación actual al inicio de la ruta
        obtenerUbicacionActual();

        // Iniciar actualizaciones de ubicación durante la ruta
        iniciarActualizacionesUbicacion();

        // Guardar la ubicación inicial
        ubicacionInicial = new UbicacionPunto(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
    }


    private void pausarRuta() {
        // Lógica para pausar la grabación de la ruta
        rutaEnProgreso = false;
    }

    private void reanudarRuta() {
        // Lógica para reanudar la grabación de la ruta
        rutaEnProgreso = true;
    }

    private void terminarRuta() {
        // Lógica para terminar la grabación de la ruta
        rutaEnProgreso = false;

        // Detener actualizaciones de ubicación
        detenerActualizacionesUbicacion();

        // Obtener la última ubicación conocida como ubicación final
        obtenerUltimaUbicacionConocida();

        // Al finalizar la grabación de la ruta, muestra el diálogo para ingresar el nombre de la ruta
        mostrarDialogoNombreRuta();
    }

    private void obtenerUltimaUbicacionConocida() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            ubicacionFinal = new UbicacionPunto(location.getLatitude(), location.getLongitude());
                        }
                    });
        }
    }

    private void iniciarActualizacionesUbicacion() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000); // Intervalo de actualización de ubicación en milisegundos (5 segundos en este ejemplo)
        locationRequest.setFastestInterval(3000); // Intervalo más rápido en caso de disponibilidad de ubicación más rápida
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                try {
                    if (locationResult != null) {
                        for (Location location : locationResult.getLocations()) {
                            // Añadir la ubicación a la lista de puntos de ruta y actualizar el marcador en el mapa
                            agregarPuntoDeRuta(location);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling ActivityCompat#requestPermissions here to request the missing permissions.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }


    private void detenerActualizacionesUbicacion() {
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    private void agregarPuntoDeRuta(Location location) {
        if (rutaEnProgreso) {
            LatLng ubicacion = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(ubicacion).title("Nueva Ubicación"));
            puntosDeRuta.add(new UbicacionPunto(location.getLatitude(), location.getLongitude()));
        }
    }

    private void guardarRutaConNombre(String nombreRuta) {
        // Obtener el ID del usuario actual desde Firebase Authentication
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Crear una instancia de la clase Ruta
        Ruta ruta = new Ruta(nombreRuta, puntosDeRuta, ubicacionInicial, ubicacionFinal);

        // Obtén la referencia a la base de datos de Firebase
        DatabaseReference rutasRef = FirebaseDatabase.getInstance().getReference("rutas").child(userId);

        // Genera una nueva clave única para la ruta
        String rutaId = rutasRef.push().getKey();

        // Guarda la ruta en Firebase utilizando la clave generada
        rutasRef.child(rutaId).setValue(ruta);

        // Notifica al usuario que la ruta se guardó con éxito o realiza cualquier acción necesaria
        Toast.makeText(this, "Ruta guardada con éxito", Toast.LENGTH_SHORT).show();
    }

    private void mostrarDialogoNombreRuta() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ingrese el nombre de la ruta");

        // Crear un EditText en el cuadro de diálogo
        final EditText input = new EditText(this);
        builder.setView(input);

        // Configurar botones del cuadro de diálogo
        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nombreRuta = input.getText().toString();
                if (!nombreRuta.isEmpty()) {
                    // El usuario ingresó un nombre, guarda la ruta con ese nombre
                    guardarRutaConNombre(nombreRuta);
                } else {
                    // El usuario no ingresó un nombre, puedes manejarlo según tus requisitos
                    Toast.makeText(MapsActivity.this, "Debe ingresar un nombre para la ruta", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            // Verificar si el usuario otorgó los permisos
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Los permisos fueron otorgados, proceder con la lógica relacionada con la ubicación
                iniciarMapa();
            } else {
                // Los permisos fueron denegados, puedes mostrar un mensaje al usuario o realizar alguna acción
                Toast.makeText(this, "Los permisos de ubicación son necesarios para esta aplicación.", Toast.LENGTH_SHORT).show();
                // Puedes cerrar la actividad si no es posible continuar sin permisos
                finish();
            }
        }
    }
        private void iniciarMapa () {
            // Inicializar el mapa
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }
        }
    }
