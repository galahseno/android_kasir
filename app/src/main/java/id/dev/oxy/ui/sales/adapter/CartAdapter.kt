package id.dev.oxy.ui.sales.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.dev.oxy.data.local.entity.ProductTable
import id.dev.oxy.databinding.ItemCartBinding
import id.dev.oxy.util.toCurrencyFormat
import javax.inject.Inject

class CartAdapter @Inject constructor() :
    ListAdapter<ProductTable, CartAdapter.MainViewHolder>(DiffCallBack) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val productTable = getItem(position)
        holder.bind(productTable)
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(productTable)
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(productTable: ProductTable)
    }

    class MainViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(productTable: ProductTable) {
            binding.apply {
                txtName.text = productTable.productName
                txtQuantity.text = buildString {
                    append("Jumlah : ${productTable.quantity}")
                }
                txtPrice.text =
                    buildString {
                        append(
                            "Harga : ${
                                productTable.lineTotal.toString()
                                    .substringBefore(".").toCurrencyFormat()
                            }"
                        )
                    }
                txtDiscountProduct.text =
                    buildString {
                        append("Diskon aktif : ${productTable.discountProduct ?: "-"}")
                    }
            }
        }
    }

    companion object DiffCallBack : DiffUtil.ItemCallback<ProductTable>() {
        override fun areItemsTheSame(oldItem: ProductTable, newItem: ProductTable): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ProductTable, newItem: ProductTable): Boolean {
            return oldItem.productId == newItem.productId
        }
    }
}