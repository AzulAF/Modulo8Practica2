package com.azul.mod6prac2.ui.adapters

import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.azul.mod6prac2.R
import com.azul.mod6prac2.data.remote.model.ArtistDto
import com.azul.mod6prac2.databinding.ArtistElementBinding

class ArtistAdapter(
    private val artists: MutableList<ArtistDto>,
    private val onArtistClicked: (ArtistDto) -> Unit
): RecyclerView.Adapter<ArtistViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val binding = ArtistElementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArtistViewHolder(binding)
    }

    override fun getItemCount(): Int = artists.size

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {


        val artist = artists[position]

        holder.bind(artist)

        holder.itemView.setOnClickListener {

            //Para los clicks a los juegos

            onArtistClicked(artist)
        }

    }


}