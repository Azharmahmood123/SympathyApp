package com.astadevs.sympathyapp.ui.organization

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.astadevs.sympathyapp.base.Resource
import com.astadevs.sympathyapp.data.model.OrganizationObject
import com.google.firebase.database.*
import kotlinx.coroutines.launch

class OrganizationViewModel constructor(
    private val firebaseDatabase: FirebaseDatabase
) : ViewModel() {

    private val _responseOrganization = MutableLiveData<Resource<Boolean>>()
    val responseOrganization = _responseOrganization

    private val _responseOrganizationDetail = MutableLiveData<Resource<OrganizationObject>>()
    val responseOrganizationDetail = _responseOrganizationDetail

    private val _organizationList = MutableLiveData<Resource<List<OrganizationObject>>>()
    val organizationList = _organizationList

    fun addOrganizationDetail(organization: OrganizationObject) {
        viewModelScope.launch {
            _responseOrganization.value = Resource.Loading
            val databaseReference = firebaseDatabase.getReference("Organization")
            val organizationID = databaseReference.push().key
            if (organizationID != null) {
                organization.organizationID = organizationID
                databaseReference.child(organizationID).setValue(organization)
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

    fun fetchOrganizationDetail(userID: String, organizationID: String) {
        viewModelScope.launch {
            _responseOrganizationDetail.value = Resource.Loading
            val databaseReference = firebaseDatabase.getReference("Organization")
            val query = when {
                userID.isNotEmpty() -> {
                    databaseReference.orderByChild("userID").equalTo(userID)
                }
                else -> {
                    databaseReference.orderByChild("organizationID").equalTo(organizationID)
                }
            }
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val spShot = snapshot.children.iterator().next()
                        val organizationObject = spShot.getValue(OrganizationObject::class.java)
                        if (organizationObject != null) {
                            _responseOrganizationDetail.value = Resource.Success(organizationObject)
                        } else {
                            _responseOrganizationDetail.value = Resource.Success(null)
                        }
                    } else {
                        _responseOrganizationDetail.value = Resource.Success(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _responseOrganizationDetail.value = Resource.Error(error.message)
                }
            })
        }
    }

    fun fetchOrganizations() {
        viewModelScope.launch {
            _organizationList.value = Resource.Loading
            val databaseReference = firebaseDatabase.getReference("Organization")
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val organizationList = ArrayList<OrganizationObject>()
                        for (snapshot in dataSnapshot.children) {
                            val organizationObject =
                                snapshot.getValue(OrganizationObject::class.java)
                            if (organizationObject != null) {
                                organizationList.add(organizationObject)
                            }
                        }
                        _organizationList.value = Resource.Success(organizationList)
                    } else {
                        _organizationList.value = Resource.Error("Organization list is empty")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _organizationList.value = Resource.Error(error.message)
                }
            })
        }
    }
}