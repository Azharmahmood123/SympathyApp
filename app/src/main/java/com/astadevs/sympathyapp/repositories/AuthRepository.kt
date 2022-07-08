package com.astadevs.sympathyapp.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthRepository constructor(
    private val firebaseAuth: FirebaseAuth
) {
    fun loginUserWithEmailAndPassword(email: String, password: String, callback: (FirebaseUser?, Exception?) -> Unit){
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener { user ->
            callback(user.user, null)
        }.addOnFailureListener { exception ->
            callback(null, exception)
        }
    }

    fun createUserWithEmailAndPassword(email: String, password: String, callback: (FirebaseUser? , Exception?) -> Unit){
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener { user ->
            callback(user.user, null)
        }.addOnFailureListener { exception ->
            callback(null, exception)
        }
    }

    fun logoutUser() {
        firebaseAuth.signOut()
    }
}