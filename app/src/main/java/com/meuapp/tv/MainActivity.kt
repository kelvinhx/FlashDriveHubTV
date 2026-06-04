package com.meuapp.tv

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.graphics.Color
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.http.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Interface visual simples criada por código
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#121212"))
            gravity = Gravity.CENTER
        }
        
        val text = TextView(this).apply {
            text = "FlashDrive Hub TV Ativo\n\nConecte-se via IP na porta 8080"
            setTextColor(Color.WHITE)
            textSize = 20f
            gravity = Gravity.CENTER
        }
        
        layout.addView(text)
        setContentView(layout)

        // Inicia o servidor em segundo plano (evita travamento)
        thread {
            try {
                embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
                    routing {
                        get("/") {
                            call.respondText(
                                "<html><body style='text-align:center;font-family:sans-serif;'><h1>Hub Ativo!</h1><p>Pronto para receber arquivos.</p></body></html>",
                                ContentType.Text.Html
                            )
                        }
                    }
                }.start(wait = true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}