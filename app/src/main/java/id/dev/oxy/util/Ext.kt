package id.dev.oxy.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import id.dev.oxy.data.model.exception.ErrorMessage
import id.dev.oxy.data.model.history.Data
import id.dev.oxy.data.model.history.adapter.HistoryRow
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

fun List<Data>.mapToHistoryRow(): List<HistoryRow> {
    return groupBy { it.transactionDate.formatToDate() }.flatMap {
        listOf(HistoryRow.Header(it.key), *(it.value.map { history ->
            (HistoryRow.History(
                id = history.id,
                invoiceNo = buildString {
                    append("#")
                    append(history.invoiceNo)
                },
                status = history.paymentStatus,
                totalPayment = history.finalTotal.toCurrencyFormat(),
                date = history.transactionDate.toTimeGMT()
            ))
        }).toTypedArray())
    }
}

fun String.formatToDate(): String {
    val date = this.substringBefore(" ")
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale("in", "ID")).parse(date)
    val outputFormat = SimpleDateFormat("EEEE - dd/MM/yyyy", Locale("in", "ID"))

    return inputFormat?.let { outputFormat.format(it) } ?: ""
}

fun String.formatToDateDetail(): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale("in", "ID")).parse(this)
    val outputFormat = SimpleDateFormat("dd/MM/yy HH:mm", Locale("in", "ID"))

    return inputFormat?.let { outputFormat.format(it) } ?: ""
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(rootView.windowToken, 0)
}

fun createChip(
    context: Context,
    chipId: Int,
    chipName: String,
    onClickListener: (View) -> Unit
): Chip {
    return Chip(context).apply {
        id = chipId
        text = chipName
        setOnClickListener(onClickListener)
    }
}

fun String.toDecimalFormat(): String {
    val decimalFormat = DecimalFormat("###,###,###")
    val decimalFormatSymbols = DecimalFormatSymbols()
    decimalFormat.minimumFractionDigits = 0
    decimalFormatSymbols.decimalSeparator = ','
    decimalFormatSymbols.groupingSeparator = '.'
    decimalFormat.decimalFormatSymbols = decimalFormatSymbols

    return decimalFormat.format(this.toDouble())
}

fun String.toCurrencyFormat(): String {
    val localeID = Locale("in", "ID")
    val doubleValue = this.toDoubleOrNull() ?: return this
    val numberFormat = NumberFormat.getCurrencyInstance(localeID)
    numberFormat.minimumFractionDigits = 0
    return numberFormat.format(doubleValue).replaceFirst("..".toRegex(), "$0 ")
}

val Boolean.int
    get() = if (this) 1 else 0

fun String.toTimeGMT(): String {
    val time = substringAfter(" ")
    return time.take(5)
}

fun Throwable.getErrorMessage(): String {
    return when (this) {
        is HttpException -> {
            when (code()) {
                in 400..451 -> parseHttpError(this)
                in 500..599 -> "Server Error"
                else -> "Undefined error"
            }
        }
        is UnknownHostException -> "Tidak ada koneksi Internet"
        is SocketTimeoutException -> "Connection Timeout. Silahkan Coba lagi"
        else -> message.toString()
    }
}

private fun parseHttpError(httpException: HttpException): String {
    return try {
        val errorBody = httpException.response()?.errorBody()?.charStream()
        val gson = Gson()
        val response = gson.fromJson(errorBody, ErrorMessage::class.java)
        response.message
    } catch (e: Exception) {
        e.message.toString()
    }
}

// TODO FIX PIN DIALOG TIME