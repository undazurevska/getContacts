package lv.unda.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;

import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;
import android.Manifest;
import android.app.AlertDialog;

import android.os.Bundle;
import android.provider.ContactsContract;
import java.util.ArrayList;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.View;


public class MainActivity extends AppCompatActivity {
    private ArrayList<String> selectedContacts = new ArrayList<>();
    private ListView contactListView;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactListView = findViewById(R.id.contactListView);
        adapter = new ArrayAdapter<>(
                this,
                R.layout.contact_list_item, // Layout for each item
                R.id.contactName,            // ID of the TextView for the name
                selectedContacts
        );
        contactListView.setAdapter(adapter);
    }

    public void btnGetContactPressed (View v) {
        getPhoneContacts();
    }

    private void getPhoneContacts() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 0);
        } else {
            ContentResolver contentResolver = getContentResolver();
            Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            Cursor cursor = contentResolver.query(uri, null, null, null, null);

            if (cursor.getCount() > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Select Contacts");

                final ArrayList<String> contactNames = new ArrayList<>();
                final ArrayList<String> contactNumbers = new ArrayList();
                final boolean[] checkedContacts = new boolean[cursor.getCount()];

                while (cursor.moveToNext()) {
                    int nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY);
                    int numberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                    if (nameColumnIndex >= 0 && numberColumnIndex >= 0) {
                        String contactName = cursor.getString(nameColumnIndex);
                        String contactNumber = cursor.getString(numberColumnIndex);

                        contactNames.add(contactName);
                        contactNumbers.add(contactNumber);
                    }
                }

                builder.setMultiChoiceItems(
                        contactNames.toArray(new String[0]),
                        checkedContacts,
                        (dialog, which, isChecked) -> {
                            if (isChecked) {
                                selectedContacts.add(contactNames.get(which) + ": " + contactNumbers.get(which));
                            } else {
                                selectedContacts.remove(contactNames.get(which) + ": " + contactNumbers.get(which));
                            }
                        }
                );

                builder.setPositiveButton("OK", (dialog, which) -> {
                    adapter.notifyDataSetChanged();
                });

                builder.setNegativeButton("Cancel", null);

                builder.show();
            } else {
                Toast.makeText(this, "No contacts found on the phone.", Toast.LENGTH_SHORT).show();
            }


        }
    }


}