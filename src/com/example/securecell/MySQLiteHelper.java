package com.example.securecell;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper{
	
	// Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "SecureCellDB";
    
    public MySQLiteHelper(Context context){
    	super(context, DATABASE_NAME, null, DATABASE_VERSION);  
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create secureCellData table
        String CREATE_SECURE_CELL_DATA_TABLE = "CREATE TABLE secureCellData ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
                "type TEXT, "+
                "data TEXT )";
 
        // create books table
        db.execSQL(CREATE_SECURE_CELL_DATA_TABLE);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older secureCellData table if existed
        db.execSQL("DROP TABLE IF EXISTS secureCellData");
 
        // create fresh secureCellData table
        this.onCreate(db);
    }
    
    private static final String TABLE_SECURE_CELL_DATA = "secureCellData";
    
    private static final String KEY_ID = "id";
    private static final String KEY_TYPE = "type";
    private static final String KEY_DATA = "data";
    
    private static final String[] COLUMNS = {KEY_ID,KEY_TYPE,KEY_DATA};

    
    public void addEntry(String type, String data){
    	SQLiteDatabase sqlitedatabase = this.getWritableDatabase();
    	
    	ContentValues contentvalues = new ContentValues();
    	contentvalues.put(KEY_TYPE, type);
    	contentvalues.put(KEY_DATA, data);
    	
    	sqlitedatabase.insert(TABLE_SECURE_CELL_DATA, null, contentvalues);
    	
    	sqlitedatabase.close();    	
    }
    
    public String getEntry(String type){
    	
    	String result = null;
    	
    	SQLiteDatabase sqlitedatabase = this.getReadableDatabase();
    	
    	Cursor cursor = sqlitedatabase.query(TABLE_SECURE_CELL_DATA, COLUMNS, KEY_TYPE+" = ?", new String[]{type}, null, null, null, null);
    	
    	if(cursor.moveToFirst()){    		    	
        	Log.d("Data:",""+cursor.getString(2));  
        	result = cursor.getString(2);
        	
    	}    	
    	else{
    		result = "-1";
    	}
    	
    	return result;  

    }
    
    public void updateEntry(String type, String data){
    	SQLiteDatabase sqlitedatabase = this.getWritableDatabase();
    	
    	 ContentValues contentvalues = new ContentValues();
    	 contentvalues.put("type", type);
    	 contentvalues.put("data", data); 
    	 
    	sqlitedatabase.update(TABLE_SECURE_CELL_DATA, contentvalues, KEY_TYPE+" = ?", new String[]{type} );
    	 
    	 sqlitedatabase.close();
    }
    
    
}
