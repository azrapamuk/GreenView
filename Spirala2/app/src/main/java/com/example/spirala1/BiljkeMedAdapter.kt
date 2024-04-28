package com.example.spirala1

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BiljkeMedAdapter(
    private var biljke: List<Biljka?>,
    ): RecyclerView.Adapter<BiljkeMedAdapter.BiljkeViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BiljkeViewHolder {
            val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_biljka_med, parent, false)
            return BiljkeViewHolder(view)
        }

        override fun getItemCount(): Int = biljke.size

        override fun onBindViewHolder(holder: BiljkeViewHolder, position: Int) {
            holder.biljkaName.text = biljke[position]?.naziv ?: ""

            var p: String ?= biljke[position]?.naziv
            val temp=p
            val i= temp?.indexOf('(')
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
            var id: Int = context.resources.getIdentifier(picMatch, "drawable", context.packageName)
            if (id==0) id=context.resources.getIdentifier("biljka", "drawable", context.packageName)
            holder.biljkaImage.setImageResource(id)
            holder.biljkaUpozorenje.text= biljke[position]?.medicinskoUpozorenje ?: "";

            var tekst= biljke[position]?.medicinskeKoristi?.get(0)?.opis
            var index= tekst?.indexOf('-');
            var extractedTekst = if (index != -1) {
                if (index != null) {
                    tekst?.substring(0, index)?.trim()
                } else {

                }
            } else {
                tekst
            }
            holder.biljkaKorist1.text= extractedTekst.toString()

            if ((biljke[position]?.medicinskeKoristi?.size ?: 0) > 1) {
                tekst = biljke[position]?.medicinskeKoristi?.get(1)?.opis
                if (tekst != null) {
                    index = tekst.indexOf('-')
                };
                if (tekst != null) {
                    extractedTekst = if (index != -1) {
                        if (index != null) {
                            tekst.substring(0, index).trim()
                        } else {

                        }
                    } else {
                        tekst
                    }
                }
                holder.biljkaKorist2.text = extractedTekst.toString()

                if ((biljke[position]?.medicinskeKoristi?.size ?: 0) > 2) {
                    tekst = biljke[position]?.medicinskeKoristi?.get(2)?.opis
                    if (tekst != null) {
                        index = tekst.indexOf('-')
                    };
                    if (tekst != null) {
                        extractedTekst = if (index != -1) {
                            if (index != null) {
                                tekst.substring(0, index).trim()
                            } else {

                            }
                        } else {
                            tekst
                        }
                    }
                    holder.biljkaKorist3.text = extractedTekst.toString()
                }
                else{
                    holder.biljkaKorist3.text="N/A"
                }
            }
            else{
                holder.biljkaKorist2.text="N/A"
                holder.biljkaKorist3.text="N/A"
            }

            holder.itemView.setOnClickListener{
                val filtriraneBiljke=filtriraj(biljke[position],biljke)
                updateBiljke(filtriraneBiljke)
            }
        }
        fun updateBiljke(biljkeNew: List<Biljka?>) {
            this.biljke = biljkeNew
            notifyDataSetChanged()
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
                        trenutnaKorist == koristIzListe
                    } ?: true
                } ?: true
            }.toMutableList()
            return filteredLista
        }
    }
