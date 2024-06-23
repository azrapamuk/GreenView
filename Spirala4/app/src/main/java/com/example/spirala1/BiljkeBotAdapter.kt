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

class BiljkeBotAdapter(
    private var biljke: List<Biljka?>, private var context: Context
): RecyclerView.Adapter<BiljkeBotAdapter.BiljkeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BiljkeViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_biljka_bot, parent, false)
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

        if (biljka != null) {
            holder.biljkaPorodica.text=biljka.porodica
        }

        if (biljka != null) {
            holder.biljkaKlimatskiTip.text= biljka.klimatskiTipovi?.get(0)?.opis ?: ""
        };
        if (biljka != null) {
            holder.biljkaZemljisniTip.text= biljka.zemljisniTipovi?.get(0)?.naziv ?: ""
        };

        holder.itemView.setOnClickListener{
            val filtriraneBiljke=filtriraj(biljka,listaBiljki)
            updateBiljke(filtriraneBiljke.toList())
        }

        val biljkaDAO = BiljkaDatabase.getDatabase(context).biljkaDao()

        var id = biljka?.id
        CoroutineScope(Dispatchers.Main).launch {
            var slikaBitmap = id?.let { biljkaDAO.getBitmap(it) }
            if (slikaBitmap==null) {
                val webImg = biljka?.let { trefle.getImage(it) }
                if (webImg != null && id!=null) {
                    biljkaDAO.addImage(id,webImg)
                }
                slikaBitmap = id?.let { biljkaDAO.getBitmap(it) }
            }
            holder.biljkaImage.setImageBitmap(slikaBitmap)
        }
    }

    fun updateBiljke(biljkeNew: List<Biljka?>) {
        listaBiljki= biljkeNew.toMutableList()
        notifyDataSetChanged()
    }

    fun returnBiljke(): List<Biljka?> {
        return listaBiljki
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
                trenutnaBiljka?.klimatskiTipovi?.any { trenutnaKlima ->
                    trenutnaKlima == klimaIzListe && biljkaIzListe.zemljisniTipovi?.any { zemljaIzListe->
                        trenutnaBiljka.zemljisniTipovi?.any { trenutnaZemlja ->
                            trenutnaZemlja==zemljaIzListe && trenutnaBiljka.porodica ==biljkaIzListe.porodica
                        } ?: true
                    } ?: true
                } ?: true
            } ?: true
        }.toMutableList()
        return filteredLista
    }
}
