package com.example.aml.inventoryapp;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.MotionEvent;
import android.view.View;
import android.support.v4.app.NavUtils;
import com.example.aml.inventoryapp.data.ProductContract.ProductEntry;
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>
{
    private EditText mNameEditText;
    private Button increaseButton;
    private Button decreaseButton;
    String name;
    String price;
    String supplier;
    private EditText mSupplierEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private Button orderIt;
    private Button delete;
    private ImageView mProductImageView;
    private boolean mPetHasChanged = false;
    Uri fullPhotoUri = null;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent)
        {
            mPetHasChanged = true;
            return false;
        }
    };
    Uri productUri;
    private static final int PRODUCT_LOADER = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        mNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mSupplierEditText = (EditText) findViewById(R.id.edit_product_supplier);
        mProductImageView = (ImageView) findViewById(R.id.edit_product_photo);
        increaseButton = (Button) findViewById(R.id.increase);
        decreaseButton = (Button) findViewById(R.id.decrease);
        orderIt = (Button) findViewById(R.id.order_it);
        mPriceEditText = (EditText) findViewById(R.id.edit_product_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_product_quantity);
        delete = (Button) findViewById(R.id.delete);
        delete.setVisibility(View.GONE);
        orderIt.setVisibility(View.GONE);
        delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showDeleteConfirmationDialog();
            }
        });
        orderIt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"amlhanfy05@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, name + "order");
                intent.putExtra(Intent.EXTRA_TEXT,"Product Order\n" + "Name : " + name + "\n" + "Price : " + price + "L.E" + "\n" + "Supplier : " + supplier + "\n" + "Your name : \n" + "Your Phone : \n");
                if (intent.resolveActivity(getPackageManager()) != null)
                {
                    startActivity(intent);
                }
            }
        });
        mProductImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                selectImage();
            }
        });
        increaseButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String quantity = mQuantityEditText.getText().toString().trim();
                if (TextUtils.isEmpty(quantity))
                {
                    Toast.makeText(EditorActivity.this,"Enter Quantity",Toast.LENGTH_LONG).show();
                }
                else
                {
                    int quantityIntValue = Integer.parseInt(quantity);
                    quantityIntValue++;
                    mQuantityEditText.setText(String.valueOf(quantityIntValue));
                }
            }
        });
        decreaseButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String quantity = mQuantityEditText.getText().toString().trim();
                if (TextUtils.isEmpty(quantity)){
                    Toast.makeText(EditorActivity.this,"Enter Quantity ",Toast.LENGTH_LONG).show();
                }
                else
                {
                    int quantityIntValue = Integer.parseInt(quantity);
                    if (quantityIntValue > 0)
                    {
                        quantityIntValue--;
                        mQuantityEditText.setText(String.valueOf(quantityIntValue));
                    }
                    else
                    {
                        Toast.makeText(EditorActivity.this,"Can't Negative",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mProductImageView.setOnTouchListener(mTouchListener);
        mNameEditText.setOnTouchListener(mTouchListener);
        Intent intent = getIntent();
        productUri = intent.getData();
        if (productUri != null)
        {
            setTitle("Edit Product");
            delete.setVisibility(View.VISIBLE);
            orderIt.setVisibility(View.VISIBLE);
            getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
        }
        else
        {
            setTitle("Add Pet");
            invalidateOptionsMenu();
        }
    }
    private void saveProduct()
    {
        String supplierString = mSupplierEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String nameString = mNameEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        if (productUri == null && fullPhotoUri == null && TextUtils.isEmpty(nameString) && TextUtils.isEmpty(supplierString) && TextUtils.isEmpty(priceString) && TextUtils.isEmpty(quantityString))
        {
            return;
        }
        if (TextUtils.isEmpty(nameString)|| TextUtils.isEmpty(priceString) || TextUtils.isEmpty(quantityString) || fullPhotoUri == null)
        {
            throw new IllegalArgumentException();
        }
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER, supplierString);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, priceString);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantityString);
        values.put(ProductEntry.COLUMN_PRODUCT_IMAGE, fullPhotoUri.toString());
        if (productUri == null)
        {
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
            if (newUri == null)
            {
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            int numberOfRowsUpdated = getContentResolver().update(productUri, values, null, null);
            if (numberOfRowsUpdated != 0)
            {
                Toast.makeText(this, "updated", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(this, "error", Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_save:
                try
                {
                    saveProduct();
                    finish();
                }
                catch (Exception e)
                {
                    Toast.makeText(EditorActivity.this,"Fill all ",Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mPetHasChanged)
                {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                productUri,
                projection,
                null,
                null,
                null
        );
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        if (data.moveToNext())
        {
            name = data.getString(data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME));
            supplier = data.getString(data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER));
            price = data.getString(data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE));
            String quantity = data.getString(data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));
            String imageString = data.getString(data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE));
            fullPhotoUri = Uri.parse(imageString);
            if (TextUtils.isDigitsOnly(imageString))
            {
                mProductImageView.setImageResource(Integer.parseInt(imageString));
            }
            else
            {
                mProductImageView.setImageURI(fullPhotoUri);
            }
            mNameEditText.setText(name);
            mSupplierEditText.setText(supplier);
            mPriceEditText.setText(String.valueOf(price));
            mQuantityEditText.setText(String.valueOf(quantity));
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {

    }
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("close ?");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                if (dialog != null)
                {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    @Override
    public void onBackPressed()
    {
        if (!mPetHasChanged)
        {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }
    private void showDeleteConfirmationDialog()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("delete ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                deleteProduct();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                if (dialog != null)
                {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        if (productUri == null)
        {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }
    private void deleteProduct()
    {
        if (productUri != null)
        {
            int rowsDeleted = getContentResolver().delete(productUri, null, null);
            if (rowsDeleted == 0)
            {
                Toast.makeText(this, "error",
                        Toast.LENGTH_SHORT).show();
            }
            else
            {

                Toast.makeText(this, "Deleted",
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK)
        {
            fullPhotoUri = data.getData();
            mProductImageView.setImageURI(fullPhotoUri);
        }
    }
    static final int REQUEST_IMAGE_GET = 1;
    public void selectImage()
    {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_IMAGE_GET);
    }
}
