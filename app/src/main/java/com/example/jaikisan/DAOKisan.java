package com.example.jaikisan;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DAOKisan {
    private DatabaseReference databaseReference;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    public Task<Void> add(String name, String city,String state,String address, String phone){
        databaseReference = db.getReference("KisanUserDetails");
        KisanUserDetails user = new KisanUserDetails(name,city,state,address,phone);
        return databaseReference.child(phone).setValue(user);
    }
    public Task<Void> addItems(String itemName, String quantity,String price,String userID){
        databaseReference = db.getReference("Kisan_Items");
        KisanItems items = new KisanItems(itemName,quantity,price);
        return databaseReference.child(userID).child(itemName).setValue(items);
    }
    public Task<Void> modifyQuantity(String itemName,String newQuantity){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //String userID = user.getPhoneNumber();
        databaseReference = db.getReference("Kisan_Items");
        return databaseReference.child("+919620533961"/*userID*/).child(itemName).child("quantity").setValue(newQuantity);
    }
    public Task<Void> deleteQuantity(String itemName){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //String userID = user.getPhoneNumber();
        databaseReference = db.getReference("Kisan_Items");
        return databaseReference.child("+919620533961"/*userID*/).child(itemName).removeValue();
    }
}
