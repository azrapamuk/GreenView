package com.example.spirala1

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
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

class MainActivity : AppCompatActivity() {
    private lateinit var biljkeView: RecyclerView
    private lateinit var medAdapter: BiljkeMedAdapter
    private lateinit var kuhAdapter: BiljkeKuhAdapter
    private lateinit var botAdapter: BiljkeBotAdapter
    private lateinit var biljkaDAO: BiljkaDAO
    private var trenutnaLista: List<Biljka?> = listOf()
    private var originalnaLista: List<Biljka?> = listOf()
    private var currentMode: String = "Medicinski mod"
    private var previousMode: String = ""
    private val context: Context = this
    private val trefle = TrefleDAO()
    private lateinit var newBiljkaButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        biljkeView = findViewById(R.id.biljkeRV)
        newBiljkaButton = findViewById(R.id.novaBiljkaBtn)

        biljkeView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )

        biljkaDAO = BiljkaDatabase.getDatabase(this).biljkaDao()

        CoroutineScope(Dispatchers.Main).launch {
            originalnaLista = biljkaDAO.getAllBiljkas()
            trenutnaLista = originalnaLista

            medAdapter = BiljkeMedAdapter(trenutnaLista, context)
            kuhAdapter = BiljkeKuhAdapter(trenutnaLista, context)
            botAdapter = BiljkeBotAdapter(trenutnaLista, context)

            biljkeView.adapter = medAdapter

            val modSpinner: Spinner = findViewById(R.id.modSpinner)
            val modArray = resources.getStringArray(R.array.modovi)
            val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, modArray)
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
                    currentMode = parent?.getItemAtPosition(position).toString()
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
                                        // Do nothing
                                    }
                                }
                            pretragaBtn.setOnClickListener {
                                newBiljkaButton.visibility = View.GONE
                                val substring = pretragaTekst.text.toString()
                                if (flowerColor.length > 1 && substring.length > 0) {
                                    pretrazi(flowerColor, substring)
                                } else {
                                    Toast.makeText(this@MainActivity, "Unesite substring i odaberite boju", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Do nothing
                }
            }

            val resetButton: Button = findViewById(R.id.resetBtn)
            resetButton.setOnClickListener {
                resetList()
            }

            newBiljkaButton.setOnClickListener {
                val intent = Intent(this@MainActivity, NovaBiljkaActivity::class.java)
                startActivityForResult(intent, NEW_PLANT_REQUEST_CODE)
                resetList()
            }
        }
    }

    private fun pretrazi(flowerColor: String, substring: String) {
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
                scope.launch {
                    var novaBiljka = trefle.fixData(novaBiljka1)
                    val savedSuccessfully = biljkaDAO.saveBiljka(novaBiljka)
                    if (savedSuccessfully) {
                        Toast.makeText(this@MainActivity, "Biljka uspješno spašena u bazu podataka", Toast.LENGTH_SHORT).show()
                        trenutnaLista = biljkaDAO.getAllBiljkas()
                         originalnaLista = trenutnaLista
                        currentMode = "Medicinski mod"
                        val modSpinner: Spinner = findViewById(R.id.modSpinner)
                        val modArray = resources.getStringArray(R.array.modovi)
                        val position = modArray.indexOf(currentMode)
                        modSpinner.setSelection(position)
                        biljkeView.adapter = medAdapter
                        medAdapter.updateBiljke(trenutnaLista)
                    } else {
                        Toast.makeText(this@MainActivity, "Greška pri spremanju biljke", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun beforeList() {
        newBiljkaButton.visibility = View.VISIBLE
        when (previousMode) {
            "" -> {
                trenutnaLista = medAdapter.returnBiljke()
            }
            "Medicinski mod" -> {
                trenutnaLista = medAdapter.returnBiljke()
            }
            "Kuharski mod" -> {
                trenutnaLista = kuhAdapter.returnBiljke()
            }
            else -> {
                trenutnaLista = botAdapter.returnBiljke()
                val pretraga = findViewById<ConstraintLayout>(R.id.fastSearch)
                pretraga.visibility = View.GONE
            }
        }
    }

    private fun resetList() {
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
                newBiljkaButton.visibility = View.VISIBLE
                biljkeView.adapter = botAdapter
                botAdapter.updateBiljke(originalnaLista)
            }
        }
    }
}
