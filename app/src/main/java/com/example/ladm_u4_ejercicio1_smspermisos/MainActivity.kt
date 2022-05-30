package com.example.ladm_u4_ejercicio1_smspermisos

import android.Manifest
import android.R
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.ladm_u4_ejercicio1_smspermisos.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.time.Instant
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    val siPermiso = 1
    val siPermisoReceiver = 2
    lateinit var binding: ActivityMainBinding
    var listaSMS = ArrayList<String>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val consulta = FirebaseDatabase.getInstance().getReference().child("mensajes")

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var datos = ArrayList<String>()
                listaSMS.clear()

                for(data in snapshot.children!!){
                    val id = data.key
                    listaSMS.add(id!!)
                    val telefono = data.getValue<SMS>()!!.telefono
                    val mensaje = data.getValue<SMS>()!!.mensaje
                    datos.add("telefono: ${telefono}\nmensaje: ${mensaje}")
                }
                mostrarLista(datos)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }

        consulta.addValueEventListener(postListener)


        if(ActivityCompat.checkSelfPermission(this,
            Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.RECEIVE_SMS),siPermisoReceiver)
        }

        button.setOnClickListener {
            var basedatos = Firebase.database.reference

            val usuario = SMS(binding.telefono.text.toString(),
                binding.mensaje.text.toString())




            if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.SEND_SMS), siPermiso
                )
            }else{
                envioSMS()
                }

            basedatos.child("mensajes").push().setValue(usuario)
                .addOnSuccessListener { setTitle("SE INSERTÓ")
                    binding.telefono.text.clear()
                    binding.mensaje.text.clear()}
                .addOnFailureListener {
                    AlertDialog.Builder(this)
                        .setMessage(it.message)
                        .setPositiveButton("OK"){d,i->}
                        .show()
                }

            }
        }

    private fun mostrarLista(datos: ArrayList<String>) {
        binding.lista.adapter = ArrayAdapter<String>(this, R.layout.simple_list_item_1,datos)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == siPermiso) {
            envioSMS()
        }
        if (requestCode == siPermisoReceiver){
            mensajeRecibir()
        }
    }

    private fun mensajeRecibir() {
        AlertDialog.Builder(this)
            .setMessage("SE OTORGO RECIBIR")
            .show()
    }

    private fun envioSMS() {
        SmsManager.getDefault().sendTextMessage(telefono.text.toString(),null,
        mensaje.text.toString(), null,null)
        Toast.makeText(this,"se envio el mensaje",Toast.LENGTH_LONG)
            .show()
    }

}