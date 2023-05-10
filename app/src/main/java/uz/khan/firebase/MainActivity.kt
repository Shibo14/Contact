package uz.khan.firebase

import android.annotation.SuppressLint

import android.content.pm.PackageManager
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import uz.khan.firebase.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val READ_CONTACTS_PERMISSION_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        binding.button.setOnClickListener {
            //  savePhoneNumber(id)

            readContacts()
        }


        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Ruhsat berilmaganligini tekshirish
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.READ_CONTACTS
                )
            ) {
                // Ruhsat talab qilish uchun izoh berish
                // Masalan, dialog oynasini ko'rsatish
            } else {
                // Ruhsatni so'rov qilish
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_CONTACTS),
                    READ_CONTACTS_PERMISSION_REQUEST
                )
            }
        } else {
            // Ruhsat berilganligini tekshirish
            // Kontaktlarni o'qish jarayonini davom ettirish
            readContacts()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == READ_CONTACTS_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Ruhsat berildi
                // Kontaktlarni o'qish jarayonini davom ettirish
                readContacts()
            } else {
                // Ruhsat berilmadi
                // Foydalanuvchiga izoh berish yoki alternativ variantlarni ko'rsatish mumkin
            }
        }
    }


    private fun savePhoneNumber(phoneNumber: String) {
        binding.progressBar.visibility = View.VISIBLE
        val database = FirebaseDatabase.getInstance()
        val phoneNumberRef = database.getReference("phoneNumbers")
        phoneNumberRef.push().setValue(phoneNumber)
            .addOnSuccessListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Saqlash muvaffaqiyatli yakunlandi", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Saqlashda xatolik yuz berdi", Toast.LENGTH_SHORT).show()
            }
    }




        @SuppressLint("Range")
        private fun readContacts() {
            binding.progressBar.visibility = View.VISIBLE
            val contactsList = mutableListOf<ContactData>()

            val cursor: Cursor? = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                null
            )

            cursor?.let { c ->
                if (c.moveToFirst()) {
                    do {
                        val contactId =
                            c.getString(c.getColumnIndex(ContactsContract.Contacts._ID))
                        val displayName =
                            c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

                        val phoneCursor: Cursor? = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            arrayOf(contactId),
                            null
                        )

                        val phoneNumbers = mutableListOf<String>()

                        phoneCursor?.let { pc ->
                            if (pc.moveToFirst()) {
                                do {
                                    val phoneNumber = pc.getString(
                                        pc.getColumnIndex(
                                            ContactsContract.CommonDataKinds.Phone.NUMBER
                                        )
                                    )
                                    phoneNumbers.add(phoneNumber)
                                } while (pc.moveToNext())
                            }
                            pc.close()
                        }

                        val contact = ContactData(displayName, phoneNumbers)
                        contactsList.add(contact)

                    } while (c.moveToNext())
                }
                c.close()
            }

            // Kontaktlarni Firebase'ga yuklash
            val database = FirebaseDatabase.getInstance()
            val contactsRef = database.getReference("contacts")
            contactsRef.setValue(contactsList)
                .addOnSuccessListener {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Saqlash muvaffaqiyatli yakunlandi", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    // Saqlashda xatolik yuz berdi
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, " Saqlashda xatolik yuz berdi", Toast.LENGTH_SHORT).show()
                }
        }

    }
