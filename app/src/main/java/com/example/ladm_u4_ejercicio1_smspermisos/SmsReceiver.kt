package com.example.ladm_u4_ejercicio1_smspermisos

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsMessage
import android.widget.Toast

/*
    RECEIVER = EVENTO U OYENTE DE ANDROID QUE PERMITE LA LECTURA DE EVENTOS DEL SISTEMA OPERATIVO
 */

class SmsReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent) {
        val extras = intent.extras

        if(extras != null){
            var sms = extras.get("pdus") as Array<Any>

            for(indice in sms.indices){
                var formato = extras.getString("format")

                var smsMensaje = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    SmsMessage.createFromPdu(sms[indice] as ByteArray,formato)
                }else{
                    SmsMessage.createFromPdu(sms[indice] as ByteArray)
                }

                var celularOrigen = smsMensaje.originatingAddress
                var contenidoSMS = smsMensaje.messageBody.toString()

                Toast.makeText(context,"ENTRÓ CONTENIDO",Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
}