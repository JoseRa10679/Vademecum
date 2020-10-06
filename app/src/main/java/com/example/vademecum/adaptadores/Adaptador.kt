package com.example.vademecum.adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vademecum.R
import com.example.vademecum.dataclass.MiFarmaco
import kotlinx.android.synthetic.main.item.view.*
import kotlin.math.max

/**
 * Adapter para que funcione el RecyclerView
 * @author José Ramón Laperal
 * @param context
 * @param miLista
 * @param clickListner
 */
class Adaptador(
    private val context: Context,
    private val miLista: List<MiFarmaco>,
    private var clickListner: OnFarItemClickListner
) : RecyclerView.Adapter<Adaptador.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return miLista.count()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val miP = miLista[position]
        holder.setData(miP, clickListner)
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(f: MiFarmaco?, action: OnFarItemClickListner) {
            with(itemView){
                itemFarmaco.text = f!!.nombre
                itemLaboratorio.text = f.labtitular

                setOnClickListener {
                    action.onItemClick(f, absoluteAdapterPosition)
                }
            }
        }
    }

    //<editor-folder desc = " Animación ">

    /**
     * Código para activar la animación
     * @param holder
     */
    override fun onViewAttachedToWindow(holder: MyViewHolder) {
        super.onViewAttachedToWindow(holder)
        animatedCircularReveal(holder.itemView)
    }

    /**
     * Datos de la animación
     * @param view Vista
     */
    private fun animatedCircularReveal(view: View) {
        val centerX = 0
        val centerY = 0
        val startRadious = 0f
        val endRadious: Float = max(view.width, view.height).toFloat()
        ViewAnimationUtils.createCircularReveal(
            view,
            centerX,
            centerY,
            startRadious,
            endRadious
        ).start()

    }

    //</editor-folder>

}

/**
 * Interface que habilita el click del recyclerview
 */
interface OnFarItemClickListner {
    fun onItemClick(item: MiFarmaco, position: Int)
}