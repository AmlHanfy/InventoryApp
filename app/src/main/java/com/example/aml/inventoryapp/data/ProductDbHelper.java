package com.example.aml.inventoryapp.data;
import com.example.aml.inventoryapp.data.ProductContract.ProductEntry;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
public class ProductDbHelper extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    public static final String LOG_TAG = ProductDbHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "store.db";
    public ProductDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String SQL_CREATE_PRODUCTS_TABLE =  "CREATE TABLE " + ProductEntry.TABLE_NAME + " (" + ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " + ProductEntry.COLUMN_PRODUCT_PRICE + " TEXT NOT NULL, " + ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, " + ProductEntry.COLUMN_PRODUCT_SUPPLIER + " TEXT," + ProductEntry.COLUMN_PRODUCT_IMAGE +" TEXT NOT NULL );";db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
    }

}
