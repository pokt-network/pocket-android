package network.pokt.pocketaion.util

import android.content.Context
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class RawFileUtil {

    companion object {
        fun readRawTextFile(context: Context, resId: Int): String? {
            val inputStream: InputStream
            try {
                inputStream = context.resources.openRawResource(resId)
            } catch (exception: Exception) {
                return null
            }

            val inputStreamReader = InputStreamReader(inputStream)
            val buffReader = BufferedReader(inputStreamReader)
            var line: String
            val text = StringBuilder()
            try {
                line = buffReader.readLine()
                while (line != null) {
                    text.append(line)
                    text.append('\n')
                }
            } catch (e: IOException) {
                return null
            }

            return text.toString()
        }
    }
}