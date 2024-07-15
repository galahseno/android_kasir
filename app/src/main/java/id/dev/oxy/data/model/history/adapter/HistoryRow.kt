package id.dev.oxy.data.model.history.adapter

sealed class HistoryRow(val historyType: HistoryType) {
    data class Header(val date: String) : HistoryRow(HistoryType.Header)
    data class History(
        val totalPayment: String,
        val id: Int,
        val invoiceNo: String,
        val date: String,
        val status: String,
    ) :
        HistoryRow(HistoryType.History)
}
