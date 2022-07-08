package com.astadevs.sympathyapp.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.astadevs.sympathyapp.base.Resource
import com.astadevs.sympathyapp.data.model.OrganizationObject
import com.astadevs.sympathyapp.data.model.UserObject
import com.astadevs.sympathyapp.repositories.AuthRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class ProfileViewModel constructor(
    private val authRepository: AuthRepository,
    private val firebaseDatabase: FirebaseDatabase
) : ViewModel() {

    val isLogout = MutableLiveData<Boolean>()

    fun logoutUser() {
        viewModelScope.launch {
            authRepository.logoutUser()
            isLogout.value = true
        }
    }

    private val _responseUserObject = MutableLiveData<Resource<UserObject>>()
    val responseUserObject = _responseUserObject

    private val _responseOrganization = MutableLiveData<Resource<Boolean>>()
    val responseOrganization = _responseOrganization

    fun fetchUserDetail(userID: String) {
        viewModelScope.launch {
            _responseUserObject.value = Resource.Loading
            val databaseReference = firebaseDatabase.getReference("Users")
            val query = databaseReference.orderByChild("uid").equalTo(userID)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val spShot = snapshot.children.iterator().next()
                        val userObject = spShot.getValue(UserObject::class.java)
                        _responseUserObject.value = Resource.Success(userObject)
                    } else {
                        _responseUserObject.value = Resource.Success(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _responseUserObject.value = Resource.Error(error.message)
                }
            })
        }
    }

    fun updateOrganizationDetail(orgId: String, organization: OrganizationObject) {
        viewModelScope.launch {
            _responseOrganization.value = Resource.Loading
            val databaseReference = firebaseDatabase.getReference("Organization")
            databaseReference.child(orgId).setValue(organization)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        _responseOrganization.value = Resource.Success(true)
                    } else {
                        _responseOrganization.value =
                            Resource.Error(it.exception?.message ?: "Unknown Error")
                    }
                }
        }
    }
}