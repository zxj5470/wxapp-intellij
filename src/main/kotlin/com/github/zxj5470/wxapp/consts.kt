package com.github.zxj5470.wxapp

import com.intellij.CommonBundle
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiFile
import org.jetbrains.annotations.PropertyKey
import java.io.InputStream
import java.util.*
import java.io.InputStreamReader
import java.util.PropertyResourceBundle
import java.util.ResourceBundle


const val WXML_EXT = "wxml"
const val WXSS_EXT = "wxss"

val APP_JS_KEY = Key.create<PsiFile?>("wxapp-app.js")
val APP_WXSS_KEY = Key.create<PsiFile?>("wxapp-app.wxss")
val APP_JSON_KEY = Key.create<PsiFile?>("wxapp-app.json")
typealias JavaStr = java.lang.String

object WxappBundle {
	const val NAME = "message.wxapp-bundle"
	val bundle: ResourceBundle by lazy { ResourceBundle.getBundle(NAME, JuliaUTF8Control) }
	@JvmStatic
	fun message(@PropertyKey(resourceBundle = NAME) key: String, vararg params: Any): String =
		CommonBundle.message(bundle, key, *params)
}

/**
 * Well. The name is Julia ㅋㅋ
 */
object JuliaUTF8Control : ResourceBundle.Control() {
	override fun newBundle(
		baseName: String, locale: Locale, format: String, loader: ClassLoader, reload: Boolean): ResourceBundle {
		val bundleName = toBundleName(baseName, locale)
		val resourceName = toResourceName(bundleName, "properties")
		var bundle: ResourceBundle? = null
		var stream: InputStream? = null
		if (reload) {
			val url = loader.getResource(resourceName)
			if (url != null) {
				val connection = url.openConnection()
				if (connection != null) {
					connection.useCaches = false
					stream = connection.getInputStream()
				}
			}
		} else {
			stream = loader.getResourceAsStream(resourceName)
		}
		if (stream != null) {
			try {
				// Only this line is changed to make it to read properties files as UTF-8.
				bundle = PropertyResourceBundle(InputStreamReader(stream, "UTF-8"))
			} finally {
				stream.close()
			}
		}
		return bundle!!
	}
}