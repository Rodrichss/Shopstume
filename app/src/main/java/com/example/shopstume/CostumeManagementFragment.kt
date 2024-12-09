package com.example.shopstume

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopstume.adapter.CostumeAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import org.json.JSONObject

class CostumeManagementFragment : Fragment() {

    private lateinit var rCostumes: RecyclerView
    private lateinit var costumeAdapter: CostumeAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private val costumesList = mutableListOf<Costume>()
    private var filteredCostumesList = mutableListOf<Costume>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_costume_management, container, false)
        rCostumes = view.findViewById(R.id.rvCostumes)
        val fabAddCostume: FloatingActionButton = view.findViewById(R.id.fabAddCostume)
        val editSearchCostume: EditText = view.findViewById(R.id.editSearchCostume)

        sharedPreferences = requireContext().getSharedPreferences("CostumePrefs", Context.MODE_PRIVATE)

        costumeAdapter = CostumeAdapter(filteredCostumesList) { action, costume ->
            when (action) {
                //"edit" -> editCostume(costume)
                "delete" -> deleteCostume(costume)
            }
        }

        rCostumes.layoutManager = LinearLayoutManager(requireContext())
        rCostumes.adapter = costumeAdapter

        loadCostumes()

        fabAddCostume.setOnClickListener {
            showAddCostumeDialog()
        }

        editSearchCostume.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterCostumes(s.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })

        return view
    }

    private fun loadCostumes() {
        val costumesJSON = sharedPreferences.getString("costumes", "[]")
        val jsonArray = JSONArray(costumesJSON)

        costumesList.clear()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val costume = Costume(
                jsonObject.getInt("idCostume"),
                jsonObject.getString("name"),
                jsonObject.getString("state"),
                jsonObject.getDouble("price"),
                jsonObject.getString("size"),
                jsonObject.getInt("stock"),
                jsonObject.getInt("image")
            )
            costumesList.add(costume)
            Log.d("CostumeManagement", "Nuevo disfraz agregado: ${costume.name}")
        }

        filteredCostumesList = costumesList.toMutableList()
        costumeAdapter.updateCostumes(costumesList)
    }

    private fun filterCostumes(query:String){
        filteredCostumesList = if(query.isEmpty()) {
            costumesList.toMutableList()
        }else{
            costumesList.filter {
                it.name.contains(query, ignoreCase = true)
            }.toMutableList()
        }
        Log.d("FilterCostumes", "Número de disfraces filtrados: ${filteredCostumesList.size}")

        costumeAdapter.updateCostumes(filteredCostumesList)
    }

    private fun saveCostumes() {
        val jsonArray = JSONArray()
        for (costume in costumesList) {
            val jsonObject = JSONObject()
            jsonObject.put("idCostume", costume.idCostume)
            jsonObject.put("name", costume.name)
            jsonObject.put("state", costume.state)
            jsonObject.put("price", costume.price)
            jsonObject.put("size", costume.size)
            jsonObject.put("stock", costume.stock)
            jsonObject.put("image", costume.image)
            jsonArray.put(jsonObject)
        }
        sharedPreferences.edit().putString("costumes", jsonArray.toString()).apply()
    }


    private fun deleteCostume(costume: Costume) {
        val builder = AlertDialog.Builder(requireContext())
            .setTitle("Confirmar eliminación")
            .setMessage("¿Estás seguro de que deseas eliminar ${costume.name}?")
            .setPositiveButton("Sí") { dialog, _ ->
                costumesList.remove(costume)
                saveCostumes()
                costumeAdapter.deleteCostume(costume)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }

        builder.create().show()
    }

    private fun showAddCostumeDialog(){
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_costume, null)
        val builder = AlertDialog.Builder(requireContext())
        val selectImageButton = dialogView.findViewById<ImageButton>(R.id.btnSelectImage)
        val sizesSpinner = dialogView.findViewById<Spinner>(R.id.spinnerSizes)

        val lstSizes = resources.getStringArray(R.array.sizes)
        val sizesAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, lstSizes)
        sizesSpinner.adapter = sizesAdapter
        var sizesSel = lstSizes[0]

        sizesSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                sizesSel = lstSizes[position]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
            }

        var selectedImageName = ""

        selectImageButton.setOnClickListener {
            showCostumeImagePicker { imageName ->
                selectedImageName = imageName

            }
        }

        builder.setTitle("Añadir disfraz")
            .setView(dialogView)
            .setPositiveButton("Guardar") { dialog, _ ->
                val name = dialogView.findViewById<TextView>(R.id.etCostumeName).text.toString()
                val price = dialogView.findViewById<TextView>(R.id.etCostumePrice).text.toString().toDoubleOrNull() ?: 0.0
                val stock = dialogView.findViewById<TextView>(R.id.etCostumeStock).text.toString().toIntOrNull() ?: 0
                val state = if (stock > 0) "Disponible" else "Agotado"

                val imageRes = resources.getIdentifier(selectedImageName, "drawable", requireContext().packageName)
                if (imageRes == 0) {
                    Log.e("CostumeManagement", "Imagen no seleccionada o no encontrada")
                    return@setPositiveButton
                }

                val newCostume = Costume(
                    idCostume = costumesList.size + 1,
                    name = name,
                    state = state,
                    price = price,
                    size = sizesSel,
                    stock = stock,
                    image = imageRes
                )
                costumesList.add(newCostume)
                costumeAdapter.addCostume(newCostume)
                saveCostumes()
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }

        builder.create().show()
    }

    private fun showCostumeImagePicker(onImageSelected: (String) -> Unit) {
        val drawableNames = resources.getStringArray(R.array.costume_images) // Puedes definir este array en strings.xml

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Selecciona una imagen")
            .setItems(drawableNames) { _, which ->
                // Cuando el usuario selecciona una imagen, se pasa el nombre de la imagen al callback
                val selectedImageName = drawableNames[which]
                onImageSelected(selectedImageName)
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }

        builder.create().show()
    }
}