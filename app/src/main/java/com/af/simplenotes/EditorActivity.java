package com.af.simplenotes;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.af.plainnotes.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditorActivity extends AppCompatActivity {

    private String mAction;
    private String mNoteFilter;
    private String mOldText;

    @BindView(R.id.editText) EditText mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        ButterKnife.bind(this);

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(NotesProvider.CONTENT_ITEM_TYPE);

        if(uri == null) {
            mAction = Intent.ACTION_INSERT;
            setTitle(R.string.new_note);
        } else {
            mAction = Intent.ACTION_EDIT;
            setTitle(R.string.edit_note);
            mNoteFilter = DBOpenHelper.COLUMN_NOTE_ID + "=" + uri.getLastPathSegment();

            Cursor cursor = getContentResolver().query(uri, DBOpenHelper.ALL_COLUMNS, mNoteFilter,
                    null, null);
            assert cursor != null;
            cursor.moveToFirst();
            mOldText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COLUMN_NOTE_TEXT));
            mEditor.setText(mOldText);
            mEditor.requestFocus();
            cursor.close();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finishEditing();
                break;
            case R.id.action_delete:
                deleteNote();
                break;
        }

        return true;
    }

    private void deleteNote() {
        getContentResolver().delete(NotesProvider.CONTENT_URI, mNoteFilter, null);
        Toast.makeText(this, R.string.note_deleted, Toast.LENGTH_LONG).show();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(mAction.equals(Intent.ACTION_EDIT)) {
            getMenuInflater().inflate(R.menu.menu_editor, menu);
        }
        return true;
    }

    private void finishEditing() {
        String newText = mEditor.getText().toString().trim();
        switch (mAction) {
            case Intent.ACTION_INSERT:
                if(newText.length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    insertNote(newText);
                }
                break;
            case Intent.ACTION_EDIT:
                if(newText.length() == 0) {
                    deleteNote();
                } else if(mOldText.equals(newText)) {
                    setResult(RESULT_CANCELED);
                } else {
                    updateNote(newText);
                }
        }
        finish();
    }

    private void updateNote(String noteText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COLUMN_NOTE_TEXT, noteText);
        getContentResolver().update(NotesProvider.CONTENT_URI, values, mNoteFilter, null);
        Toast.makeText(this, R.string.note_updated, Toast.LENGTH_LONG).show();
        setResult(RESULT_OK);
    }

    private void insertNote(String noteText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COLUMN_NOTE_TEXT, noteText);
        getContentResolver().insert(NotesProvider.CONTENT_URI, values);
        setResult(RESULT_OK);
    }

    @Override
    public void onBackPressed() {
        finishEditing();
    }
}
