package com.example.spirala1

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.provider.MediaStore
import android.widget.Button
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.ActivityNotFoundException
import android.util.Log

class NovaBiljkaActivity : AppCompatActivity() {

    private lateinit var zemljistaList: ListView
    private lateinit var medicinskaKoristList: ListView
    private lateinit var klimaList: ListView
    private lateinit var okusList: ListView

    private lateinit var jelaListView: ListView
    private lateinit var jelaAdapter: ArrayAdapter<String>
    private lateinit var jeloEditText: EditText
    private lateinit var dodajJeloBtn: Button

    private lateinit var dodajBiljkuBtn:Button

    private lateinit var slika: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nova_biljka)

        zemljistaList= findViewById(R.id.zemljisniTipLV)
        zemljistaList.choiceMode=ListView.CHOICE_MODE_MULTIPLE
        medicinskaKoristList=findViewById(R.id.medicinskaKoristLV)
        medicinskaKoristList.choiceMode=ListView.CHOICE_MODE_MULTIPLE
        klimaList=findViewById(R.id.klimatskiTipLV)
        klimaList.choiceMode=ListView.CHOICE_MODE_MULTIPLE
        okusList=findViewById(R.id.profilOkusaLV)
        okusList.choiceMode=ListView.CHOICE_MODE_SINGLE

        var enumValues1 = Zemljiste.entries
        var stringValues = enumValues1.map { it.naziv }
        var adapter = EnumAdapter(this, stringValues)
        zemljistaList.adapter = adapter

        var enumValues2 = MedicinskaKorist.entries
        stringValues = enumValues2.map { it.opis }
        adapter = EnumAdapter(this, stringValues)
        medicinskaKoristList.adapter = adapter

        var enumValues3 = KlimatskiTip.entries
        stringValues = enumValues3.map { it.opis }
        adapter = EnumAdapter(this, stringValues)
        klimaList.adapter = adapter

        var enumValues4 = ProfilOkusaBiljke.entries
        stringValues = enumValues4.map { it.opis }
        adapter = EnumAdapter(this, stringValues)
        okusList.adapter = adapter

        jelaListView = findViewById(R.id.jelaLV)
        jeloEditText = findViewById(R.id.jeloET)
        dodajJeloBtn = findViewById(R.id.dodajJeloBtn)

        val jela = ArrayList<String>()
        jelaAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, jela)
        jelaListView.adapter = jelaAdapter
        var odabranoJelo:String

        jelaListView.setOnItemClickListener { _, _, position, _ ->
            odabranoJelo = jela[position]
            jeloEditText.setText(odabranoJelo)
            dodajJeloBtn.text = "Izmijeni jelo"
        }
        dodajJeloBtn.setOnClickListener {
            dodajJeloBtn.setError(null)
            val novoJelo = jeloEditText.text.toString().trim()
            if (dodajJeloBtn.text=="Izmijeni jelo"){
                val odabranoJeloIndex = jelaListView.checkedItemPosition
                if(novoJelo.isNotEmpty()){
                    if (novoJelo.length<2){
                        jeloEditText.setError("Naziv prekratak")
                    }
                    else if (novoJelo.length>20){
                        jeloEditText.setError("Naziv predug")
                    }
                    else {
                        val contains = jela.any { it.lowercase() == novoJelo.lowercase() }
                        if (!contains) {
                            jela[odabranoJeloIndex]=novoJelo
                        }
                        else{
                            jeloEditText.setError("Jelo vec postoji")
                        }
                    }
                }
                else{
                    jela.removeAt(odabranoJeloIndex)
                }
                jelaAdapter.notifyDataSetChanged()
                jeloEditText.setText("")
                dodajJeloBtn.text = "Dodaj jelo"
            }
            else {
                if (novoJelo.length<2){
                    jeloEditText.setError("Naziv prekratak")
                }
                else if (novoJelo.length>20){
                    jeloEditText.setError("Naziv predug")
                }
                else{
                    val contains = jela.any { it.lowercase() == novoJelo.lowercase() }
                    if (!contains) {
                        jela.add(novoJelo)
                        jelaAdapter.notifyDataSetChanged()
                        jeloEditText.setText("")
                    }
                    else{
                        jeloEditText.setError("Jelo vec postoji")
                    }
                }
            }
        }

        var klimaValues= ArrayList<KlimatskiTip>()

        klimaList.setOnItemClickListener { _, view, position, _ ->
            val selected = klimaList.isItemChecked(position)
            view.setBackgroundColor(if (selected) Color.DKGRAY else Color.TRANSPARENT)
            val adapter1 = klimaList.adapter
            val selectedItem = adapter1.getItem(position)
            for(i in KlimatskiTip.entries){
                if (i.opis==selectedItem){
                    if (klimaValues.contains(i)) klimaValues.remove(i)
                    else klimaValues.add(i)
                }
            }
        }

        var medKoristValues= ArrayList<MedicinskaKorist>()

        medicinskaKoristList.setOnItemClickListener { _, view, position, _ ->
            val selected = medicinskaKoristList.isItemChecked(position)
            view.setBackgroundColor(if (selected) Color.DKGRAY else Color.TRANSPARENT)
            val adapter1 = medicinskaKoristList.adapter
            val selectedItem = adapter1.getItem(position)
            for(i in MedicinskaKorist.entries){
                if (i.opis==selectedItem){
                    if (medKoristValues.contains(i)) medKoristValues.remove(i)
                    else medKoristValues.add(i)
                }
            }
        }

        var zemljisteValues= ArrayList<Zemljiste>()

        zemljistaList.setOnItemClickListener { _, view, position, _ ->
            val selected = zemljistaList.isItemChecked(position)
            view.setBackgroundColor(if (selected) Color.DKGRAY else Color.TRANSPARENT)
            val adapter1 = zemljistaList.adapter
            val selectedItem = adapter1.getItem(position)
            for(i in Zemljiste.entries){
                if (i.naziv==selectedItem){
                    if (zemljisteValues.contains(i)) zemljisteValues.remove(i)
                    else zemljisteValues.add(i)
                }
            }
        }

        var okusValue:ProfilOkusaBiljke=ProfilOkusaBiljke.GORKO
        var s=false


        okusList.setOnItemClickListener { _, view, position, _ ->
            val selected = okusList.isItemChecked(position)
            val adapter1 = okusList.adapter
            for (i in 0 until adapter1.count) {
                val item = adapter.getItem(i)
                view.setBackgroundColor(Color.TRANSPARENT)
            }
            view.setBackgroundColor(if (selected) Color.DKGRAY else Color.TRANSPARENT)
            val selectedItem = adapter1.getItem(position)
            for(i in ProfilOkusaBiljke.entries){
                if (i.opis==selectedItem) {
                    okusValue=i
                    s=true
                }
            }
        }



        dodajBiljkuBtn=findViewById(R.id.dodajBiljkuBtn)
        dodajBiljkuBtn.setOnClickListener{
            var add=true
            dodajBiljkuBtn.setError(null)
            add=(handleEditText() && handleJelaLV(jela))
            if (klimaValues.size<1){
                add=false
                dodajBiljkuBtn.setError("Odaberite barem jedan klimatski tip")
            }
            if (medKoristValues.size<1){
                add=false
                dodajBiljkuBtn.setError("Odaberite barem jednu medicinsku korist")
            }
            if (zemljisteValues.size<1){
                add=false
                dodajBiljkuBtn.setError("Odaberite barem jedan zemljisni tip")
            }
            if (s==false){
                add=false
                dodajBiljkuBtn.setError("Odaberite samo jedan profil okusa")
            }

            if (add){
                var n:EditText=findViewById(R.id.nazivET)
                val name=n.text.toString()
                n=findViewById(R.id.porodicaET)
                val family=n.text.toString()
                n=findViewById(R.id.medicinskoUpozorenjeET)
                val warning=n.text.toString()
                val biljka=Biljka(
                    naziv =name,
                    porodica =family,
                    medicinskoUpozorenje =warning,
                    medicinskeKoristi =medKoristValues,
                    profilOkusa =okusValue,
                    jela =jela,
                    klimatskiTipovi =klimaValues,
                    zemljisniTipovi =zemljisteValues
                )
                val resultIntent = Intent()
                resultIntent.putExtra("novaBiljka", biljka)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }

        slika = findViewById(R.id.slikaIV)
        val uslikajBiljkuBtn = findViewById<Button>(R.id.uslikajBiljkuBtn)
        uslikajBiljkuBtn.setOnClickListener {
            uslikaj()
        }

    }

    val REQUEST_IMAGE_CAPTURE = 123

    private fun uslikaj() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            Log.d("Greska", "Nema kamere")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            slika?.setImageBitmap(imageBitmap)
        }
    }

    fun isRightFormat(ime: String): Boolean {
        val regex = Regex("^[a-zA-ZčćžšđČĆŽŠĐ\\s]+ \\([a-zA-Z\\s]+ [a-zA-Z\\s]+\\)$")
        return regex.matches(ime)
    }

    private fun handleEditText(): Boolean {
        var add = true
        val name:EditText=findViewById(R.id.nazivET)
        val family:EditText=findViewById(R.id.porodicaET)
        val warning:EditText=findViewById(R.id.medicinskoUpozorenjeET)

        name.setError(null)
        family.setError(null)
        warning.setError(null)

        val ime=name.text
        if (ime.length<2){
            name.setError("Prekratak naziv")
            add=false
        }
        else if (ime.length>40) {
            name.setError("Predug naziv")
            add=false
        }
        else if (!isRightFormat(ime.toString())){
            name.setError("Naziv mora biti formata \"Naziv (Latinski naziv)\"")
            add=false
        }

        val porodica=family.text
        if (porodica.length<2)
        {
            family.setError("Prekratak naziv porodice")
            add=false
        }
        else if (porodica.length>20) {
            family.setError("Predug naziv porodice")
            add=false
        }

        val upozorenje=warning.text
        if (upozorenje.length<2)
        {
            warning.setError("Prekratako upozorenje")
            add=false
        }
        else if (upozorenje.length>20) {
            warning.setError("Predugo upozorenje")
            add=false
        }
        return add
    }

    private fun handleJelaLV(jela: ArrayList<String>):Boolean{
        if (jela.size<1){
            val btn:Button=findViewById(R.id.dodajJeloBtn)
            btn.setError("Mora biti dodato jedno jelo")
            return false
        }
        return true
    }

}