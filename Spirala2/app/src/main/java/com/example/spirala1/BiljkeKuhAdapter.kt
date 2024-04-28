package com.example.spirala1

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BiljkeKuhAdapter(
    private var biljke: List<Biljka?>
): RecyclerView.Adapter<BiljkeKuhAdapter.BiljkeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BiljkeViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_biljka_kuh, parent, false)
        return BiljkeViewHolder(view)
    }

    override fun getItemCount(): Int = biljke.size

    override fun onBindViewHolder(holder: BiljkeViewHolder, position: Int) {

        holder.itemView.setOnClickListener{
            val filtriraneBiljke=filtriraj(biljke[position],biljke)
            updateBiljke(filtriraneBiljke)
        }

        holder.biljkaName.text = biljke[position]?.naziv ?: ""
        var p: String? = biljke[position]?.naziv
        val temp=p
        val i= temp?.indexOf('(');
        if (temp != null) {
            if (i != null) {
                p = if (i != -1) {
                    temp.substring(0, i-1).trim().lowercase()
                } else {
                    temp
                }
            }
        }
        val picMatch= p?.replace("ž", "z")?.replace("č", "c")
            ?.replace("ć", "c")?.replace("dž","dz")?.
            replace("đ","dj")?.replace(" ","")

        val context: Context = holder.biljkaImage.context
        var id: Int = context.resources
            .getIdentifier(picMatch, "drawable", context.packageName)
        if (id==0) id=context.resources.getIdentifier("biljka", "drawable", context.packageName)
        holder.biljkaImage.setImageResource(id)
        holder.biljkaOkus.text= biljke[position]?.profilOkusa?.opis ?:""
        if(biljke[position]?.jela?.size ?: 0>0) {
            holder.biljkaJelo1.text = biljke[position]?.jela?.get(0) ?: ""
        }
        else{
            holder.biljkaJelo1.text ="N/A"
        }
        if(biljke[position]?.jela?.size ?:0 >1) {
            holder.biljkaJelo2.text = biljke[position]?.jela?.get(1) ?: ""
        }
        else{
            holder.biljkaJelo2.text ="N/A"
        }
        if(biljke[position]?.jela?.size ?:0 >2) {
            holder.biljkaJelo3.text = biljke[position]?.jela?.get(2) ?: ""
        }
        else{
            holder.biljkaJelo3.text ="N/A"
        }
    }

    fun updateBiljke(biljkeNew: List<Biljka?>) {
        this.biljke = biljkeNew
        notifyDataSetChanged()
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
                    trenutnoJelo == jelaIzListe && biljkaIzListe.profilOkusa==trenutnaBiljka.profilOkusa
                } ?: true
            } ?: true
        }.toMutableList()
        return filteredLista
    }
}
