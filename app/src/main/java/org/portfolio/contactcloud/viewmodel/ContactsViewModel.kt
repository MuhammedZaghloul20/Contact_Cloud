package org.portfolio.contactcloud.viewmodel

import androidx.lifecycle.ViewModel
import org.portfolio.contactcloud.Adapters.ContactsAdapter
import org.portfolio.contactcloud.Adapters.contacts

class ContactsViewModel:ViewModel() {
    var contactsList = ArrayList<contacts>()
     var adapter=ContactsAdapter(contactsList)



}