package com.example.muzix.repository

import android.content.ContentValues
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginRepository {
    fun uploadData() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val db = Firebase.firestore
        db.collection("user").document(currentUser!!.uid)
            .set(
                hashMapOf(
                    "uid" to currentUser.uid,
                    "name" to currentUser.displayName,
                    "photoURL" to currentUser.photoUrl,
                )
            )
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Lỗi khi lấy thông tin user: ", exception)
            }
    }
}