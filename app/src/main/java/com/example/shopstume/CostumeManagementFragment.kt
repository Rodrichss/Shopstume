package com.example.shopstume

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_costume_management, container, false)
        rCostumes = view.findViewById(R.id.rvCostumes)
        val fabAddCostume: FloatingActionButton = view.findViewById(R.id.fabAddCostume)

        sharedPreferences = requireContext().getSharedPreferences("CostumePrefs", Context.MODE_PRIVATE)

        costumeAdapter = CostumeAdapter(costumesList) { action, costume ->
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
        costumeAdapter.updateCostumes(costumesList)
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
//        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_costume, null)
//        val builder = AlertDialog.Builder(requireContext())
//        val imageSelectedTextView = dialogView.findViewById<TextView>(R.id.tvSelectedImageName)
//        val selectImageButton = dialogView.findViewById<FloatingActionButton>(R.id.btnSelectImage)
//
//        var selectedImageName = ""
//
//        selectImageButton.setOnClickListener {
//            showCostumeImagePicker { imageName ->
//                selectedImageName = imageName
//                imageSelectedTextView.text = "Imagen seleccionada: $imageName"
//            }
//        }
//
//        builder.setTitle("Añadir disfraz")
//            .setView(dialogView)
//            .setPositiveButton("Guardar") { dialog, _ ->
//                val name = dialogView.findViewById<TextView>(R.id.etCostumeName).text.toString()
//                val state = dialogView.findViewById<TextView>(R.id.etCostumeState).text.toString()
//                val price = dialogView.findViewById<TextView>(R.id.etCostumePrice).text.toString().toDoubleOrNull() ?: 0.0
//                val size = dialogView.findViewById<TextView>(R.id.etCostumeSize).text.toString()
//                val stock = dialogView.findViewById<TextView>(R.id.etCostumeStock).text.toString().toIntOrNull() ?: 0
//
//                val imageRes = resources.getIdentifier(selectedImageName, "drawable", requireContext().packageName)
//                if (imageRes == 0) {
//                    Log.e("CostumeManagement", "Imagen no seleccionada o no encontrada")
//                    return@setPositiveButton
//                }
//
//                val newCostume = Costume(
//                    idCostume = costumesList.size + 1,
//                    name = name,
//                    state = state,
//                    price = price,
//                    size = size,
//                    stock = stock,
//                    image = imageRes
//                )
//                costumesList.add(newCostume)
//                costumeAdapter.addCostume(newCostume)
//                saveCostumes()
//                dialog.dismiss()
//            }
//            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
//
//        builder.create().show()
    }
}