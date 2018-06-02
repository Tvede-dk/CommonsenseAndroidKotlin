package com.commonsense.android.kotlin.base.compat

import android.support.annotation.IntRange
import java.net.InetAddress
import java.net.Socket
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

/**
 * Created by kasper on 10/06/2017.
 */

enum class SSLContextProtocols(val algorithmName: String) {
    //The first part is insecure.
    //SSL
    SSL("SSL"),
    SSLv2("SSLv2"),
    SSLv3("SSLv3"),
    //TLS
    TLS("TLS"),
    TLSv1("TLSv1"),
    // Secure TLSes
    TLSv11("TLSv1.1"),
    TLSv12("TLSv1.2");

    fun createContext(): SSLContext? = SSLContext.getInstance(algorithmName)

    fun createSocketFactory(): SSLSocketFactory? = createContext()?.run {
        init(null, null, null)
        socketFactory
    }
}


/**
 * Enables TLS 1.2 for old androids (android api 16 ~ 20 )
 */
class SSLSocketFactoryCompat : SSLSocketFactory {

    constructor() : super() {
        val optFactory = SSLContextProtocols.TLSv12.createSocketFactory()
        factory = optFactory ?: throw RuntimeException("Cannot work with SSL / TLS when its not available.")
    }

    internal constructor(factory: SSLSocketFactory) : super() {
        this.factory = factory
    }

    private val factory: SSLSocketFactory

    override fun getSupportedCipherSuites(): Array<out String> = factory.supportedCipherSuites

    override fun getDefaultCipherSuites(): Array<out String> = factory.defaultCipherSuites

    override fun createSocket(): Socket =
            factory.createSocket().setProtocolToTls12()

    override fun createSocket(socket: Socket?,
                              host: String?,
                              @IntRange(from = 0, to = 65535) port: Int,
                              autoClose: Boolean): Socket =
            factory.createSocket(socket, host, port, autoClose).setProtocolToTls12()

    override fun createSocket(host: String?,
                              @IntRange(from = 0, to = 65535) port: Int): Socket =
            factory.createSocket(host, port).setProtocolToTls12()

    override fun createSocket(host: String?,
                              @IntRange(from = 0, to = 65535) port: Int,
                              localHost: InetAddress?,
                              localPort: Int): Socket =
            factory.createSocket(host, port, localHost, localPort).setProtocolToTls12()

    override fun createSocket(host: InetAddress?,
                              @IntRange(from = 0, to = 65535) port: Int): Socket =
            factory.createSocket(host, port).setProtocolToTls12()

    override fun createSocket(address: InetAddress?,
                              @IntRange(from = 0, to = 65535) port: Int,
                              localAddress: InetAddress?, localPort: Int): Socket =
            factory.createSocket(address, port, localAddress, localPort).setProtocolToTls12()

}

fun <T : Socket> T.setProtocolToTls12(): T {
    if (this is SSLSocket) {
        enabledProtocols = arrayOf(SSLContextProtocols.TLSv12.algorithmName)
    }
    return this
}