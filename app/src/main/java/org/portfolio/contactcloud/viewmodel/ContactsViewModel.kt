package org.portfolio.contactcloud.viewmodel

import androidx.lifecycle.ViewModel
import org.portfolio.contactcloud.Adapters.contacts

class ContactsViewModel:ViewModel() {
    val contactsList = ArrayList<contacts>()
}