package me.phantomx.githubuserapp.extension

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import me.phantomx.githubuserapp.R
import me.phantomx.githubuserapp.data.HttpResponse
import me.phantomx.githubuserapp.records.AppManager.SETTINGS_DATA_STORE
import me.phantomx.githubuserapp.records.AppManager.gson
import me.phantomx.githubuserapp.records.AppManager.okHttpClient
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.resume


val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = SETTINGS_DATA_STORE)

inline fun Context.showDialog(
    title: String,
    message: String,
    builder: MaterialAlertDialogBuilder.() -> Unit = {}
) {
    MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
        .apply(builder)
        .show()
}

suspend inline fun <reified T : Any> String.toObj(crossinline isValid: (T) -> Boolean = { true }): T? {
    val type = object : TypeToken<T>() {}.type
    return withContext(Default) {
        this@toObj.safeRun {
            val r = gson.fromJson<T>(this, type)
            if (r.let(isValid))
                r
            else null
        }
    }
}


@OptIn(ExperimentalContracts::class)
inline fun <T, R> T.safeRun(block: T.() -> R?): R? {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return try {
        block()
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        e.printStackTrace()
        null
    }
}

@OptIn(ExperimentalContracts::class)
inline fun String?.ifEmptyOrNullDo(block: String.() -> Unit): String {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    with(this ?: "") {
        if (isEmpty()) block()
        return this
    }
}

suspend inline fun String.httpGet(context: Context, crossinline block: suspend (response: HttpResponse) -> Unit) {
    val call = okHttpClient.newCall(Request.Builder().url(this@httpGet).build())

    val response = suspendCancellableCoroutine { conti ->
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                conti.resume(HttpResponse(false, e.message ?: context.resources.getString(R.string.unknown_error_message)))
            }
            override fun onResponse(call: Call, response: Response) {
                conti.resume(safeRun {
                    HttpResponse(true, response.body?.string() ?: "")
                } ?: HttpResponse(false, context.resources.getString(R.string.unknown_error_message)))
            }
        })
        conti.invokeOnCancellation {
            if (!call.isCanceled())
                call.cancel()
        }
    }

    block(response)
}