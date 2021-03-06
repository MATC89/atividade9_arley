package app.atividades.matc89.atividade9_arley;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by arleyprates on 4/17/16.
 * Exibirá cada item item da lista
 */
public class ContatoAdapter extends CursorAdapter {

    int[] indices;

    public ContatoAdapter(Context context, Cursor c) {

        super(context, c, 0);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        indices = new int[] {
                cursor.getColumnIndex(ContactsContract.Contacts._ID),
                cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY),
                cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
        };
        return LayoutInflater.from(context).inflate(R.layout.item_contato, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textNome = (TextView) view.findViewById(R.id.textNome);
        QuickContactBadge qcbBadge = (QuickContactBadge) view.findViewById(R.id.qcbFoto);

        Uri uriContato = ContactsContract.Contacts.getLookupUri(mCursor.getLong(indices[0]),
                mCursor.getString(indices[1]));

        textNome.setText(cursor.getString(indices[2]));
        qcbBadge.assignContactUri(uriContato);
        Picasso.with(mContext).load(uriContato).placeholder(R.mipmap.ic_launcher).into(qcbBadge);


    }
}
