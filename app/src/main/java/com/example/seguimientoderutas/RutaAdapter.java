package com.example.seguimientoderutas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RutaAdapter extends RecyclerView.Adapter<RutaAdapter.RutaViewHolder> implements Filterable {

    private List<Ruta> listaRutas;
    private List<Ruta> listaRutasFiltradas;
    private List<Ruta> listaRutasOriginal;
    private RutaFilter filter;

    public RutaAdapter(List<Ruta> listaRutas) {
        this.listaRutas = listaRutas;
        this.listaRutasFiltradas = new ArrayList<>(listaRutas);
        this.listaRutasOriginal = new ArrayList<>(listaRutas);
    }
    @NonNull
    @Override
    public RutaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_ruta, parent, false);
        return new RutaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RutaViewHolder holder, int position) {
        Ruta ruta = listaRutasFiltradas.get(position);
        holder.bind(ruta);
    }

    @Override
    public int getItemCount() {
        return listaRutasFiltradas.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new RutaFilter();
        }
        return filter;
    }

    public class RutaViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewNombreRuta;
        private TextView textViewUbicacionInicial;
        private TextView textViewUbicacionFinal;

        public RutaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombreRuta = itemView.findViewById(R.id.textViewNombreRuta);
            textViewUbicacionInicial = itemView.findViewById(R.id.textViewUbicacionInicial);
            textViewUbicacionFinal = itemView.findViewById(R.id.textViewUbicacionFinal);
        }

        public void bind(Ruta ruta) {
            // Muestra los datos de la ruta en los TextView
            textViewNombreRuta.setText(ruta.getNombre());
            textViewUbicacionInicial.setText("Ubicación Inicial: " + ruta.getUbicacionInicial().toString());
            textViewUbicacionFinal.setText("Ubicación Final: " + ruta.getUbicacionFinal().toString());
        }
    }
    public void mostrarTodasLasRutas() {
        listaRutasFiltradas.clear();
        listaRutasFiltradas.addAll(listaRutas);
        notifyDataSetChanged();
    }
    private class RutaFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<Ruta> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                // No hay filtro, mostrar la lista completa
                filteredList.addAll(listaRutas);
            } else {
                // Aplicar el filtro
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Ruta ruta : listaRutas) {
                    String nombre = ruta.getNombre().toLowerCase();
                    String ubicacionInicial = ruta.getUbicacionInicial().toString().toLowerCase();
                    String ubicacionFinal = ruta.getUbicacionFinal().toString().toLowerCase();

                    if (ruta.getNombre().toLowerCase().contains(filterPattern)) {
                        filteredList.add(ruta);
                    }
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listaRutasFiltradas.clear();
            listaRutasFiltradas.addAll((List<Ruta>) results.values);
            notifyDataSetChanged();
        }
    }
}
