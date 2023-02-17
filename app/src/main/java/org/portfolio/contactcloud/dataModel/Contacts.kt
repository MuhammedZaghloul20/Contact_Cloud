package org.portfolio.contactcloud.dataModel

import android.graphics.drawable.Drawable
import android.widget.ImageView
import org.portfolio.contactcloud.R

data class contacts(
    val contactname: String,
    val contactNumber:String,
    var selected:Boolean=false)
