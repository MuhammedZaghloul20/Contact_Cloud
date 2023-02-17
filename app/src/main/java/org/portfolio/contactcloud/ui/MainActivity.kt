package org.portfolio.contactcloud.ui

import android.app.Activity
import android.content.ContentProviderOperation
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.SpannableString
import android.text.TextUtils.isEmpty
import android.text.style.ForegroundColorSpan
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.chosing_photo_source.view.*
import kotlinx.android.synthetic.main.custom_alert_dialog.view.*
import kotlinx.android.synthetic.main.password_reset_alert.view.*
import kotlinx.coroutines.*
import org.portfolio.contactcloud.Adapters.ContactsAdapter
import org.portfolio.contactcloud.dataModel.contacts
import org.portfolio.contactcloud.GlobalSharedPreference
import org.portfolio.contactcloud.R
import org.portfolio.contactcloud.viewModel.ContactsViewModel


private const val pickImage = 100
private const val pickPhoto = 1000

val database = Firebase.database
var baseRef = database.getReference("contacts")


class MainActivity : AppCompatActivity() {
    lateinit var view: View
    lateinit var job: Job
    lateinit var auth:FirebaseAuth
    lateinit var pref: GlobalSharedPreference

    var alertDialog: AlertDialog? = null
    private val vm: ContactsViewModel by lazy {
        ViewModelProviders.of(this).get(ContactsViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //setting toolbar
        setSupportActionBar(toolbar)


        //shared prefrenece
        pref= GlobalSharedPreference.getInstance(this)

        //authentication
        auth=Firebase.auth
        var myRef=baseRef.child( auth.currentUser!!.uid )


        //alert
        var builder = AlertDialog.Builder(this@MainActivity)
        builder.setView(R.layout.prog_bar)
        builder.setCancelable(false)
        alertDialog = builder.create()

        //adapting the recyclerView

        contacts_recyclerView.layoutManager = LinearLayoutManager(this)
        if (vm.contactsList.size > 0)
        {
            contacts_recyclerView.adapter = vm.adapter
        }


        //Get permission
        checkPermission()

        //upload contacts to the array
        show.setOnClickListener {



            alertDialog!!.show()



            job = GlobalScope.launch {
                GetAllContacts()
            }
            GlobalScope.launch(Dispatchers.Main) {
                job.join()
                vm.contactsList.distinct()
                contacts_recyclerView.adapter = vm.adapter

            }


        }


        upload.setOnClickListener {
            if(vm.contactsList.size==0)
            {
                Toast.makeText(applicationContext, "there is no numbers", Toast.LENGTH_SHORT).show()
            }
            else {
                var brk = false
                val connectivityManager =
                    getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

                if (!isConnected) {
                    Toast.makeText(
                        applicationContext,
                        "the upload process requires internet connection",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    alertDialog?.show()
                    for (i in vm.contactsList) {
                        if (i.selected)
                            myRef.child(i.contactNumber).setValue(i.contactname).addOnCompleteListener{
                                if(it.isSuccessful)
                                {
                                    vm.contactsList.remove(i)
                                    contacts_recyclerView.adapter = vm.adapter

                                }
                                else
                                {
                                    Toast.makeText(
                                        applicationContext,
                                        "the contact ${i.contactname} and the contacts after it has 'not' be uploaded",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    alertDialog?.dismiss()
                                    brk = true
                                }

                            }
                        if (brk) {

                            break
                        }

                    }
                    if (!brk) {
                        alertDialog?.dismiss()
                        Toast.makeText(
                            applicationContext,
                            "only selected Numbers Uploaded",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                    brk = false

                }
            }
        }
        write.setOnClickListener{
            var alertuilder=AlertDialog.Builder(this)
            alertuilder.setTitle("Write Permission").setMessage("Are you sure that you want to save the contacts in you phone?").setNegativeButton("No"){dialog,which->
                dialog.dismiss()

            }.setPositiveButton("Yes"){dialog,which->
                WriteContacts(vm.contactsList)
                dialog.dismiss()

            }
            var dialog=alertuilder.create()
            dialog.show()
        }
        //download contacts from firebase
        download.setOnClickListener{
            alertDialog!!.show()
            vm.contactsList.clear()
            var j:Job?=null
            myRef.addValueEventListener(object: ValueEventListener {


                override fun onDataChange(snapshot: DataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    val temp=ArrayList<contacts>()
                     j=GlobalScope.launch(Dispatchers.IO) {
                    for ( i in snapshot.children) {

                        temp.add(contacts(i.value.toString(), i.key.toString()))
                        }
                    }
                    runBlocking {
                        j?.join()
                        val t=temp- vm.contactsList.toSet()
                        vm.contactsList+=t
                        vm.adapter= ContactsAdapter(vm.contactsList)
                        alertDialog!!.dismiss()
                        contacts_recyclerView.adapter = vm.adapter
                }
            }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext, "something goes wrong", Toast.LENGTH_SHORT).show()

                }
            })
            alertDialog!!.dismiss()

        }

    }


    //Reading Contacts
    private fun GetAllContacts() {

        var list = ArrayList<contacts>()
        val contentResolver = contentResolver
        val cursor: Cursor? = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        if (cursor != null) {
            if (cursor.count > 0) {
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
                                if (phoneNumber[0] != '+' && phoneNumber[0] != '2') {
                                    var s = "+2" + phoneNumber
                                    phoneNumber = s
                                }

                                list.add(contacts(name, phoneNumber))
                                phoneCursor.close()
                            }
                        }
                    }
                }
            }
        }
        vm.contactsList = list
        vm.adapter = ContactsAdapter(vm.contactsList)

        alertDialog?.dismiss()
    }

    private fun WriteContacts(contacts: ArrayList<contacts>) {



        val cr = contentResolver

        for (contact in contacts) {
            val ops = ArrayList<ContentProviderOperation>()
            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build())

            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contact.contactname)
                .build())

            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.contactNumber)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                .build())

            try {
                cr.applyBatch(ContactsContract.AUTHORITY, ops)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    //Menu inflation
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        var item = menu.getItem(1)
        var s = SpannableString(getString(R.string.selectall))
        s.setSpan(ForegroundColorSpan(Color.WHITE), 0, s.length, 0)
        item.title = s


        return true
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
                    val inputMethodManager =
                        getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
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

                        vm.contactsList.remove(x)
                        vm.contactsList.add(0,x)

                        vm.adapter.notifyDataSetChange()
                        contacts_recyclerView.adapter = vm.adapter




                        dialog.dismiss()
                    }

                    //cancel adding
                    view.cancel_adding.setOnClickListener {
                        dialog.dismiss()
                    }


                }

                view.cancel_adding.setOnClickListener {
                    dialog.dismiss()
                }

            }

            R.id.selectall -> {
                if (item.title.toString() == getString(R.string.selectall)) {
                    vm.contactsList.forEach {
                        it.selected = true

                    }
                    var s = SpannableString(getString(R.string.unselectall))
                    s.setSpan(ForegroundColorSpan(Color.WHITE), 0, s.length, 0)
                    item.title = s
                } else if (item.title.toString() == getString(R.string.unselectall)) {
                    vm.contactsList.forEach {
                        it.selected = false

                    }
                    var s = SpannableString(getString(R.string.selectall))
                    s.setSpan(ForegroundColorSpan(Color.WHITE), 0, s.length, 0)
                    item.title = s
                }

                vm.adapter = ContactsAdapter(vm.contactsList)
                contacts_recyclerView.adapter = vm.adapter


            }
            R.id.logout->
            {
                //var log=login()
                auth.signOut()
                pref.setValue("email","none")
                startActivity(Intent(this@MainActivity,Login::class.java))
                finish()

            }
            R.id.change_password->{
                var alertBuilder=AlertDialog.Builder(this@MainActivity)
                var view=layoutInflater.inflate(R.layout.password_reset_alert,null,false)
                alertBuilder.setView(view).create()
                var baseDialog=alertBuilder.show()
            view.confirm_change.setOnClickListener{


                    if(isValidPass(view.password_reset.text.toString()))
                    {
                        if(view.password_reset.text.toString()==view.matched_password_reset.text.toString())
                        {
                            var alert2= AlertDialog.Builder(this)
                            alert2.setTitle("Confirmation")
                            alert2.setMessage("Are you sure that you will change the password ?")
                            alert2.setCancelable(false)
                            alert2.setPositiveButton("confirm") { dialog, which ->

                                auth.currentUser?.updatePassword(view.password_reset.text.toString())
                                dialog.dismiss()
                                baseDialog.dismiss()

                            }
                            alert2.setNegativeButton("Cancel"){dialog, which ->
                                dialog.dismiss()

                            }
                            alert2.create().show()
                        }
                        else{
                            Toast.makeText(applicationContext, "Passwords are not matched", Toast.LENGTH_SHORT).show()

                        }
                    }
                    else if(view.password_reset.text.toString().length<8)
                        Toast.makeText(applicationContext, "Passwords should consist of more than 8 of both numbers and letters", Toast.LENGTH_LONG).show()

                    else
                        Toast.makeText(applicationContext, "Passwords should contain numbers and alphabet letters ", Toast.LENGTH_SHORT).show()




                }
                view.cancel_change.setOnClickListener{
                baseDialog.dismiss()
                }
            }

        }

        return super.onOptionsItemSelected(item)
    }

    fun isValidPass(password: String): Boolean {
        var num=IntArray(2)
        for (i in password)
        {
            if(i.isDigit())
            {
                num[0]+=1
            }
            else if(i.isLetter())
                num[1]+=1

            if(num[0]>0&&num[1]>0)
                return true

        }

        return false
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

    private fun checkPermission(): Boolean {
        var isContactRead =
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
        var isContactWrite =
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_CONTACTS)
        var connected =
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_NETWORK_STATE)
        var permissionList = ArrayList<String>()

        if (isContactRead != PackageManager.PERMISSION_GRANTED)
            permissionList.add(android.Manifest.permission.READ_CONTACTS)

        if (isContactWrite != PackageManager.PERMISSION_GRANTED)
            permissionList.add(android.Manifest.permission.WRITE_CONTACTS)

        if (connected != PackageManager.PERMISSION_GRANTED)
            permissionList.add(android.Manifest.permission.ACCESS_NETWORK_STATE)
        if (permissionList.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toTypedArray<String>(), 1)


        }

        return true

    }


}

