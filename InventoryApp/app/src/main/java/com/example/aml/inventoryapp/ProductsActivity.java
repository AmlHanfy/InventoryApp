package com.example.aml.inventoryapp;

import android.app.LoaderManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.app.AlertDialog;
import com.example.aml.inventoryapp.data.ProductContract.ProductEntry;
public class ProductsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>
{
    ListView listView;
    private static final int PRODUCT_LOADER = 0;
    ProductCursorAdapter productCursorAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(ProductsActivity.this,EditorActivity.class);
                startActivity(intent);
            }
        });
        listView = (ListView) findViewById(R.id.products_list);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);
        productCursorAdapter = new ProductCursorAdapter(this,null);
        listView.setAdapter(productCursorAdapter);
        listView.setOnItemClickListener( new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(ProductsActivity.this,EditorActivity.class);
                intent.setData(ContentUris.withAppendedId(ProductEntry.CONTENT_URI,id));
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(PRODUCT_LOADER,null,this);
    }
    private void insertProduct()
    {
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "ASUS");
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER, "Samsung");
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, 12000);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, 50);
        values.put(ProductEntry.COLUMN_PRODUCT_IMAGE,String.valueOf(R.drawable.no_image));
        getContentResolver().insert(ProductEntry.CONTENT_URI,values);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId()) {

            case R.id.action_insert_data:
                insertProduct();
                return true;

            case R.id.action_delete_all_entries:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_products, menu);
        return true;
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        String[] projection =
                {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_IMAGE
        };
        return new CursorLoader(
                this,
                ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        productCursorAdapter.swapCursor(data);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        productCursorAdapter.swapCursor(null);
    }
    private void showDeleteConfirmationDialog()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("delete all data ?");
        builder.setPositiveButton("delete", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                deletePets();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void deletePets()
    {
        int rowsDeleted = getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);
        if (rowsDeleted == 0)
        {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
        }
    }
}
