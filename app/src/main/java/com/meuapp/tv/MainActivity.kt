package com.meuapp.tv

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.graphics.Color
import android.view.Gravity
import androidx.fragment.app.FragmentActivity
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.http.*

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Cria a interface visual direto por código (evita erro de arquivo faltando)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.BLACK)
            gravity = Gravity.CENTER
        }
        
        val text = TextView(this).apply {
            text = "FlashDrive Hub TV Ativo\n\nEscaneie o IP da TV na porta 8080"
            setTextColor(Color.WHITE)
            textSize = 24f
            gravity = Gravity.CENTER
        }
        
        layout.addView(text)
        setContentView(layout)

        // Inicia o servidor
        embeddedServer(Netty, port = 8080) {
            routing {
                get("/") {
                    call.respondText("<h1>Hub Conectado!</h1>", ContentType.Text.Html)
                }
            }
        }.start(wait = false)
    }
}