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
import java.io.File
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#121212"))
            gravity = Gravity.CENTER
            setPadding(50, 50, 50, 50)
        }
        
        val text = TextView(this).apply {
            text = "FLASH DRIVE HUB TV\n\nStatus: ONLINE\nCache: USB Pendrive\n\nAcesse no iPhone: http://seu-ip:8080"
            setTextColor(Color.CYAN)
            textSize = 22f
            gravity = Gravity.CENTER
        }
        
        layout.addView(text)
        setContentView(layout)

        thread {
            embeddedServer(Netty, port = 8080) {
                routing {
                    get("/") {
                        call.respondText(generatePWA(), ContentType.Text.Html)
                    }
                    // Adicione aqui as rotas de upload/delete/move
                }
            }.start(wait = true)
        }
    }

    private fun generatePWA(): String {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no">
            <meta name="apple-mobile-web-app-capable" content="yes">
            <style>
                body { background: #0f172a; color: white; font-family: -apple-system, sans-serif; padding: 20px; }
                .card { background: #1e293b; padding: 15px; border-radius: 12px; margin-bottom: 10px; border: 1px solid #334155; }
                .btn { background: #3b82f6; color: white; padding: 12px; border: none; border-radius: 8px; width: 100%; font-weight: bold; }
                .folder-icon { color: #f59e0b; font-size: 20px; margin-right: 10px; }
            </style>
        </head>
        <body>
            <h3>🗄️ Gerenciador Pendrive TV</h3>
            <div class="card">
                <input type="file" id="file" multiple style="display:none" onchange="upload()">
                <button class="btn" onclick="document.getElementById('file').click()">➕ INSERIR ARQUIVOS</button>
            </div>
            <div id="file-list">
                <p>Carregando arquivos do pendrive...</p>
            </div>
        </body>
        </html>
        """.trimIndent()
    }
}