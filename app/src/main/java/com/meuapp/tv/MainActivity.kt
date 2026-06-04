package com.meuapp.tv

import android.content.*
import android.graphics.*
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.http.*
import java.io.File
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private lateinit var statusText: TextView
    private lateinit var qrView: ImageView
    private lateinit var usbStatusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val ipAddress = getLocalIpAddress() ?: "Sem Wi-Fi"
        setupUI(ipAddress)
        startServer()
        checkUsbStatus()
    }

    private fun setupUI(ip: String) {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#0F172A"))
            gravity = Gravity.CENTER
            setPadding(40, 40, 40, 40)
        }

        val title = TextView(this).apply {
            text = "⚡ FLASH DRIVE HUB"
            setTextColor(Color.CYAN)
            textSize = 30f
            setTypeface(null, Typeface.BOLD)
        }

        // Exibe o QR Code para o iPhone
        qrView = ImageView(this).apply {
            val params = LinearLayout.LayoutParams(400, 400)
            params.setMargins(0, 40, 0, 40)
            layoutParams = params
            setImageBitmap(generateQRCode("http://$ip:8080"))
        }

        statusText = TextView(this).apply {
            text = "Acesse no iPhone:\nhttp://$ip:8080"
            setTextColor(Color.WHITE)
            textSize = 20f
            gravity = Gravity.CENTER
        }

        usbStatusText = TextView(this).apply {
            text = "\n[ AGUARDANDO PENDRIVE ]"
            setTextColor(Color.YELLOW)
            textSize = 16f
        }

        // Botão interativo para abrir o gerenciador (D-pad compatível)
        val btnOpen = Button(this).apply {
            text = "EXPLORAR PENDRIVE"
            isFocusable = true
            requestFocus()
            setOnClickListener { Toast.makeText(context, "Abrindo arquivos...", Toast.LENGTH_SHORT).show() }
        }

        root.addView(title)
        root.addView(qrView)
        root.addView(statusText)
        root.addView(usbStatusText)
        root.addView(btnOpen)
        setContentView(root)
    }

    private fun getLocalIpAddress(): String? {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress.hostAddress.contains(".")) {
                        return inetAddress.hostAddress
                    }
                }
            }
        } catch (ex: Exception) { ex.printStackTrace() }
        return null
    }

    private fun generateQRCode(text: String): Bitmap {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512)
        val bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565)
        for (x in 0 until 512) {
            for (y in 0 until 512) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }

    private fun checkUsbStatus() {
        val storageDir = File("/storage/")
        val usbDir = storageDir.listFiles()?.firstOrNull { it.name.contains("-") }
        if (usbDir != null) {
            usbStatusText.text = "✅ PENDRIVE DETECTADO: ${usbDir.name}"
            usbStatusText.setTextColor(Color.GREEN)
        }
    }

    private fun startServer() {
        thread {
            embeddedServer(Netty, port = 8080) {
                routing {
                    get("/") { call.respondText("<h1>Hub Conectado!</h1>", ContentType.Text.Html) }
                }
            }.start(wait = true)
        }
    }
}