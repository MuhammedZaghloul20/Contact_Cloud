package org.portfolio.contactcloud.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import org.portfolio.contactcloud.R

class ContactsAdapter(private val facelist:ArrayList<contacts>) : RecyclerView.Adapter<ContactsAdapter.MyViewHolder> (){
   class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
       val contactImage : ImageView = itemView.findViewById(R.id.contact_image)
       val contactName : TextView = itemView.findViewById(R.id.contact_name)
       val contactNumber : TextView = itemView.findViewById(R.id.contact_number)

       }

override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
 val itemView = LayoutInflater.from(parent.context).inflate(R.layout.contact_item , parent,false)
    return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = facelist[position]
        holder.contactName.text = currentItem.contactname
        holder.contactNumber.text = currentItem.contactNumber
        //holder.contactImage.setImageDrawable(currentItem.image)
               }

    override fun getItemCount(): Int {
        return facelist.size
    }

    fun add(contacts: contacts) {
        facelist.add(contacts)
        notifyDataSetChanged()

    }


}
