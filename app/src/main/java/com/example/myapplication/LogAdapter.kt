package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime

class LogAdapter : RecyclerView.Adapter<LogAdapter.VH>() {

    companion object {

        private val TIME_FORMAT: DateTimeFormat<LocalTime> = LocalTime.Format {
            hour(); char(':'); minute(); char(':'); second(); char('.'); this.secondFraction(minLength = 3, maxLength = 3)
        }

    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timestamp: TextView = itemView.findViewById(R.id.timestamp)
        val msg: TextView = itemView.findViewById(R.id.msg)
    }

    data class Item(
        val msg: String,
        val time: LocalTime
    )

    private val logs = mutableListOf<Item>()

    fun append(msg: String) {
        logs.add(
            Item(
                msg = msg,
                time = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time
            )
        )
        notifyItemInserted(logs.size-1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.log_adapter_item, parent, false)
        )
    }

    override fun getItemCount(): Int = logs.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        logs[position].let { item ->
            holder.timestamp.text = item.time.format(TIME_FORMAT)
            holder.msg.text = item.msg
        }
    }

}
