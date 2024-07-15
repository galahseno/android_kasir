package id.dev.oxy.ui.sales.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import id.dev.oxy.data.model.sales.product.Data
import id.dev.oxy.databinding.ItemSalesBinding
import id.dev.oxy.util.toCurrencyFormat
import javax.inject.Inject

class SalesAdapter @Inject constructor() :
    PagingDataAdapter<Data, SalesAdapter.MainViewHolder>(DiffCallBack) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            ItemSalesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
            holder.itemView.setOnClickListener {
                onItemClickCallback.onItemClicked(data)
            }
        }
    }

    class MainViewHolder(private val binding: ItemSalesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Data) {
            binding.txtName.text = data.name
            binding.txtSku.text = data.sku
            binding.txtPrice.text =
                data.variations[0].defaultSellPrice.substringBefore(".").toCurrencyFormat()
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Data)
    }

    companion object DiffCallBack : DiffUtil.ItemCallback<Data>() {
        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem.id == newItem.id
        }
    }
}