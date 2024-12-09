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

        costumeAdapter = CostumeAdapter(costumesList) { costume ->
            showCostumeDetailsDialog(costume) // Muestra el diálogo con los detalles del disfraz
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

    private fun showCostumeDetailsDialog(costume: Costume) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_show_costume_details, null)

        val costumeNameTextView = dialogView.findViewById<TextView>(R.id.tvCostumeName)
        val costumeStateTextView = dialogView.findViewById<TextView>(R.id.tvCostumeState)
        val costumePriceTextView = dialogView.findViewById<TextView>(R.id.tvCostumePrice)
        val costumeSizeTextView = dialogView.findViewById<TextView>(R.id.tvCostumeSize)
        val costumeStockTextView = dialogView.findViewById<TextView>(R.id.tvCostumeStock)
        val costumeImageView = dialogView.findViewById<ImageView>(R.id.ivCostumeImage)

        costumeNameTextView.text = costume.name
        costumeStateTextView.text = costume.state
        costumePriceTextView.text = "Precio: ${costume.price}"
        costumeSizeTextView.text = "Tamaño: ${costume.size}"
        costumeStockTextView.text = "Stock: ${costume.stock}"
        costumeImageView.setImageResource(costume.image)

        val builder = AlertDialog.Builder(requireContext())
            .setTitle("Detalles del Disfraz")
            .setView(dialogView)
            .setPositiveButton("Cerrar") { dialog, _ -> dialog.dismiss() }

        builder.create().show()
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
}
