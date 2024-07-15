package id.dev.oxy.ui.draft.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import id.dev.oxy.data.local.entity.DraftTable
import id.dev.oxy.databinding.ItemCustomerBinding
import javax.inject.Inject

class DraftAdapter @Inject constructor() :
    ListAdapter<DraftTable, DraftAdapter.MainViewHolder>(DiffCallBack) {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(id: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            ItemCustomerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val productTable = getItem(position)
        holder.bind(productTable)
        holder.itemView.setOnClickListener {
            if (productTable.id != null) {
                onItemClickCallback.onItemClicked(productTable.id)
            }
        }
    }

    class MainViewHolder(private val binding: ItemCustomerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(draftTable: DraftTable) {
            binding.apply {
                binding.txtCustomerName.text = draftTable.customerName
            }
        }
    }

    companion object DiffCallBack : DiffUtil.ItemCallback<DraftTable>() {
        override fun areItemsTheSame(oldItem: DraftTable, newItem: DraftTable): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: DraftTable, newItem: DraftTable): Boolean {
            return oldItem.id == newItem.id
        }
    }
}