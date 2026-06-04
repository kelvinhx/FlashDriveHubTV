package com.meuapp.tv

import android.os.Bundle
import android.widget.*
import android.graphics.*
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.http.*
import io.ktor.http.content.*
import java.io.File
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Interface da TV
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#0F172A"))
            gravity = Gravity.CENTER
            setPadding(50, 50, 50, 50)
        }
        
        val title = TextView(this).apply {
            text = "⚡ FLASH DRIVE HUB TV"
            setTextColor(Color.CYAN)
            textSize = 28f
            setPadding(0, 0, 0, 40)
        }
        
        val info = TextView(this).apply {
            text = "1. Conecte o Pendrive\n2. Acesse pelo iPhone o IP abaixo:\n\nPORTA: 8080"
            setTextColor(Color.WHITE)
            textSize = 18f
            gravity = Gravity.CENTER
        }
        
        layout.addView(title)
        layout.addView(info)
        setContentView(layout)

        // Inicia o servidor de transferência
        thread {
            embeddedServer(Netty, port = 8080) {
                routing {
                    get("/") {
                        call.respondText(generatePWA(), ContentType.Text.Html)
                    }
                    
                    // ROTA PARA RECEBER ARQUIVOS DO IPHONE
                    post("/upload") {
                        val multipart = call.receiveMultipart()
                        multipart.forEachPart { part ->
                            if (part is PartData.FileItem) {
                                // Tenta achar o pendrive em /storage/
                                val storageDir = File("/storage/")
                                val usbDir = storageDir.listFiles()?.firstOrNull { it.name.contains("-") } ?: storageDir
                                
                                val folder = File(usbDir, "Transferencias_TV")
                                if (!folder.exists()) folder.mkdirs()
                                
                                val file = File(folder, part.originalFileName ?: "arquivo_desconhecido")
                                part.streamProvider().use { input ->
                                    file.outputStream().use { output -> input.copyTo(output) }
                                }
                            }
                            part.dispose()
                        }
                        call.respondText("OK")
                    }
                }
            }.start(wait = true)
        }
    }

    private fun generatePWA(): String {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
            <style>
                body { background: #0F172A; color: white; font-family: sans-serif; text-align: center; padding: 30px; }
                .btn { background: #38BDF8; color: #0F172A; padding: 20px; border-radius: 15px; border: none; font-weight: bold; width: 100%; font-size: 18px; }
                .card { background: #1E293B; padding: 20px; border-radius: 20px; border: 1px solid #334155; margin-top: 20px; }
            </style>
        </head>
        <body>
            <h2>Hub de Transferência</h2>
            <div class="card">
                <input type="file" id="fileInput" multiple style="display:none">
                <button class="btn" onclick="document.getElementById('fileInput').click()">📁 SELECIONAR ARQUIVOS</button>
                <p id="status" style="margin-top:15px; color: #94A3B8;">Aguardando seleção...</p>
            </div>
            <script>
                document.getElementById('fileInput').onchange = async (e) => {
                    const status = document.getElementById('status');
                    status.innerText = "Enviando para a TV...";
                    const formData = new FormData();
                    for (let file of e.target.files) {
                        formData.append('file', file);
                    }
                    await fetch('/upload', { method: 'POST', body: formData });
                    status.innerText = "✅ Concluído! Verifique o Pendrive.";
                };
            </script>
        </body>
        </html>
        """.trimIndent()
    }
}