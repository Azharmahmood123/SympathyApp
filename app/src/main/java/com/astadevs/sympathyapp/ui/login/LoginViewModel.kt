package com.astadevs.sympathyapp.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.astadevs.sympathyapp.AppSettings
import com.astadevs.sympathyapp.base.Resource
import com.astadevs.sympathyapp.data.model.UserObject
import com.astadevs.sympathyapp.repositories.AuthRepository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.coroutines.launch

class LoginViewModel constructor(
    private val authRepository: AuthRepository,
    private val firebaseDatabase: FirebaseDatabase
) : ViewModel() {

    private val _responseUser = MutableLiveData<Resource<FirebaseUser>>()
    val responseUser = _responseUser

    private val _userInfo = MutableLiveData<Resource<UserObject>>()
    val userInfo = _userInfo

    fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        fbrNo: String,
        userType: String
    ) {
        viewModelScope.launch {
            _responseUser.value = Resource.Loading
            authRepository.createUserWithEmailAndPassword(
                email,
                password
            ) { firebaseUser, exception ->
                if (firebaseUser != null) {
                    val uid = firebaseUser.uid
                    val userObject = UserObject(
                        uid = uid,
                        userType = userType,
                        email = email,
                        fbrNo = fbrNo
                    )
                    val databaseReference = firebaseDatabase.getReference("Users")
                    databaseReference.child(uid).setValue(userObject)
                    databaseReference.addChildEventListener(object : ChildEventListener {
                        override fun onChildAdded(
                            snapshot: DataSnapshot,
                            previousChildName: String?
                        ) {
                            AppSettings.userEmail = email
                            _responseUser.value = Resource.Success(firebaseUser)
                        }

                        override fun onChildChanged(
                            snapshot: DataSnapshot,
                            previousChildName: String?
                        ) {

                        }

                        override fun onChildRemoved(snapshot: DataSnapshot) {

                        }

                        override fun onChildMoved(
                            snapshot: DataSnapshot,
                            previousChildName: String?
                        ) {

                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
                } else {
                    _responseUser.value = Resource.Error(exception?.message ?: "Unknown Error")
                }
            }
        }
    }

    fun loginUserWithEmailAndPassword(email: String, password: String) {
        _responseUser.value = Resource.Loading
        authRepository.loginUserWithEmailAndPassword(email, password) { firebaseUser, exception ->
            if (exception == null && firebaseUser != null) {
                AppSettings.userUID = firebaseUser.uid
                AppSettings.userEmail = email
                _responseUser.value = Resource.Success(firebaseUser)
            } else {
                _responseUser.value = Resource.Error(exception?.message ?: "Unknown Error")
            }
        }
    }

    fun fetchUserInfo(userID: String) {
        viewModelScope.launch {
            _userInfo.value = Resource.Loading
            val databaseReference = firebaseDatabase.getReference("Users").child(userID)
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userObject = snapshot.getValue(UserObject::class.java)
                        if (userObject != null) {
                            _userInfo.value = Resource.Success(userObject)
                        } else {
                            _userInfo.value = Resource.Error("Unknown Error")
                        }
                    } else {
                        _userInfo.value = Resource.Error("Unknown Error")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _userInfo.value = Resource.Error(error.message)
                }
            })
        }
    }
}