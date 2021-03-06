package com.example.jaikisan;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class Famers_Dashboard extends AppCompatActivity {
    Button add_itemButton, remove_itemButton;
    ImageButton refresh;
    RecyclerView recyclerView;
    CardView LogOutAlertBoxCard, remove_Item_cardView, add_Item_cardView;
    //Delete item onclick of listView
    ConstraintLayout confirm_delete_item;
    TextView confirm_delete_itemTextView;
    Button confirm_delete_itemButton,confirm_delete_itemCancelButton;

    //Kisan's Profile
    ConstraintLayout kisans_profileLayout;
    TextView kisan_nameTitle;
    Button kisanCallButton,goBackButton;
    TextView FetchedKisanPhoneNumber,FetchedKisanAddress,FetchedKisanState,FetchedKisanName;

    EditText itemNameEdit, itemQuantityEdit, itemPriceEdit;
    String itemToBeDeleted;
    ListView itemListCard;
    String[] itemNameArray;
    List<String> itemNameList = new ArrayList<>();
    List<KisanItems> kisanItemsList;
    DatabaseReference databaseReference;
    RecyclerAdapterKisan adapter;
    static int count =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_famers_dashboard);
        Objects.requireNonNull(getSupportActionBar()).hide();
        add_itemButton = findViewById(R.id.add_item_button);
        remove_itemButton = findViewById(R.id.remove_item_button);
        refresh = findViewById(R.id.refresh);

        LogOutAlertBoxCard = findViewById(R.id.LogOutAlertBoxCard);
        remove_Item_cardView = findViewById(R.id.remove_Item_cardView);
        add_Item_cardView = findViewById(R.id.add_Item_cardView);

        itemNameEdit = findViewById(R.id.itemNameEdit);
        itemQuantityEdit = findViewById(R.id.itemQuantityEdit);
        itemPriceEdit = findViewById(R.id.itemPriceEdit);
        itemListCard = findViewById(R.id.itemListCard);

        //Kisan's Profile View variables
        kisans_profileLayout= findViewById(R.id.KisanProfile);
        kisan_nameTitle= findViewById(R.id.kisan_nameTitle);
        kisanCallButton= findViewById(R.id.kisanCallButton);
        goBackButton= findViewById(R.id.goBackButton);
        FetchedKisanPhoneNumber= findViewById(R.id.FetchedKisanPhoneNumber);
        FetchedKisanAddress= findViewById(R.id.FetchedKisanAddress);
        FetchedKisanState= findViewById(R.id.FetchedKisanState);
        FetchedKisanName= findViewById(R.id.FetchedKisanName);

        //Item delete onlclick of listView
         confirm_delete_item = findViewById(R.id.confirm_delete_item);
         confirm_delete_itemTextView= findViewById(R.id.confirm_delete_itemTextView);
         confirm_delete_itemButton= findViewById(R.id.confirm_delete_itemButton);
         confirm_delete_itemCancelButton= findViewById(R.id.confirm_delete_itemCancelButton);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        kisanItemsList= new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerview);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Refresh_Recycler_View(refresh);
        adapter = new RecyclerAdapterKisan(kisanItemsList,this);
        recyclerView.setAdapter(adapter);
    }
    public void Refresh_Recycler_View(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // To retreive data and store items
        //kisanItemsList= new ArrayList<>();
        databaseReference.child("Kisan_Items").child("+919620533961"/*user.getPhoneNumber()*/)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            KisanItems items = snapshot.getValue(KisanItems.class);
                            kisanItemsList.add(items);
                            //Toast.makeText(getApplicationContext(),items.quantity , Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
        HashSet<KisanItems> kisanItemsHashSet = new HashSet<>(kisanItemsList);
        kisanItemsList = new ArrayList<>(kisanItemsHashSet);
        adapter = new RecyclerAdapterKisan(kisanItemsList,this);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                if(count<=2)
                    recyclerView.setAdapter(adapter);
                else
                    adapter.notifyDataSetChanged();
                count++;
            }
        });
    }

    public void add_Item_Function(View view) {
        add_Item_cardView.setVisibility(View.VISIBLE);
        remove_Item_cardView.setVisibility(View.GONE);
    }

    public void remove_item_function(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        add_Item_cardView.setVisibility(View.GONE);
        remove_Item_cardView.setVisibility(View.VISIBLE);
        getDataForRemoveItemList();
    }
    void getDataForRemoveItemList(){
        // To retreive data and store item name in itemNameList
        databaseReference.child("Kisan_Items").child("+919620533961")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            KisanItems items = snapshot.getValue(KisanItems.class);
                            itemNameList.add(Objects.requireNonNull(items).itemName);
                            //Toast.makeText(getApplicationContext(),items.itemName , Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
        itemNameArray = itemNameList.toArray(new String[0]);
        ArrayAdapter adapter = new ArrayAdapter<String>(
                getApplicationContext(),
                android.R.layout.simple_list_item_1,
                itemNameArray
        );
        itemListCard.setAdapter(adapter);
        itemNameList.clear();
        itemListCard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemToBeDeleted = (String) itemListCard.getItemAtPosition(position);
                confirm_delete_item.setVisibility(View.VISIBLE);
                confirm_delete_itemTextView.setText(String.format("Confirm delete item %s", itemToBeDeleted));

                //Toast.makeText(getApplicationContext(),itemToBeDeleted,Toast.LENGTH_LONG).show();
            }
        });
    }
    public void ConfirmDelete(View view) {
        DAOKisan dao = new DAOKisan();
        dao.deleteQuantity(itemToBeDeleted).addOnSuccessListener(suc->{
            Toast.makeText(getApplicationContext(),"Item Deleted",Toast.LENGTH_LONG).show();
        }).addOnFailureListener(er-> Toast.makeText(getApplicationContext(),"Error Occurred, Please try again",Toast.LENGTH_LONG).show());
        itemToBeDeleted = null;
        confirm_delete_item.setVisibility(View.GONE);
    }
    public void Cancel_delete_item_confirmation(View view) {
        confirm_delete_item.setVisibility(View.GONE);
    }


    // CardView Add Item
    public void cancel_item(View view) {
        remove_Item_cardView.setVisibility(View.GONE);
        add_Item_cardView.setVisibility(View.GONE);
    }

    public void clearItemCard(View view) {
        itemNameEdit.setText("");
        itemQuantityEdit.setText("");
        itemPriceEdit.setText("");
    }

    public void addItem_to_db(View view) {
        String itemName,quantity,price,userID;
        itemName = itemNameEdit.getText().toString();
        quantity = itemQuantityEdit.getText().toString();
        price = itemPriceEdit.getText().toString();
        if(itemName.isEmpty()||quantity.isEmpty()||price.isEmpty()){
            Toast.makeText(getApplicationContext(),"Enter valid details for all fields",Toast.LENGTH_LONG).show();
            return;
        }
        try{
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            //Remove HardCoded UserID
            userID = "+919620533961";/*userID*/ //Objects.requireNonNull(user).getPhoneNumber();
            DAOKisan dao = new DAOKisan();
            dao.addItems(itemName,quantity,price,userID).addOnSuccessListener(er-> Toast.makeText(getApplicationContext(),"Error Occurred, Please try again",Toast.LENGTH_LONG).show())
                    .addOnSuccessListener(sc-> Toast.makeText(getApplicationContext(),"Item Added",Toast.LENGTH_LONG).show());
            //Toast.makeText(getApplicationContext(),"Item Added",Toast.LENGTH_LONG).show();
            itemNameEdit.setText("");
            itemQuantityEdit.setText("");
            itemPriceEdit.setText("");
            add_Item_cardView.setVisibility(View.GONE);
            remove_Item_cardView.setVisibility(View.GONE);
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Some Error Occurred"+e, Toast.LENGTH_LONG).show();
        }
    }

    public void logout_common_function(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }

    public void cancelLogoutButton(View view) {
        LogOutAlertBoxCard.setVisibility(View.GONE);
    }

    int doubleBackToExitPressed = 1;
    @RequiresApi
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressed == 1) {
            add_Item_cardView.setVisibility(View.GONE);
            remove_Item_cardView.setVisibility(View.GONE);
        }
        if (doubleBackToExitPressed == 2) {
            LogOutAlertBoxCard.setVisibility(View.VISIBLE);
            //Toast.makeText(this, "Please press Back again to exit", Toast.LENGTH_SHORT).show();
        }
        if (doubleBackToExitPressed == 3) {
            finishAffinity();
            System.exit(0);
        }
        else {
            doubleBackToExitPressed++;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressed=1;
            }
        }, 2000);
    }



}