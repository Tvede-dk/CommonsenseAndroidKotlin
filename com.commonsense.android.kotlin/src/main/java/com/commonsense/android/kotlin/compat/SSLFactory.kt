package com.commonsense.android.kotlin.compat

import java.net.InetAddress
import java.net.Socket
import javax.net.ssl.*

/**
 * Created by kasper on 10/06/2017.
 */

enum class SSLContextProtocols(val algorithmName: String) {
    SSL("SSL"), SSLv2("SSLv2"), SSLv3("SSLv3"),
    TLS("TLS"), TLSv1("TLSv1"), TLSv11("TLSv1.1"),
    TLSv12("TLSv1.2");

    fun createContext(): SSLContext? {
        return SSLContext.getInstance(algorithmName)
    }

    fun createSocketFactory(): SSLSocketFactory? {
        return createContext()?.run {
            init(null, null, null)
            socketFactory
        }
    }
}


/**
 * Enables TLS 1.2 for old androids (android api 16 ~ 20 )
 */
class SSLSocketFactoryCompat : SSLSocketFactory() {
    val factory: SSLSocketFactory

    init {
        val optFactory = SSLContextProtocols.TLSv12.createSocketFactory()
        factory = optFactory ?: throw RuntimeException("Cannot work with SSL / TLS when its not available.")
    }


    override fun getSupportedCipherSuites(): Array<out String> = factory.supportedCipherSuites

    override fun getDefaultCipherSuites(): Array<out String> = factory.defaultCipherSuites

    override fun createSocket(s: Socket?, host: String?, port: Int, autoClose: Boolean): Socket {
        return enableTLSOnSocket(factory.createSocket(s, host, port, autoClose))
    }

    override fun createSocket(host: String?, port: Int): Socket {
        return enableTLSOnSocket(factory.createSocket(host, port))
    }


    override fun createSocket(host: String?, port: Int, localHost: InetAddress?, localPort: Int): Socket {
        return enableTLSOnSocket(factory.createSocket(host, port, localHost, localPort))
    }

    override fun createSocket(host: InetAddress?, port: Int): Socket {
        return enableTLSOnSocket(factory.createSocket(host, port))
    }

    override fun createSocket(address: InetAddress?, port: Int, localAddress: InetAddress?, localPort: Int): Socket {
        return enableTLSOnSocket(factory.createSocket(address, port, localAddress, localPort))
    }

    private fun enableTLSOnSocket(socket: Socket): Socket {
        if (socket is SSLSocket) {
            socket.enabledProtocols = arrayOf("TLSv1.2")
        }
        return socket
    }


}