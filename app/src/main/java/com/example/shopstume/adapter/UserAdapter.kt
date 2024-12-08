package com.example.shopstume.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shopstume.R
import com.example.shopstume.User

class UserAdapter(private var users:MutableList<User>,
    private val onAction: (String, User) -> Unit)
    :RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var nameText: TextView = itemView.findViewById(R.id.textUserName)
        var roleText: TextView = itemView.findViewById(R.id.textUserRole)
        var editButton: ImageButton = itemView.findViewById(R.id.btnEditUser)
        var deleteButton: ImageButton = itemView.findViewById(R.id.btnDeleteUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_user, parent, false)
        return UserViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user: User = users[position]
        holder.nameText.text = "${user.name} ${user.lastName}"
        holder.roleText.text = users[position].role
        holder.editButton.setOnClickListener { onAction("edit", user) }
        holder.deleteButton.setOnClickListener { onAction("delete", user) }
    }

    override fun getItemCount(): Int = users.size

    fun updateUsers(newUsers: MutableList<User>){
        users = newUsers
        notifyDataSetChanged()
    }

    fun addUser(user: User){
        users.add(user)
        notifyItemInserted(users.size - 1)
    }

    fun deleteUser(user: User){
        val position = users.indexOf(user)
        if(position!=-1){
            users.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}