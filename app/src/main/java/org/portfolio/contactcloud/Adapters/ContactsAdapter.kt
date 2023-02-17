package org.portfolio.contactcloud.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.contact_item.view.*
import org.portfolio.contactcloud.dataModel.contacts
import org.portfolio.contactcloud.databinding.ContactItemBinding

class ContactsAdapter( var facelist:ArrayList<contacts>) : RecyclerView.Adapter<ContactsAdapter.MyViewHolder> (){
    class MyViewHolder(itemView: ContactItemBinding): RecyclerView.ViewHolder(itemView.root){
        val contactImage : ImageView = itemView.contactImage
        val contactName : TextView = itemView.contactName
        val contactNumber : TextView = itemView.contactNumber
        val selectNumber :CheckBox =itemView.selected
        fun Bind(currentItem: contacts){
            contactName.text = currentItem.contactname
            contactNumber.text = currentItem.contactNumber
            selectNumber.isChecked=currentItem.selected
            itemView.selected.setOnClickListener{
                if(currentItem.selected)
                {
                    currentItem.selected=false
                    selectNumber.isChecked=currentItem.selected

                }
                else
                {
                    currentItem.selected=true
                    selectNumber.isChecked=currentItem.selected
                }
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
        val binding=ContactItemBinding.inflate(itemView , parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = facelist[position]
        holder.Bind(currentItem)
    }

    override fun getItemCount(): Int {
        return facelist.size
    }

    fun add(contacts: contacts) {
        facelist.add(contacts)
        notifyDataSetChanged()

    }

    fun notifyDataSetChange() {
        notifyDataSetChanged()
    }


}
