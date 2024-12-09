package com.example.shopstume.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shopstume.Costume
import com.example.shopstume.R

class CostumeAdapter(private var costumes: MutableList<Costume>,
                     private val onAction: (String, Costume) -> Unit
) : RecyclerView.Adapter<CostumeAdapter.CostumeViewHolder>() {

    inner class CostumeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.imageCostume)
        var nameText: TextView = itemView.findViewById(R.id.textCostumeName)
        var stateText: TextView = itemView.findViewById(R.id.textCostumeState)
        var priceText: TextView = itemView.findViewById(R.id.textCostumePrice)
        var sizeText: TextView = itemView.findViewById(R.id.textCostumeSize)
        var stockText: TextView = itemView.findViewById(R.id.textCostumeStock)
        var editButton: ImageButton = itemView.findViewById(R.id.btnEditCostume)
        var deleteButton: ImageButton = itemView.findViewById(R.id.btnDeleteCostume)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CostumeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_costume, parent, false)
        return CostumeViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CostumeViewHolder, position: Int) {
        val costume: Costume = costumes[position]

        val imageRes = holder.itemView.context.resources.getIdentifier(
            costume.image,"drawable", holder.itemView.context.packageName
        )

        if(imageRes != 0){
            holder.imageView.setImageResource(imageRes)
        }else{
            holder.imageView.setImageResource(R.drawable.costume1)
        }

        holder.nameText.text = costume.name
        holder.stateText.text = costume.state
        holder.priceText.text = "$${costume.price}"
        holder.sizeText.text = costume.size
        holder.stockText.text = "Stock: ${costume.stock}"
        holder.editButton.setOnClickListener { onAction("edit", costume) }
        holder.deleteButton.setOnClickListener { onAction("delete", costume) }
    }

    override fun getItemCount(): Int = costumes.size

    fun updateCostumes(newCostumes: MutableList<Costume>) {
        costumes.clear()
        costumes.addAll(newCostumes)
        notifyDataSetChanged()
    }

    fun addCostume(costume: Costume) {
        costumes.add(costume)
        notifyItemInserted(costumes.size - 1)
    }

    fun deleteCostume(costume: Costume) {
        val position = costumes.indexOf(costume)
        if (position != -1) {
            costumes.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}