package com.example.shopstume

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopstume.adapter.UserAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import org.json.JSONObject

class UserManagementFragment : Fragment() {

    private lateinit var rUsuarios: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private val usersList = mutableListOf<User>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_management, container, false)
        rUsuarios = view.findViewById(R.id.rvUsuarios)
        val fabAddUser: FloatingActionButton = view.findViewById(R.id.fabAddUser)

        sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        userAdapter = UserAdapter(usersList) { action, user ->
            when(action){
                "edit" -> editUser(user)
                "delete" -> deleteUser(user)
            }
        }

        rUsuarios.layoutManager = LinearLayoutManager(requireContext())
        rUsuarios.adapter = userAdapter

        loadUsers()

        fabAddUser.setOnClickListener{
            showAddUserDialog()
        }

        //userAdapter.updateUsers(usersList)
        // Inflate the layout for this fragment
        return view
    }

    private fun loadUsers(){
        val usersJSON = sharedPreferences.getString("users", "[]")
        val jsonArray = JSONArray(usersJSON)

        usersList.clear()
        for(i in 0 until jsonArray.length()){
            val jsonObject = jsonArray.getJSONObject(i)
            val user = when (jsonObject.getString("role")) {
                "Empleado" -> Employee(
                    jsonObject.getInt("idUser"),
                    jsonObject.getString("name"),
                    jsonObject.getString("lastName"),
                    jsonObject.getString("email"),
                    jsonObject.getString("password")
                )
                else -> Administrator(
                    jsonObject.getInt("idUser"),
                    jsonObject.getString("name"),
                    jsonObject.getString("lastName"),
                    jsonObject.getString("email"),
                    jsonObject.getString("password")
                )
            }
            usersList.add(user)
            Log.d("UserManagement", "Nuevo usuario agregado: ${user.name} ${user.lastName}")
            Log.d("UserManagement", "Total de usuarios ahora: ${usersList.size}")
        }
        userAdapter.updateUsers(usersList)
        Log.d("UserManagement", "Total de usuarios ahora: ${usersList.size}")
    }

    private fun saveUsers(){
        val jsonArray = JSONArray()
        for (user in usersList) {
            val jsonObject = JSONObject()
            jsonObject.put("idUser", user.idUser)
            jsonObject.put("name", user.name)
            jsonObject.put("lastName", user.lastName)
            jsonObject.put("email", user.email)
            jsonObject.put("password", user.password)
            jsonObject.put("role", user.role)

            //jsonObject.put("userType", if (user is Administrator) "Administrator" else "Employee")

            jsonArray.put(jsonObject)
        }
        sharedPreferences.edit().putString("users", jsonArray.toString()).apply()
    }

    private fun showAddUserDialog(){
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_user, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.editTextName)
        val lastNameEditText = dialogView.findViewById<EditText>(R.id.editTextLastName)
        val emailEditText = dialogView.findViewById<EditText>(R.id.editTextEmail)
        val passwordEditText = dialogView.findViewById<EditText>(R.id.editTextPassword)
        val roleSpinner = dialogView.findViewById<Spinner>(R.id.roleSpinner)

        val lstRoles = getResources().getStringArray(R.array.roles)
        val rolesAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, lstRoles)
        roleSpinner.adapter = rolesAdapter
        var rolesSel = lstRoles[0]
        roleSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                rolesSel = lstRoles[position]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        val builder = AlertDialog.Builder(requireContext())
            .setTitle("Agregar Usuario")
            .setView(dialogView)
            .setPositiveButton("Agregar") { dialog, _ ->
                val name = nameEditText.text.toString()
                val lastName = lastNameEditText.text.toString()
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()

                if(nameEditText.text.isNotBlank() && lastNameEditText.text.isNotBlank() && emailEditText.text.isNotBlank()&&
                    passwordEditText.text.isNotBlank()){
                    val newUser = if (rolesSel == "Administrador") {
                        Administrator(1, name, lastName, email, password)
                    } else {
                        Employee(1, name, lastName, email, password)
                    }

                    usersList.add(newUser)
                    saveUsers()
                    userAdapter.addUser(newUser)
                    loadUsers()
                    dialog.dismiss()
                    Log.d("UserManagement", "Usuarios cargados: ${usersList.size}")
                }else{
                    Toast.makeText(requireContext(),"Rellene todos los campos.",Toast.LENGTH_SHORT).show()
                }

            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }

        builder.create().show()
    }

    private fun editUser(user: User){
        // Crear el cuadro de diálogo para editar el usuario
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_user, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.editTextName)
        val lastNameEditText = dialogView.findViewById<EditText>(R.id.editTextLastName)
        val emailEditText = dialogView.findViewById<EditText>(R.id.editTextEmail)
        val passwordEditText = dialogView.findViewById<EditText>(R.id.editTextPassword)
        val roleSpinner = dialogView.findViewById<Spinner>(R.id.roleSpinner)

        // Prellenar los campos con la información actual del usuario
        nameEditText.setText(user.name)
        lastNameEditText.setText(user.lastName)
        emailEditText.setText(user.email)
        passwordEditText.setText(user.password)

        // Configurar el Spinner con los roles disponibles
        val rolesList = resources.getStringArray(R.array.roles)
        val rolesAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, rolesList)
        roleSpinner.adapter = rolesAdapter
        // Establecer el rol actual como el valor seleccionado en el Spinner
        val selectedRolePosition = rolesList.indexOf(user.role)
        roleSpinner.setSelection(selectedRolePosition)

        val builder = AlertDialog.Builder(requireContext())
            .setTitle("Editar Usuario")
            .setView(dialogView)
            .setPositiveButton("Guardar") { dialog, _ ->
                val name = nameEditText.text.toString()
                val lastName = lastNameEditText.text.toString()
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()
                val selectedRole = rolesList[roleSpinner.selectedItemPosition]

                if (name.isNotBlank() && lastName.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                    // Actualizar los datos del usuario

                    user.name = name
                    user.lastName = lastName
                    user.email = email
                    user.password = password
                    user.role = selectedRole

                    // Actualizar la lista de usuarios en SharedPreferences
                    saveUsers()

                    // Actualizar el RecyclerView
                    userAdapter.updateUsers(usersList)
                    //userAdapter.notifyDataSetChanged()

                    // Mostrar un mensaje de éxito
                    Toast.makeText(requireContext(), "Usuario actualizado", Toast.LENGTH_SHORT).show()

                    Log.d("UserManagement", "Usuarios cargados: ${usersList.size}")

                    dialog.dismiss()
                } else {
                    Toast.makeText(requireContext(), "Rellene todos los campos", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }

        builder.create().show()
    }

    private fun deleteUser(user: User){
        val builder = AlertDialog.Builder(requireContext())
            .setTitle("Confirmar eliminación")
            .setMessage("¿Estás seguro de que deseas eliminar a ${user.name} ${user.lastName}?")
            .setPositiveButton("Sí") { dialog, _ ->

                usersList.remove(user)
                saveUsers()
                userAdapter.deleteUser(user)
                userAdapter.notifyDataSetChanged()
                Toast.makeText(requireContext(), "Usuario eliminado", Toast.LENGTH_SHORT).show()

                Log.d("UserManagement", "Usuarios cargados: ${usersList.size}")

                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }

}