package com.example.spirala1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity(){
    private lateinit var biljkeView: RecyclerView
    private lateinit var medAdapter: BiljkeMedAdapter
    private lateinit var kuhAdapter: BiljkeKuhAdapter
    private lateinit var botAdapter: BiljkeBotAdapter
    private var biljkeList:MutableList<Biljka?> =  getBiljke()
    private var orginalList:MutableList<Biljka?> =  getBiljke()
    private var currentMode: String ="Medicinski mod"
    private val REQUEST_NEW_PLANT_CODE = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        biljkeView = findViewById(R.id.biljkeRV)

        biljkeView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        medAdapter = BiljkeMedAdapter(listOf())
        kuhAdapter = BiljkeKuhAdapter(listOf())
        botAdapter = BiljkeBotAdapter(listOf())

        biljkeView.adapter = medAdapter
        medAdapter.updateBiljke(biljkeList)


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
                        medAdapter= BiljkeMedAdapter(biljkeList)
                        biljkeView.adapter = medAdapter
                        medAdapter.updateBiljke(biljkeList)

                    }
                    "Kuharski mod" -> {
                        kuhAdapter= BiljkeKuhAdapter(biljkeList)
                        biljkeView.adapter = kuhAdapter
                        kuhAdapter.updateBiljke(biljkeList)
                    }
                    else -> {
                        botAdapter=BiljkeBotAdapter(biljkeList)
                        biljkeView.adapter = botAdapter
                        botAdapter.updateBiljke(biljkeList)
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

        val newButton: Button=findViewById(R.id.novaBiljkaBtn)
        newButton.setOnClickListener{
            val intent = Intent(this, NovaBiljkaActivity::class.java)
            startActivityForResult(intent,1)
        }
    }

    companion object {
        private const val NEW_PLANT_REQUEST_CODE = 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == NEW_PLANT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.getParcelableExtra<Biljka>("novaBiljka")?.let { novaBiljka ->
                biljkeList+= novaBiljka
                orginalList+= novaBiljka
                currentMode="Medicinski mod"
                val modSpinner:Spinner=findViewById(R.id.modSpinner)
                val modArray = resources.getStringArray(R.array.modovi)
                var position = modArray.indexOf(currentMode)
                modSpinner.setSelection(position)
                biljkeView.adapter=medAdapter
                medAdapter.updateBiljke(biljkeList)
            }
        }
    }

    private fun resetList(){
        when (currentMode) {
            "Medicinski mod" -> {
                medAdapter = BiljkeMedAdapter(orginalList)
                biljkeView.adapter = medAdapter
                medAdapter.updateBiljke(biljkeList)
            }
            "Kuharski mod" -> {
                kuhAdapter = BiljkeKuhAdapter(orginalList)
                biljkeView.adapter = kuhAdapter
                kuhAdapter.updateBiljke(biljkeList)
            }
            else -> {
                botAdapter = BiljkeBotAdapter(orginalList)
                biljkeView.adapter = botAdapter
                botAdapter.updateBiljke(biljkeList)
            }
        }
    }
}
