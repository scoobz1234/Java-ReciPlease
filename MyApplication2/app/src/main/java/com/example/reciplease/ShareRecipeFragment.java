package com.example.reciplease;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static android.app.Activity.RESULT_OK;

public class ShareRecipeFragment extends Fragment implements View.OnClickListener {

    private Context context;
    private BluetoothAdapter btAdapter;
    private CommServ commServ = null;

    private ListView lvRecipeToSend;
    public TextView tvConnectedDevice;
    private Button btnSendRecipe, btnGoBack;
    private ArrayAdapter<String> adapterRecipeToSend;

    private final int LOCATION_PERMISSION_REQUEST = 101;
    private final int SELECT_DEVICE = 102;

    public static final int MESSAGE_STATE_CHANGED = 0;
    public static final int MESSAGE_READ = 1;
    public static final int MESSAGE_WRITE = 2;
    public static final int MESSAGE_DEVICE_NAME = 3;
    public static final int MESSAGE_TOAST = 4;

    public static final String DEVICE_NAME = "deviceName";
    public static final String TOAST = "toast";
    private String connectedDevice;

    public ShareRecipeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_share_recipe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = this.getContext();
        init(view);
        initBluetooth();
        commServ = new CommServ(context, handler);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_go_back:
                MainActivity.fragMan.popBackStack();
                break;
        }
    }

    /**HANDLER*/
    private Handler handler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message message){
            switch (message.what){
                case MESSAGE_READ:
                    byte[] inBuffer = (byte[]) message.obj;
                    Object inBoundObject = DeserializeObject(inBuffer);

                    if (inBoundObject.getClass() == Recipe.class){
                        Recipe oToR = (Recipe)inBoundObject;
                        AddToDatabase(oToR);
                        Log.i("Recipe", "Inbound Recipe Added Successfully!");
                    }
                    Toast.makeText(context,"You got a new Recipe!",Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_STATE_CHANGED:

                    switch(CommServ.getState()){
                        case CommServ.STATE_CONNECTED:
                        case CommServ.STATE_CONNECTING:
                            tvConnectedDevice.setText("Connected to: " + connectedDevice);
                            break;
                        case CommServ.STATE_LISTEN:
                        case CommServ.STATE_NONE:
                            tvConnectedDevice.setText("Ready");
                            break;
                    }
                    break;
                case MESSAGE_DEVICE_NAME:
                    connectedDevice = message.getData().getString(DEVICE_NAME);
                    Toast.makeText(context, connectedDevice, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(context, message.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });

    private void init(View v){
        lvRecipeToSend = v.findViewById(R.id.list_recipe_to_send);
        ListView lvIngredientsToSend = v.findViewById(R.id.lv_ingredients_to_send);
        tvConnectedDevice = v.findViewById(R.id.tv_connected_Device);
        btnSendRecipe = v.findViewById(R.id.btn_send_recipe);
        btnGoBack = v.findViewById(R.id.btn_go_back);
        btnGoBack.setOnClickListener(this);

        if (MainActivity.selectedRecipe != null) {
            adapterRecipeToSend = new ArrayAdapter<>(context, R.layout.meals_list_item);
            adapterRecipeToSend.add(MainActivity.selectedRecipe.toString());

            ArrayAdapter<String> adapterIngredientToSend = new ArrayAdapter<>(context, R.layout.ingredient_list_item);
            for (Ingredient i : MainActivity.selectedRecipe.getIngredients()) {
                adapterIngredientToSend.add(i.toString());
            }

            lvRecipeToSend.setAdapter(adapterRecipeToSend);
            lvIngredientsToSend.setAdapter(adapterIngredientToSend);
        }
        
        btnSendRecipe.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // Create byte arrays of data being sent...
                byte[] recipeToSend = PrepareRecipe();

                if (recipeToSend != null) {
                    commServ.write(recipeToSend);
                    Log.i("Recipe Byte[]", recipeToSend.toString());

                    //Recipe being sent data...
                    Object o = DeserializeObject(recipeToSend);
                    Recipe oToR = (Recipe)o;
                    Log.i("Recipe Sent",oToR.toOtherString());
                    for(Ingredient i : oToR.getIngredients()){
                        Log.i("IngredientSent",i.toString());
                    }
                    Toast.makeText(context,"Successfully sent Recipe!",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(context,"No Recipe selected.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        setHasOptionsMenu(true);
    }

    /**Gets the recipe ready for serialization**/
    private byte[] PrepareRecipe(){
        if(MainActivity.selectedRecipe != null){
            Recipe r = MainActivity.selectedRecipe;
            return SerializeObject(r);
        }
        return null;
    }

    /**Serializes object**/
    private byte[] SerializeObject(Object o){
        byte[] data = null;
        try{
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(o);
            oos.flush();
            data = bos.toByteArray();
        } catch (IOException e){
            Log.e("SerializeObject: ", "Failed to Serialize object" );
        }
        return data;
    }

    /**De-Serializes objects**/
    private static Object DeserializeObject(byte[] data){

        try {
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ObjectInputStream is = new ObjectInputStream(in);
            return is.readObject();
        } catch (IOException e){
            Log.e("Could not Deserialize ", e.toString());
        } catch (ClassNotFoundException ce){
            Log.e("Class not found ", ce.toString());
        }
        return null;
    }

    /**Adds object to the database, depending on input**/
    private static void AddToDatabase(Object input){
        if (input.getClass() == Recipe.class){
            MainActivity.db.dao().addRecipe((Recipe)input);
        }
    }

    //////////////////////////////////////OPTIONS MENU//////////////////////////////////////////////
    //region Options Menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu_main_activity, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        switch(item.getItemId()){
            case R.id.menu_toggle_bluetooth:
                EnableBluetooth();
                return true;
            case R.id.menu_search_devices:
                CheckPermissions();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //endregion
    ////////////////////////////////////////BLUETOOTH///////////////////////////////////////////////
    //region Bluetooth
    /**Initialize the bluetooth device (check to see if device has bluetooth capabilities)**/
    private void initBluetooth(){
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter == null){
            Toast.makeText(context, "No bluetooth found!", Toast.LENGTH_SHORT).show();
        }
    }

    /**This function will enable the bluetooth if its not already enabled**/
    private void EnableBluetooth(){
        if (!btAdapter.isEnabled()){
            btAdapter.enable();
        }

        if (btAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
            Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoveryIntent);
        }
    }

    /**This function will check for permissions**/
    private void CheckPermissions(){
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
        } else {
            Intent intent = new Intent(context, DeviceListActivity.class);
            startActivityForResult(intent, SELECT_DEVICE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == SELECT_DEVICE && resultCode == RESULT_OK){
            String address = data.getStringExtra("deviceAddress");
            commServ.connect(btAdapter.getRemoteDevice(address));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == LOCATION_PERMISSION_REQUEST){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(context, DeviceListActivity.class);
                startActivityForResult(intent, SELECT_DEVICE);
            } else {
                new AlertDialog.Builder(context)
                        .setCancelable(false)
                        .setMessage("Location permission is required.\n Please allow")
                        .setPositiveButton("Allow", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i){
                                CheckPermissions();
                            }
                        })
                        .setNegativeButton("Deny", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i){
                                getActivity().finish();
                            }
                        }).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    //endregion

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (commServ != null){
            commServ.stop();
        }
    }
}
