package org.portfolio.contactcloud.viewModel

import androidx.lifecycle.ViewModel
import org.portfolio.contactcloud.Adapters.ContactsAdapter
import org.portfolio.contactcloud.dataModel.contacts

class ContactsViewModel:ViewModel() {
    var contactsList = ArrayList<contacts>()
     var adapter=ContactsAdapter(contactsList)



}