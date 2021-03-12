package todo.quang.mvvm.utils.extension

import java.io.IOException
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.MalformedURLException
import java.net.URL

fun isInternetAvailable(): Boolean {
    return try {
        val address = InetAddress.getByName("google.com")
        !address.equals("")
    } catch (e: java.lang.Exception) {
        false
    }
}

fun isInternetAvailable(url: String): Boolean {
    var connection: HttpURLConnection? = null
    return try {
        connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "HEAD"
        connection.connectTimeout = 5 * 1000
        connection.connect()
        true
    } catch (e: MalformedURLException) {
        false
    } catch (e: IOException) {
        false
    } finally {
        connection?.disconnect()
    }
}

private fun executeCommand(ip: String): Boolean {
    return try {
        val process = Runtime.getRuntime().exec("/system/bin/ping -c 1 $ip")
        process.waitFor() == 0
    } catch (e: Exception) {
        false
    }
}