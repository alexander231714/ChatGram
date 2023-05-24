package com.loschidos.chatgram.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loschidos.chatgram.R;
import com.loschidos.chatgram.activities.FiltersActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class FiltersFragment extends Fragment {

    View mView;
    CardView mCardViewDeportes, mCardViewJuegos, mCardViewMusica, mCardViewPeliculas;

    public FiltersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_filters, container, false);
        mCardViewDeportes = mView.findViewById(R.id.cardViewDeportes);
        mCardViewJuegos = mView.findViewById(R.id.cardViewJuegos);
        mCardViewMusica = mView.findViewById(R.id.cardViewMusica);
        mCardViewPeliculas = mView.findViewById(R.id.cardViewPeliculas);

        mCardViewDeportes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFilterActivti("DEPORTE");
            }
        });

        mCardViewJuegos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFilterActivti("JUEGOS");
            }
        });

        mCardViewMusica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFilterActivti("MUSICA");
            }
        });

        mCardViewPeliculas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFilterActivti("PELICULAS");
            }
        });
        return mView;
    }

    private void goToFilterActivti(String categoria){
        Intent intent = new Intent(getContext(), FiltersActivity.class);
        intent.putExtra("categoria", categoria);
        startActivity(intent);
    }
}