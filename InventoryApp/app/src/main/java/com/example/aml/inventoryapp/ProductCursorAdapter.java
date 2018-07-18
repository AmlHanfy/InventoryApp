package com.example.aml.inventoryapp;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aml.inventoryapp.data.ProductContract.ProductEntry;

import java.net.URI;



public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.product_list_item, parent, false);
    }

    @Override
    public void bindView(final View view, final Context context, Cursor cursor) {

        TextView productName = (TextView) view.findViewById(R.id.product_name);
        TextView productSupplier = (TextView) view.findViewById(R.id.product_supplier);
        TextView productPrice = (TextView) view.findViewById(R.id.product_price);
        final TextView productQuantity = (TextView) view.findViewById(R.id.product_quantity);
        Button buyItButton = (Button) view.findViewById(R.id.buy_it);
        ImageView imageView = (ImageView) view.findViewById(R.id.product_photo);

        String name = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME));
        String supplier = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER));
        String price = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE));
        final String quantity = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));
        final int id = cursor.getInt(cursor.getColumnIndex(ProductEntry._ID));
        String imageString = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE));


        productName.setText(name);

        if (TextUtils.isEmpty(supplier)){
            productSupplier.setText("Unknown Supplier");
        }
        else {
            productSupplier.setText(supplier);
        }
        productPrice.setText(price + " L.E");
        productQuantity.setText(quantity + " Pieces");

        if (TextUtils.isDigitsOnly(imageString)) {
            imageView.setImageResource(Integer.parseInt(imageString));
        }
        else {
            imageView.setImageURI(Uri.parse(imageString));
        }

        buyItButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int quantityIntValue = Integer.parseInt(quantity);

                if (quantityIntValue > 0){

                    quantityIntValue--;
                    productQuantity.setText(String.valueOf(quantityIntValue) + "Pieces");
                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY,quantityIntValue);
                    int rowsUpdated = context.getContentResolver().update(
                            ContentUris.withAppendedId(ProductEntry.CONTENT_URI,id),values,null,null);
                    if (rowsUpdated != 0){
                        Toast.makeText(context,"Decreased by 1",Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(context,"Error",Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(context,"Can't be negative",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
