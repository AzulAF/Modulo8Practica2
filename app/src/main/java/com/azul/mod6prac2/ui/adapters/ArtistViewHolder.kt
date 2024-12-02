package com.azul.mod6prac2.ui.adapters

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.azul.mod6prac2.R
import com.azul.mod6prac2.data.remote.model.ArtistDto
import com.azul.mod6prac2.databinding.ArtistElementBinding
import com.bumptech.glide.Glide


class ArtistViewHolder(
    private val binding: ArtistElementBinding
): RecyclerView.ViewHolder(binding.root) {


    fun bind(item: ArtistDto){
        binding.apply {
            tvMerch.text = item.nombre
            tvTable.text = item.mesa
            tvTableTEXT.text = binding.root.context.getString(R.string.mesa)
            tvArtist.visibility = View.INVISIBLE
            tvCost.visibility = View.INVISIBLE
            tvTag.text = binding.root.context.getString(R.string.piso)+ item.piso
            tvMerchType.visibility = View.INVISIBLE
            tvMerchPriority.visibility = View.INVISIBLE
        }
        Glide.with(binding.root.context).load(item.imagen).into(binding.ivIcon)

    }
}