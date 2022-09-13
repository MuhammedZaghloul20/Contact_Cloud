package org.portfolio.contactcloud.ui


import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.TextUtils.isEmpty
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AlertDialogLayout
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.chosing_photo_source.view.*
import kotlinx.android.synthetic.main.custom_alert_dialog.view.*
import org.portfolio.contactcloud.Adapters.ContactsAdapter
import org.portfolio.contactcloud.Adapters.contacts
import org.portfolio.contactcloud.R
import org.portfolio.contactcloud.viewmodel.ContactsViewModel


private const val pickImage = 100
private const val pickPhoto = 1000

val database = FirebaseDatabase.getInstance()
val myRef = database.getReference("contacts")


class MainActivity : AppCompatActivity() {
    private  var pulled = false
    private lateinit var contactsList: ArrayList<contacts>
    private lateinit var adapter: ContactsAdapter
    lateinit var view: View

    private val vm:ContactsViewModel by lazy {
        ViewModelProviders.of(this).get(ContactsViewModel::class.java)
    }


    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //setting toolbar
        setSupportActionBar(toolbar)


        //ArrayList initialization
        contactsList = arrayListOf()

        //adapting the recyclerView
        adapter = ContactsAdapter(contactsList)
        contacts_recyclerView.adapter = adapter
        contacts_recyclerView.layoutManager = LinearLayoutManager(this)


        //upload contacts to the array
        upload.setOnClickListener{

            if(!pulled){
            val x=getAllContacts()
            for (i in x)
            {
                adapter.add(i)
            }
            pulled=true}
            val alertBuilder=AlertDialog.Builder(this@MainActivity)
            alertBuilder.setNegativeButton(
                "No",
                { dialog: DialogInterface, i: Int ->

                }
            )
            alertBuilder.setPositiveButton(
                "Yes"
            ) { dialog: DialogInterface, i: Int ->
                for (x in contactsList)
                {
                    myRef.push().setValue(x.contactname)
                    val toast=Toast.makeText(applicationContext,"${x.contactNumber}",Toast.LENGTH_SHORT).show()

                }
                Snackbar.make(snack, "Contacts has been Uploaded", Snackbar.LENGTH_SHORT).show()
            }
            alertBuilder.setCancelable(false)
            val dialog=alertBuilder.create()
            dialog.setTitle("Confirm your upload")
            dialog.show()



        }

        //download contacts from firebase
        download.setOnClickListener{

//            myRef.addValueEventListener(object: ValueEventListener {
//
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    // This method is called once with the initial value and again
//                    // whenever data at this location is updated.
//                    val value = snapshot.getValue<String>()
//
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//
//                }
//            })
            Toast.makeText(applicationContext, "Not yet", Toast.LENGTH_SHORT).show()
        }


    }
    //Reading Contacts
    private fun getAllContacts( ):ArrayList<contacts> {
       val contactsList= ArrayList<contacts>()
        val contentResolver = contentResolver
        val cursor: Cursor? = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    val hasPhoneNumber: Int =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                            .toInt()
                    if (hasPhoneNumber > 0) {
                        val id: String =
                            cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                        val name: String =
                            cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                        val phoneCursor: Cursor? = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", arrayOf(id),
                            null
                        )
                        if (phoneCursor != null) {
                            if (phoneCursor.moveToNext()) {
                                var phoneNumber: String =
                                    phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                                if(phoneNumber[0]!='+'&&phoneNumber[0]!='2')
                                {
                                    var s="+2"+phoneNumber
                                    phoneNumber=s
                                }

                                contactsList.add(contacts(name,phoneNumber))
                                phoneCursor.close()
                            }
                        }
                    }
                }
            }
        }
        return contactsList
    }


    //Menu inflation
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //menu item actions
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> {

                view = LayoutInflater.from(this@MainActivity)
                    .inflate(R.layout.custom_alert_dialog, null)
                var builder = android.app.AlertDialog.Builder(this@MainActivity).apply {
                    setView(view)
                    setCancelable(false)

                }
                var dialog = builder.create()
                dialog.show()

                //Picking the image
                view.pick_image.setOnClickListener {
                    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)

                    val view2 = LayoutInflater.from(this@MainActivity)
                        .inflate(R.layout.chosing_photo_source, null)
                    val builder2 = AlertDialog.Builder(this@MainActivity).setView(view2)
                    val dialog2 = builder2.create()
                    dialog2.show()

                    view2.camera.setOnClickListener {

                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(intent, pickPhoto)
                        dialog2.dismiss()


                    }


                    //get image from gallery
                    view2.gallery.setOnClickListener {

                        val intent = Intent()
                        intent.setType("image/*")
                        intent.setAction(Intent.ACTION_GET_CONTENT)

                        startActivityForResult(intent, pickImage)
                        dialog2.dismiss()
                    }


                }


                //adding new contact
                view.adding_num.setOnClickListener {

                    if (isEmpty(view.add_name.text.toString()) || isEmpty(view.add_number.text.toString()))
                        Snackbar.make(snack, "Something is empty", Snackbar.LENGTH_SHORT).show()
                    else {
                        val x = contacts(
                          //  view.pick_image.drawable,
                            view.add_name.text.toString(),
                            view.add_number.text.toString()
                        )

                        adapter.add(x)


                        dialog.dismiss()
                    }

                    //cancel adding
                    view.cancel_adding.setOnClickListener {
                        dialog.dismiss()
                    }


                }

            }

        }
        return super.onOptionsItemSelected(item)
    }


    //the received pictures
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == pickPhoto && resultCode == Activity.RESULT_OK) {
            val image = data?.getParcelableExtra<Bitmap>("data")
            view.pick_image.setImageBitmap(image)

        } else if (requestCode == pickImage && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            view.pick_image.setImageURI(uri)
        }
    }
}

