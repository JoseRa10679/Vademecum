package com.example.vademecum.objetos

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vademecum.R
import kotlinx.android.synthetic.main.item.view.*

class Adaptador(private val context: Context, private val miLista: List<MiFarmaco>, private var clickListner: OnFarItemClickListner):RecyclerView.Adapter<Adaptador.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return miLista.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val miP = miLista[position]
        holder.setData(miP, clickListner)


    }

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun setData(f: MiFarmaco?, action: OnFarItemClickListner){
            itemView.itemFarmaco.text = f!!.nombre
            itemView.itemLaboratorio.text = f.labtitular

            itemView.setOnClickListener{
                action.onItemClick(f, adapterPosition)
            }

        }
    }
}

// Interfase que habilita el click del recyclerview
interface OnFarItemClickListner{
    fun onItemClick(item: MiFarmaco, position: Int)
}