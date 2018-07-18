package com.example.aml.inventoryapp.data;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.util.Log;
import static com.example.aml.inventoryapp.data.ProductDbHelper.LOG_TAG;
import com.example.aml.inventoryapp.data.ProductContract.ProductEntry;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
public class ProductProvider extends ContentProvider
{
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final int PRODUCTS = 100;
    public static final int PRODUCTS_ID = 101;
    static
    {
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#", PRODUCTS_ID);
    }
    private ProductDbHelper mDbHelper;
    @Override
    public boolean onCreate()
    {
        mDbHelper = new ProductDbHelper(getContext());
        return true;
    }
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder)
    {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor = null;
        int match = sUriMatcher.match(uri);
        switch (match)
        {
            case PRODUCTS:
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCTS_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values)
    {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported " + uri);
        }
    }
    @Nullable
    @Override
    public String getType(@NonNull Uri uri)
    {
        final int match = sUriMatcher.match(uri);
        switch (match)
        {
            case PRODUCTS:
                return ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCTS_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
    private Uri insertProduct(Uri uri, ContentValues values)
    {

        String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
        if (name == null)
        {
            throw new IllegalArgumentException("Product requires a name");
        }
        String image = values.getAsString(ProductEntry.COLUMN_PRODUCT_IMAGE);
        if (image == null)
        {
            throw new IllegalArgumentException("Product requires a image");
        }
        Integer price = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_PRICE);
        if (price == null)
        {
            throw new IllegalArgumentException("Product requires price");
        }
        if (price < 0)
        {
            throw new IllegalArgumentException("Product requires valid price");
        }
        Integer quantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity != null && quantity < 0)
        {
            throw new IllegalArgumentException("Product requires valid quantity");
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(ProductEntry.TABLE_NAME, null, values);
        if (id == -1)
        {
            Log.e(LOG_TAG, "Failed to" + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs)
    {
        int rowsDeleted;
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match)
        {
            case PRODUCTS:
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted != 0)
                {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            case PRODUCTS_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted != 0)
                {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Deletion is not supported " + uri);
        }
    }
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs)
    {
        final int match = sUriMatcher.match(uri);
        switch (match)
        {
            case PRODUCTS:
                return updateProduct(uri, values, selection, selectionArgs);
            case PRODUCTS_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported " + uri);
        }
    }
    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {

        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_NAME))
        {
            String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
            if (name == null)
            {
                throw new IllegalArgumentException("Product requires a name");
            }
        }
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_IMAGE))
        {
            String image = values.getAsString(ProductEntry.COLUMN_PRODUCT_IMAGE);
            if (image == null)
            {
                throw new IllegalArgumentException("Product requires a image");
            }
        }
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_PRICE))
        {
            Integer price = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_PRICE);
            if (price == null)
            {
                throw new IllegalArgumentException("Product requires price");
            }
            if (price < 0)
            {
                throw new IllegalArgumentException("Product requires valid price");
            }
        }
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_QUANTITY))
        {
            Integer quantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity != null && quantity < 0)
            {
                throw new IllegalArgumentException("Product requires valid quantity");
            }
        }
        if (values.size() == 0)
        {
            return 0;
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0)
        {
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsUpdated;
    }
}
