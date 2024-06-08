package com.example.spirala1

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class BiljkeKuhAdapter(
    private var biljke: List<Biljka?>, private var context: Context
): RecyclerView.Adapter<BiljkeKuhAdapter.BiljkeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BiljkeViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_biljka_kuh, parent, false)
        return BiljkeViewHolder(view)
    }

    private val scope = CoroutineScope(Job() + Dispatchers.Main)
    private var trefle= TrefleDAO().apply {
        setContext(context)
    }

    private var listaBiljki: List<Biljka?> = biljke

    override fun getItemCount(): Int = listaBiljki.size

    override fun onBindViewHolder(holder: BiljkeViewHolder, position: Int) {

        val biljka = listaBiljki[position]

        if (biljka != null) {
            holder.biljkaName.text = biljka.naziv
        }
        var p: String = biljka?.naziv.toString()
        val temp=p
        val i=temp.indexOf('(');
        p = if (i != -1) {
            temp.substring(0, i-1).trim().lowercase()
        } else {
            temp
        }

        scope.launch {
            var imageUrl = biljka?.let { trefle.getImage(it) }
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .centerCrop()
                .placeholder(R.drawable.biljka)
                .into(holder.biljkaImage)
        }

        if (biljka != null) {
            holder.biljkaOkus.text=biljka.profilOkusa.opis
        }
        if (biljka != null) {
            if((biljka.jela?.size ?: 0) > 0) {
                holder.biljkaJelo1.text = biljka.jela?.get(0) ?:""
            }
            else{
                holder.biljkaJelo1.text ="N/A"
            }
        }
        if (biljka != null) {
            if((biljka.jela?.size ?: 0) > 1) {
                holder.biljkaJelo2.text = biljka.jela?.get(1) ?:""
            }
            else{
                holder.biljkaJelo2.text ="N/A"
            }
        }
        if (biljka != null) {
            if((biljka.jela?.size ?: 0) > 2) {
                holder.biljkaJelo3.text = biljka.jela?.get(2) ?: ""
            }
            else{
                holder.biljkaJelo3.text ="N/A"
            }
        }


        holder.itemView.setOnClickListener{
            val filtriraneBiljke=filtriraj(biljka,listaBiljki)
            updateBiljke(filtriraneBiljke.toList())
        }
    }

    fun updateBiljke(biljkeNew: List<Biljka?>) {
        listaBiljki = biljkeNew
        notifyDataSetChanged()
    }

    fun returnBiljke(): List<Biljka?>{
        return listaBiljki
    }

    inner class BiljkeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val biljkaImage: ImageView = itemView.findViewById(R.id.slikaItem)
        val biljkaName: TextView = itemView.findViewById(R.id.nazivItem)
        val biljkaOkus: TextView = itemView.findViewById(R.id.profilOkusaItem)
        val biljkaJelo1: TextView = itemView.findViewById(R.id.jelo1Item)
        val biljkaJelo2: TextView = itemView.findViewById(R.id.jelo2Item)
        val biljkaJelo3: TextView = itemView.findViewById(R.id.jelo3Item)
    }
    private fun filtriraj(trenutnaBiljka : Biljka?, listaBiljki: List<Biljka?>): MutableList<Biljka?> {
        val filteredLista = listaBiljki.filter { biljkaIzListe ->
            biljkaIzListe?.jela?.any { jelaIzListe ->
                trenutnaBiljka?.jela?.any { trenutnoJelo ->
                    trenutnoJelo == jelaIzListe || biljkaIzListe.profilOkusa.opis==trenutnaBiljka.profilOkusa.opis
                } ?: true
            } ?: true
        }.toMutableList()
        return filteredLista
    }
}
