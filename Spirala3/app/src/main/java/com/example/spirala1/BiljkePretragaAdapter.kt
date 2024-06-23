package com.example.spirala1

import android.content.Context
import android.util.Log
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

class BiljkePretragaAdapter(
    private var listaBiljki: List<Biljka?>, private var context: Context
): RecyclerView.Adapter<BiljkePretragaAdapter.BiljkeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BiljkeViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_biljka_bot, parent, false)
        return BiljkeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listaBiljki.size
    }

    private val scope = CoroutineScope(Job() + Dispatchers.Main)
    private var trefle= TrefleDAO().apply {
        setContext(context)
    }

    override fun onBindViewHolder(holder: BiljkeViewHolder, position: Int) {
        val biljka = listaBiljki[position]

        if (biljka!=null) {
            holder.biljkaName.text = biljka.naziv

            holder.biljkaPorodica.text = biljka.porodica

            scope.launch {
                var imageUrl = biljka?.let { trefle.getImage(it) }
                Glide.with(holder.itemView.context)
                    .load(imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.biljka)
                    .into(holder.biljkaImage)
            }



            if ((biljka.klimatskiTipovi?.size ?: 0) > 0) {
                holder.biljkaKlimatskiTip.text = biljka.klimatskiTipovi?.get(0)?.opis ?: ""
            }else{
                holder.biljkaKlimatskiTip.text = "N/A"
            }
            if ((biljka.zemljisniTipovi?.size ?: 0) > 0) {
                holder.biljkaZemljisniTip.text = biljka.zemljisniTipovi?.get(0)?.naziv ?: ""
            }else{
                holder.biljkaZemljisniTip.text = "N/A"
            }
        }
    }


    inner class BiljkeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val biljkaImage: ImageView = itemView.findViewById(R.id.slikaItem)
        val biljkaName: TextView = itemView.findViewById(R.id.nazivItem)
        val biljkaPorodica: TextView = itemView.findViewById(R.id.porodicaItem)
        val biljkaZemljisniTip: TextView = itemView.findViewById(R.id.zemljisniTipItem)
        val biljkaKlimatskiTip: TextView = itemView.findViewById(R.id.klimatskiTipItem)
    }
}
