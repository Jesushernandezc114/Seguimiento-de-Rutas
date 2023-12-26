package com.example.seguimientoderutas;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LocationListActivity extends AppCompatActivity {

    private static final String TAG = "LocationListActivity";

    private RecyclerView recyclerView;
    private List<Ruta> listaRutas;
    private RutaAdapter rutaAdapter;

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listaRutas = new ArrayList<>();
        rutaAdapter = new RutaAdapter(listaRutas);
        recyclerView.setAdapter(rutaAdapter);
        searchView = findViewById(R.id.searchView);
        rutaAdapter.mostrarTodasLasRutas();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Aquí puedes realizar acciones al presionar el botón de búsqueda, si es necesario.
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filtrar la lista al cambiar el texto en el SearchView
                if (TextUtils.isEmpty(newText)) {
                    // Si el texto está vacío, mostrar todas las ruta
                    rutaAdapter.mostrarTodasLasRutas();

                } else {
                    // Filtrar las rutas por nombre
                    rutaAdapter.getFilter().filter(newText);
                }
                return true;
            }

        });
        // Cargar las rutas desde Firebase
        cargarRutasDesdeFirebase();

    }


    private void mostrarMensajeListaVacia() {
        Log.d(TAG, "mostrarMensajeListaVacia: Mostrando mensaje de lista vacía");
        Toast.makeText(this, "No hay rutas disponibles", Toast.LENGTH_SHORT).show();
    }

    private void cargarRutasDesdeFirebase() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference rutasRef = FirebaseDatabase.getInstance().getReference("rutas").child(userId);

        rutasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    Log.d(TAG, "Número de rutas encontradas: " + dataSnapshot.getChildrenCount());

                    listaRutas.clear(); // Limpiar la lista antes de agregar nuevas rutas

                    for (DataSnapshot rutaSnapshot : dataSnapshot.getChildren()) {
                        try {
                            Ruta ruta = rutaSnapshot.getValue(Ruta.class);
                            if (ruta != null) {
                                listaRutas.add(ruta);
                            } else {
                                Log.w(TAG, "Ruta es null para el snapshot: " + rutaSnapshot.getKey());
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Excepción al procesar rutaSnapshot: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    Log.d(TAG, "Número de rutas después de procesamiento: " + listaRutas.size());

                    rutaAdapter.notifyDataSetChanged();

                    if (listaRutas.isEmpty()) {
                        mostrarMensajeListaVacia();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Excepción no manejada en onDataChange: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                try {
                    Log.e(TAG, "Error al leer datos desde Firebase: " + databaseError.getMessage());
                } catch (Exception e) {
                    Log.e(TAG, "Excepción no manejada en onCancelled: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
}
