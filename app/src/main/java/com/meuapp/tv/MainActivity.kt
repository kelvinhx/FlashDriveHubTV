package com.meuapp.tv

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.http.*
import java.io.File

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inicia o servidor para o iPhone se conectar
        embeddedServer(Netty, port = 8080) {
            routing {
                get("/") {
                    call.respondText(
                        "<html><body style='background:#000;color:#fff;text-align:center;padding:50px;font-family:sans-serif;'>" +
                        "<h1>Conectado à TCL 32S5400AF</h1>" +
                        "<p>O Hub de Arquivos está ativo.</p></body></html>",
                        ContentType.Text.Html
                    )
                }
            }
        }.start(wait = false)
    }
}