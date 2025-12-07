package com.example.da.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.da.R // Đảm bảo R.layout.item_history có sẵn
import com.example.da.model.HistoryEntity // Import HistoryEntity từ model

class HistoryAdapter(
    private var list: List<HistoryEntity>,
    // Callback được sử dụng khi người dùng muốn xóa (Long Click)
    private val onDeleteClicked: (HistoryEntity) -> Unit,
    // Callback được sử dụng khi người dùng muốn sửa (Click)
    private val onEditClicked: (HistoryEntity) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvID: TextView = itemView.findViewById(R.id.tvID)
        val tvMonHoc: TextView = itemView.findViewById(R.id.tvMonHoc)
        val tvTenDe: TextView = itemView.findViewById(R.id.tvTenDe)
        val tvDiem: TextView = itemView.findViewById(R.id.tvDiem)
        val tvTG: TextView = itemView.findViewById(R.id.tvTG)
        val tvNgay: TextView = itemView.findViewById(R.id.tvNgay)
        val tvMucDo: TextView = itemView.findViewById(R.id.tvMucDo)

        init {
            // Nhấn giữ (Long Click) để XÓA
            itemView.setOnLongClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onDeleteClicked(list[adapterPosition])
                    true
                } else {
                    false
                }
            }
            // Nhấn (Click) để SỬA
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onEditClicked(list[adapterPosition])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Sử dụng item_history.xml để hiển thị mỗi mục
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val i = list[position]
        holder.tvID.text = i.id.toString()
        holder.tvMonHoc.text = i.monHoc
        holder.tvTenDe.text = i.tenDe
        holder.tvDiem.text = i.diem
        holder.tvTG.text = i.thoiGianLam
        holder.tvNgay.text = i.ngayLam
        holder.tvMucDo.text = i.mucDo
    }

    /**
     * Cập nhật danh sách hiển thị và làm mới RecyclerView.
     */
    fun update(newList: List<HistoryEntity>) {
        list = newList
        notifyDataSetChanged()
    }
}