package hk.edu.polyu.eie3109.phonebook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.Manifest;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ListView myPhoneList;

    SimpleCursorAdapter myCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myPhoneList = findViewById(R.id.LVPhoneList);
        showContacts();

        myPhoneList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor c = myCursorAdapter.getCursor();
                c.moveToPosition(i);
                int nameIndex = c.getColumnIndex(ContactsContract.Contacts._ID);
                String id = c.getString(nameIndex);
                Cursor phoneCursor = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{id},
                        null
                );

                if (phoneCursor != null) {
                    StringBuilder phoneNumber = new StringBuilder();
                    int phoneCount = phoneCursor.getCount();
                    while (phoneCursor.moveToNext()) {
                        int phoneNumberIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        phoneNumber.append(phoneCursor.getString(phoneNumberIndex));
                        phoneCount--;
                        if (phoneCount > 0) {
                            phoneNumber.append("\n");
                        }

                    }
                    Toast.makeText(MainActivity.this, phoneNumber.toString(), Toast.LENGTH_SHORT).show();
                }

                if (phoneCursor != null) {
                    phoneCursor.close();
                }
            }
        });
    }

    private void showContacts() {
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 100);
        } else {
            final ContentResolver cr = getContentResolver();

            Cursor c = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    new String[]{ContactsContract.Contacts._ID,
                            ContactsContract.Contacts.DISPLAY_NAME},
                    null, null, null);

            myCursorAdapter = new SimpleCursorAdapter(this, R.layout.list_item, c,
                    new String[]{ContactsContract.Contacts.DISPLAY_NAME}, new int[]{R.id.TVRow},
                    0);

            myPhoneList.setAdapter(myCursorAdapter);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showContacts();
            } else {
                Toast.makeText(this, "Until you grant the permission, we cannot display the names",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }




}