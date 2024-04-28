package com.example.spirala1

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BiljkeBotAdapter(
    private var biljke: List<Biljka?>
): RecyclerView.Adapter<BiljkeBotAdapter.BiljkeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BiljkeViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_biljka_bot, parent, false)
        return BiljkeViewHolder(view)
    }

    override fun getItemCount(): Int = biljke.size

    override fun onBindViewHolder(holder: BiljkeViewHolder, position: Int) {
        holder.itemView.setOnClickListener{
            val filtriraneBiljke=filtriraj(biljke[position],biljke)
            biljke=filtriraneBiljke
            updateBiljke(filtriraneBiljke)
        }

        holder.biljkaName.text = biljke[position]?.naziv;
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
        if (id==0) id=context.resources
            .getIdentifier("biljka", "drawable", context.packageName)
        holder.biljkaImage.setImageResource(id)
        holder.biljkaPorodica.text= biljke[position]?.porodica ?: "";
        holder.biljkaKlimatskiTip.text= biljke[position]?.klimatskiTipovi?.get(0)?.opis ?: "";
        holder.biljkaZemljisniTip.text= biljke[position]?.zemljisniTipovi?.get(0)?.naziv ?: "";
    }

    fun updateBiljke(biljkeNew: List<Biljka?>) {
        /*this.biljke = biljkeNew
        notifyDataSetChanged()*/
        biljke=biljkeNew.toMutableList()
        notifyDataSetChanged();
    }

    inner class BiljkeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val biljkaImage: ImageView = itemView.findViewById(R.id.slikaItem)
        val biljkaName: TextView = itemView.findViewById(R.id.nazivItem)
        val biljkaPorodica: TextView = itemView.findViewById(R.id.porodicaItem)
        val biljkaZemljisniTip: TextView = itemView.findViewById(R.id.zemljisniTipItem)
        val biljkaKlimatskiTip: TextView = itemView.findViewById(R.id.klimatskiTipItem)
    }

    private fun filtriraj(trenutnaBiljka : Biljka?, listaBiljki: List<Biljka?>): MutableList<Biljka?> {
        val filteredLista = listaBiljki.filter { biljkaIzListe ->
            biljkaIzListe?.klimatskiTipovi?.any { klimaIzListe ->
                trenutnaBiljka?.klimatskiTipovi?.any { trenutnaKlima, ->
                    trenutnaKlima == klimaIzListe && biljkaIzListe?.zemljisniTipovi?.any { zemljaIzListe->
                        trenutnaBiljka.zemljisniTipovi?.any { trenutnaZemlja ->
                            trenutnaZemlja==zemljaIzListe && trenutnaBiljka?.porodica==biljkaIzListe.porodica
                        } ?: true
                    } ?: true
                } ?: true
            } ?: true
        }.toMutableList()
        return filteredLista
    }
}
