package id.dev.oxy.ui.history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import id.dev.oxy.data.model.history.adapter.HistoryRow
import id.dev.oxy.data.model.history.adapter.HistoryType
import id.dev.oxy.databinding.ItemHistoryBinding
import id.dev.oxy.databinding.ItemHistoryHeaderBinding
import javax.inject.Inject

class HistoryAdapter @Inject constructor() : PagingDataAdapter<HistoryRow,
        RecyclerView.ViewHolder>(DiffCallBack) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (HistoryType.values()[viewType]) {
            HistoryType.Header -> HeaderViewHolder(
                ItemHistoryHeaderBinding.inflate(
                    LayoutInflater.from(parent.context), parent,
                    false
                )
            )
            HistoryType.History -> HistoryViewHolder(
                ItemHistoryBinding.inflate(
                    LayoutInflater.from(parent.context), parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val historyType = getItem(position)
        if (historyType != null) {
            when (historyType) {
                is HistoryRow.Header -> (holder as HeaderViewHolder).bind(historyType)
                is HistoryRow.History -> {
                    (holder as HistoryViewHolder).apply {
                        bind(historyType)
                        holder.itemView.setOnClickListener {
                            onItemClickCallback.onItemClicked(historyType.id)
                        }
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.historyType?.ordinal ?: -1
    }

    class HeaderViewHolder(private val binding: ItemHistoryHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(header: HistoryRow.Header) {
            binding.txtDatePayment.text = header.date
        }
    }

    class HistoryViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(history: HistoryRow.History) {
            binding.apply {
//                ivPayment.setImageResource(
//                    when (history.) {
//                        PaymentType.Card -> R.drawable.ic_round_credit_card_24
//                        PaymentType.Cash -> R.drawable.round_money_24
//                        PaymentType.Ewallet -> R.drawable.ic_round_account_balance_wallet_24
//                    }
//                )
                tvIdPayment.text = history.invoiceNo
                tvTotalPayment.text = history.totalPayment
                tvTimePayment.text = history.date
                tvStatusPayment.text = history.status
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(id: Int)
    }

    companion object DiffCallBack : DiffUtil.ItemCallback<HistoryRow>() {
        override fun areItemsTheSame(oldItem: HistoryRow, newItem: HistoryRow): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: HistoryRow, newItem: HistoryRow): Boolean {
            return oldItem.historyType == newItem.historyType
        }
    }
}