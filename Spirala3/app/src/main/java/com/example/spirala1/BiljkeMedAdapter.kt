package com.example.spirala1

import android.content.Context
import android.graphics.Bitmap
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

class BiljkeMedAdapter(
    private var biljke: List<Biljka?>, private val context: Context
): RecyclerView.Adapter<BiljkeMedAdapter.BiljkeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BiljkeViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_biljka_med, parent, false)
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
        val temp = p
        val i = temp.indexOf('(')
        p = if (i != -1) {
            temp.substring(0, i - 1).trim().lowercase()
        } else {
            temp
        }

        if (biljka != null) {
            holder.biljkaUpozorenje.text = biljka.medicinskoUpozorenje
        }

        scope.launch {
            var imageUrl = biljka?.let { trefle.getImage(it) }
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .centerCrop()
                .placeholder(R.drawable.biljka)
                .into(holder.biljkaImage)
        }

        var tekst = biljka?.medicinskeKoristi?.get(0)?.opis
        var index = tekst?.indexOf('-');
        var extractedTekst = if (index != -1) {
            if (index != null) {
                tekst?.substring(0, index)?.trim()
            } else {
                TODO("nema")
            }
        } else {
            tekst
        }
        holder.biljkaKorist1.text = extractedTekst
        if (biljka != null) {
            if ((biljka.medicinskeKoristi?.size ?: 0) > 1) {
                tekst = biljka.medicinskeKoristi?.get(1)?.opis
                if (tekst != null) {
                    index = tekst.indexOf('-')
                };
                if (tekst != null) {
                    extractedTekst = if (index != -1) {
                        tekst.substring(0, index).trim()
                    } else {
                        tekst
                    }
                }
                holder.biljkaKorist2.text = extractedTekst

                if ((biljka.medicinskeKoristi?.size ?: 0) > 2) {
                    tekst = biljka.medicinskeKoristi?.get(2)?.opis
                    if (tekst != null) {
                        index = tekst.indexOf('-')
                    };
                    if (tekst != null) {
                        extractedTekst = if (index != -1) {
                            tekst.substring(0, index).trim()
                        } else {
                            tekst
                        }
                    }
                    holder.biljkaKorist3.text = extractedTekst
                } else {
                    holder.biljkaKorist3.text = "N/A"
                }
            } else {
                holder.biljkaKorist2.text = "N/A"
                holder.biljkaKorist3.text = "N/A"
            }
        }

        holder.itemView.setOnClickListener {
            val filtriraneBiljke = filtriraj(biljka, listaBiljki)
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

    inner class BiljkeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val biljkaImage: ImageView = itemView.findViewById(R.id.slikaItem)
        val biljkaName: TextView = itemView.findViewById(R.id.nazivItem)
        val biljkaUpozorenje: TextView = itemView.findViewById(R.id.upozorenjeItem)
        val biljkaKorist1: TextView = itemView.findViewById(R.id.korist1Item)
        val biljkaKorist2: TextView = itemView.findViewById(R.id.korist2Item)
        val biljkaKorist3: TextView = itemView.findViewById(R.id.korist3Item)
    }

    private fun filtriraj(trenutnaBiljka : Biljka?, listaBiljki: List<Biljka?>): MutableList<Biljka?> {
        val filteredLista = listaBiljki.filter { biljkaIzListe ->
            biljkaIzListe?.medicinskeKoristi?.any { koristIzListe ->
                trenutnaBiljka?.medicinskeKoristi?.any { trenutnaKorist ->
                    trenutnaKorist.opis == koristIzListe.opis
                } ?: true
            } ?: true
        }.toMutableList()
        return filteredLista
    }
}
