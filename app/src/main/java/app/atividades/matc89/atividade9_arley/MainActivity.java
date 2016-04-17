package app.atividades.matc89.atividade9_arley;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contatos);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            Log.d("PLAYGROUND-2", "Permission is not granted, requesting");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 123);
            //btnSendSms.setEnabled(false);
        } else {
            Log.d("PLAYGROUND-3", "Permission is granted");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("PLAYGROUND!!", "Permission has been granted");
                // textView.setText("SMS enviada!");
                //btnSendSms.setEnabled(true);
            } else {
                Log.d("PLAYGROUND@@", "Permission has been denied or request cancelled");
                // textView.setText("SMS não enviada!");
                //btnSendSms.setEnabled(false);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_inserir_api) {
            inserirContato("Fulano", "88990000", "Rua do Bolo",
                    "/mnt/sdcard/DCIM/.thumbnails/1414074222630.jpg");
        } else if (id == R.id.action_inserir_app) {
            inserirContatoApp("Fulano", "88990000", "fulano@email.com", "Rua do Bolo");

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void inserirContato(String nome, String telefone, String endereco, String foto){
        // Lista de operações que serão realizadas em batch
        ArrayList<ContentProviderOperation> operation =
                new ArrayList<ContentProviderOperation>();

        // Armazenará o id interno do contato
        // e servirá para inserir os detalhes
        int backRefIndex = 0;

        // Associa o contato à conta-padrão do telefone
        operation.add(
                ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI).
                        withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null).
                        withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());

        // Adiciona o nome do contato e alimenta id
        operation.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).
                        withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backRefIndex).
                        withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE).
                        withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                nome).build());

        // Adiciona um endereço ao contato a partir do id
        operation.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).
                        withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backRefIndex).
                        withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE).
                        withValue(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS,
                                endereco).build());

        // Associa um telefone ao contato do tipo "Home"
        operation.add(
                ContentProviderOperation.newInsert(
                        ContactsContract.Data.CONTENT_URI).
                        withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                                backRefIndex).
                        withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE).
                        withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
                                telefone).
                        withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                ContactsContract.CommonDataKinds.Phone.TYPE_HOME).
                        build());

        // Adiciona imagem ao contato
        Bitmap fotoBitmap = BitmapFactory.decodeFile(foto);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        fotoBitmap.compress(Bitmap.CompressFormat.PNG , 75, stream);

        operation.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, backRefIndex)
                        .withValue(ContactsContract.Data.IS_SUPER_PRIMARY, 1)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, stream.toByteArray())
                        .build());

        // Aplica o batch de inclusão
        try {
            getContentResolver().applyBatch(
                    ContactsContract.AUTHORITY, operation);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    private void inserirContatoApp(String nome, String telefone, String email, String endereco){
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        intent
                .putExtra(ContactsContract.Intents.Insert.NAME, nome)
                .putExtra(ContactsContract.Intents.Insert.EMAIL, email)
                .putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE,
                        ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                .putExtra(ContactsContract.Intents.Insert.PHONE, telefone)
                .putExtra(ContactsContract.Intents.Insert.PHONE_TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                .putExtra(ContactsContract.Intents.Insert.POSTAL, endereco);
        startActivity(intent);
    }
}
