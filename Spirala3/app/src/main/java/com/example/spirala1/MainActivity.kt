package com.example.spirala1

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(){
    private lateinit var biljkeView: RecyclerView
    private lateinit var medAdapter: BiljkeMedAdapter
    private lateinit var kuhAdapter: BiljkeKuhAdapter
    private lateinit var botAdapter: BiljkeBotAdapter
    private var trenutnaLista =  getBiljke()
    private var originalnaLista = getBiljke()
    private var currentMode: String = "Medicinski mod"
    private var previousMode: String = ""
    private val context:Context = this
    private val trefle = TrefleDAO()
    private lateinit var newBiljkaButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        biljkeView = findViewById(R.id.biljkeRV)
        newBiljkaButton=findViewById(R.id.novaBiljkaBtn)

        biljkeView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        medAdapter = BiljkeMedAdapter(originalnaLista, context)
        kuhAdapter = BiljkeKuhAdapter(originalnaLista, context)
        botAdapter = BiljkeBotAdapter(originalnaLista, context)

        biljkeView.adapter = medAdapter

        val modSpinner: Spinner = findViewById(R.id.modSpinner)
        val modArray = resources.getStringArray(R.array.modovi)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, modArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        modSpinner.adapter = adapter
        val defaultMode = "Medicinski mod"
        val defaultPosition = modArray.indexOf(defaultMode)
        modSpinner.setSelection(defaultPosition)

        modSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                currentMode= parent?.getItemAtPosition(position).toString()
                Toast.makeText(this@MainActivity, "Odabrani mod: $currentMode", Toast.LENGTH_SHORT).show()
                when (currentMode) {
                    "Medicinski mod" -> {
                        beforeList()
                        medAdapter.updateBiljke(trenutnaLista)
                        biljkeView.adapter = medAdapter
                        previousMode = currentMode
                    }
                    "Kuharski mod" -> {
                        beforeList()
                        kuhAdapter.updateBiljke(trenutnaLista)
                        biljkeView.adapter = kuhAdapter
                        previousMode = currentMode
                    }
                    else -> {
                        beforeList()
                        val pretraga = findViewById<ConstraintLayout>(R.id.fastSearch)
                        pretraga.visibility = View.VISIBLE
                        val pretragaTekst: EditText = findViewById(R.id.pretragaET)
                        pretragaTekst.setText("")

                        botAdapter.updateBiljke(trenutnaLista)
                        biljkeView.adapter = botAdapter
                        previousMode = currentMode
                        val bojeSpinner: Spinner = findViewById(R.id.bojaSPIN)
                        val bojeArray = resources.getStringArray(R.array.boje)
                        val bojeAdapter =
                            ArrayAdapter(context, android.R.layout.simple_spinner_item, bojeArray)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        bojeSpinner.adapter = bojeAdapter
                        val pretragaBtn: Button = findViewById(R.id.brzaPretraga)
                        var flowerColor = ""
                        bojeSpinner.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                                    flowerColor = parent?.getItemAtPosition(position).toString()
                                }
                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                    TODO("Not yet implemented")
                                }
                            }
                        pretragaBtn.setOnClickListener {
                            newBiljkaButton.visibility=View.GONE
                            val substring = pretragaTekst.text.toString()
                            if (flowerColor.length>1 && substring.length > 0) {
                                pretrazi(flowerColor, substring)
                            }else{
                                Toast.makeText(this@MainActivity, "Unesite substring i odaberite boju", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        val resetButton: Button=findViewById(R.id.resetBtn)
        resetButton.setOnClickListener{
            resetList()
        }

        newBiljkaButton.setOnClickListener{
            val intent = Intent(this, NovaBiljkaActivity::class.java)
            startActivityForResult(intent,1)
        }
    }


    private fun pretrazi(flowerColor: String, substring: String){
        CoroutineScope(Job() + Dispatchers.Main).launch {
            val lista = trefle.getPlantsWithFlowerColor(flowerColor, substring)
            Toast.makeText(this@MainActivity, "Pronađeno " + lista.size.toString() + " biljaka", Toast.LENGTH_SHORT).show()
            val pretragaAdapter = BiljkePretragaAdapter(lista, context)
            biljkeView.adapter = pretragaAdapter
        }
    }

    companion object {
        private const val NEW_PLANT_REQUEST_CODE = 1
    }

    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == NEW_PLANT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.getParcelableExtra<Biljka>("novaBiljka")?.let { novaBiljka1 ->
                var novaBiljka = novaBiljka1
                scope.launch {
                    novaBiljka = trefle.fixData(novaBiljka1)
                    novaBiljka.porodica?.let { Log.d("family", it) }
                    trenutnaLista+= novaBiljka
                    originalnaLista+= novaBiljka
                    currentMode="Medicinski mod"
                    val modSpinner:Spinner=findViewById(R.id.modSpinner)
                    val modArray = resources.getStringArray(R.array.modovi)
                    var position = modArray.indexOf(currentMode)
                    modSpinner.setSelection(position)
                    biljkeView.adapter=medAdapter
                    medAdapter.updateBiljke(trenutnaLista)
                }

            }
        }
    }

    private fun beforeList(){
        newBiljkaButton.visibility=View.VISIBLE
        when(previousMode){
            "" -> {
                trenutnaLista=medAdapter.returnBiljke()
            }
            "Medicinski mod" -> {
                trenutnaLista=medAdapter.returnBiljke()
            }
            "Kuharski mod"-> {
                trenutnaLista=kuhAdapter.returnBiljke()
            }
            else -> {
                trenutnaLista=botAdapter.returnBiljke()
                val pretraga = findViewById<ConstraintLayout>(R.id.fastSearch)
                pretraga.visibility = View.GONE
            }
        }
    }

    private fun resetList(){
        when (currentMode) {
            "Medicinski mod" -> {
                biljkeView.adapter = medAdapter
                medAdapter.updateBiljke(originalnaLista)
            }
            "Kuharski mod" -> {
                biljkeView.adapter = kuhAdapter
                kuhAdapter.updateBiljke(originalnaLista)
            }
            else -> {
                newBiljkaButton.visibility=View.VISIBLE
                biljkeView.adapter = botAdapter
                botAdapter.updateBiljke(originalnaLista)
            }
        }
    }
}
