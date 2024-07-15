package id.dev.oxy.ui.payment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.dev.oxy.R
import id.dev.oxy.data.model.sales.discount.DiscountType
import id.dev.oxy.data.model.transaction.response.SellLine
import id.dev.oxy.databinding.ItemDetialHistoryBinding
import id.dev.oxy.util.toCurrencyFormat
import id.dev.oxy.util.toDecimalFormat
import javax.inject.Inject
import kotlin.math.absoluteValue

class PaymentSuccessAdapter @Inject constructor() :
    ListAdapter<SellLine, PaymentSuccessAdapter.MainViewHolder>(DiffCallBack) {

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
        fun bind(sellLine: SellLine) {
            binding.apply {
                val totalPrice = (sellLine.quantity.toDouble()
                    .times(sellLine.unitPriceBeforeDiscount.toDouble()))

                txtItemName.text = sellLine.product.name
                txtItemPrice.text = totalPrice.toString().toCurrencyFormat()
                txtQuantityAndPrice.text = itemView.context.getString(
                    R.string.quantity_and_price,
                    sellLine.quantity.substringBefore("."),
                    sellLine.unitPriceBeforeDiscount.substringBefore(".").toCurrencyFormat()
                )
                if (sellLine.lineDiscountAmount.substringBefore(".").toInt() > 0) {
                    when (sellLine.lineDiscountType) {
                        DiscountType.fixed.name -> {
                            txtItemDiscount.text = itemView.context.getString(
                                R.string.discount_product,
                                sellLine.lineDiscountAmount.substringBefore(".").toDecimalFormat()
                            )
                        }
                        DiscountType.percentage.name -> {
                            val percentProduct =
                                sellLine.lineDiscountAmount.substringBefore(".")
                                    .toDouble().absoluteValue.div(100)
                            val discountPercentProduct =
                                totalPrice.times(percentProduct)

                            txtItemDiscount.text = itemView.context.getString(
                                R.string.discount_product,
                                discountPercentProduct.toString().substringBefore(".")
                                    .toDecimalFormat()
                            )
                        }
                    }
                } else {
                    txtItemDiscount.text = null
                }
            }
        }
    }

    companion object DiffCallBack : DiffUtil.ItemCallback<SellLine>() {
        override fun areItemsTheSame(oldItem: SellLine, newItem: SellLine): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: SellLine, newItem: SellLine): Boolean {
            return oldItem.id == newItem.id && oldItem.unitPrice == newItem.unitPrice
                    && oldItem.lineDiscountAmount == newItem.lineDiscountAmount
                    && oldItem.lineDiscountMember == newItem.lineDiscountMember
        }
    }
}