package id.dev.oxy.ui.draft.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.dev.oxy.R
import id.dev.oxy.data.local.entity.ProductTable
import id.dev.oxy.data.model.sales.discount.DiscountType
import id.dev.oxy.databinding.ItemDetialHistoryBinding
import id.dev.oxy.util.toCurrencyFormat
import id.dev.oxy.util.toDecimalFormat
import javax.inject.Inject
import kotlin.math.absoluteValue

class DraftDetailAdapter @Inject constructor() :
    ListAdapter<ProductTable, DraftDetailAdapter.MainViewHolder>(DiffCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            ItemDetialHistoryBinding.inflate(
                LayoutInflater.from(parent.context), parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val itemPurchased = getItem(position)
        holder.bind(itemPurchased)
    }

    class MainViewHolder(private val binding: ItemDetialHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(productTable: ProductTable) {
            binding.apply {
                val totalPrice =
                    (productTable.quantity.toDouble().times(productTable.unitPriceBeforeDiscount))

                txtItemName.text = productTable.productName
                txtItemPrice.text = totalPrice.toString().toCurrencyFormat()
                txtQuantityAndPrice.text = itemView.context.getString(
                    R.string.quantity_and_price,
                    productTable.quantity.toString(),
                    productTable.unitPriceBeforeDiscount.toString().toCurrencyFormat()
                )
                if (productTable.lineDiscountAmount > 0) {
                    when (productTable.lineDiscountType) {
                        DiscountType.fixed.name -> {
                            txtItemDiscount.text = itemView.context.getString(
                                R.string.discount_product,
                                productTable.lineDiscountAmount.toString().substringBefore(".").toDecimalFormat()
                            )
                        }
                        DiscountType.percentage.name -> {
                            val percentProduct =
                                productTable.lineDiscountAmount.absoluteValue.div(100)
                            val discountPercentProduct =
                                totalPrice.times(percentProduct)

                            txtItemDiscount.text = itemView.context.getString(
                                R.string.discount_product,
                                discountPercentProduct.toString().substringBefore(".").toDecimalFormat()
                            )
                        }
                    }
                } else {
                    txtItemDiscount.text = null
                }
            }
        }
    }

    companion object DiffCallBack : DiffUtil.ItemCallback<ProductTable>() {
        override fun areItemsTheSame(oldItem: ProductTable, newItem: ProductTable): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ProductTable, newItem: ProductTable): Boolean {
            return oldItem.productId == newItem.productId && oldItem.unitPrice == newItem.unitPrice
                    && oldItem.lineDiscountAmount == newItem.lineDiscountAmount
                    && oldItem.lineDiscountMember == newItem.lineDiscountMember
        }
    }
}