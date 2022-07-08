package com.astadevs.sympathyapp.ui.campaigns

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.astadevs.sympathyapp.base.Resource
import com.astadevs.sympathyapp.data.model.CampaignDonationObject
import com.astadevs.sympathyapp.data.model.CampaignObject
import com.astadevs.sympathyapp.data.model.UploadFileObject
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.util.*

class CampaignsViewModel constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val firebaseStorage: FirebaseStorage
) : ViewModel() {

    private val _addCampaignResponse = MutableLiveData<Resource<Boolean>>()
    val addCampaignResponse = _addCampaignResponse

    private val _addCampaignFile = MutableLiveData<Resource<UploadFileObject>>()
    val addCampaignFile = _addCampaignFile

    private val _campaignsList = MutableLiveData<Resource<List<CampaignObject>>>()
    val campaignsList = _campaignsList

    private val _campaignsDetail = MutableLiveData<Resource<CampaignObject>>()
    val campaignsDetail = _campaignsDetail

    private val _addCampaignDonationRequest =
        MutableLiveData<Resource<CampaignDonationObject>>()
    val addDonationRequest = _addCampaignDonationRequest

    private val _donationList = MutableLiveData<Resource<List<CampaignDonationObject>>>()
    val donationList = _donationList

    fun addNewCampaign(campaignObject: CampaignObject) {
        viewModelScope.launch {
            _addCampaignResponse.value = Resource.Loading
            val databaseReference = firebaseDatabase.getReference("Campaigns")
            val campaignID = databaseReference.push().key
            if (campaignID != null) {
                campaignObject.campaignID = campaignID
                databaseReference.child(campaignID).setValue(campaignObject)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            _addCampaignResponse.value = Resource.Success(true)
                        } else {
                            _addCampaignResponse.value =
                                Resource.Error(it.exception?.message ?: "Unknown Error")
                        }
                    }
            }
        }
    }

    fun uploadCampaignImageFile(filePath: Uri) {
        _addCampaignFile.value = Resource.Loading
        val chileInfo = "Campaigns/" + UUID.randomUUID().toString()
        val storageReference = firebaseStorage.reference.child(chileInfo)
        storageReference.putFile(filePath)
            .addOnSuccessListener { taskSnapshot ->
                if (taskSnapshot.metadata != null) {
                    if (taskSnapshot.metadata!!.reference != null) {
                        val result = taskSnapshot.storage.downloadUrl
                        result.addOnSuccessListener { uri: Uri ->
                            val uploadedFilePath = uri.toString()
                            val uploadFileObject = UploadFileObject(
                                status = "complete",
                                uploadedFilePath = uploadedFilePath
                            )
                            _addCampaignFile.value = Resource.Success(uploadFileObject)
                        }
                    }
                }
            }.addOnFailureListener { e ->
                _addCampaignFile.value = Resource.Error(e.message ?: "Unknow Error")
            }.addOnProgressListener { taskSnapshot ->
                val progress =
                    100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                val uploadFileObject = UploadFileObject(
                    status = "progress",
                    fileProgress = "Uploaded " + progress.toInt() + "%"
                )
                _addCampaignFile.value = Resource.Success(uploadFileObject)
            }
    }

    fun fetchOrganizationCampaigns(organizationID: String) {
        viewModelScope.launch {
            _campaignsList.value = Resource.Loading
            val databaseReference = firebaseDatabase.getReference("Campaigns")
            val query = databaseReference.orderByChild("organizationID").equalTo(organizationID)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val campaignsList = ArrayList<CampaignObject>()
                        for (snapshot in dataSnapshot.children) {
                            val campaignObject = snapshot.getValue(CampaignObject::class.java)
                            if (campaignObject != null) {
                                campaignsList.add(campaignObject)
                            }
                        }
                        _campaignsList.value = Resource.Success(campaignsList)
                    } else {
                        _campaignsList.value = Resource.Success(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _campaignsList.value = Resource.Error(error.message)
                }
            })
        }
    }

    fun fetchUserCampaigns() {
        viewModelScope.launch {
            _campaignsList.value = Resource.Loading
            val databaseReference = firebaseDatabase.getReference("Campaigns")
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val campaignsList = ArrayList<CampaignObject>()
                        for (snapshot in dataSnapshot.children) {
                            val campaignObject = snapshot.getValue(CampaignObject::class.java)
                            if (campaignObject != null) {
                                campaignsList.add(campaignObject)
                            }
                        }
                        _campaignsList.value = Resource.Success(campaignsList)
                    } else {
                        _campaignsList.value = Resource.Success(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _campaignsList.value = Resource.Error(error.message)
                }
            })
        }
    }

    fun addCampaignDonation(request: CampaignDonationObject) {
        _addCampaignDonationRequest.value = Resource.Loading
        viewModelScope.launch {
            val databaseReference = firebaseDatabase.getReference("CampaignsDonation")
            val requestID = databaseReference.push().key
            if (requestID != null) {
                request.requestID = requestID
                databaseReference.child(requestID).setValue(request)
                databaseReference.addChildEventListener(object : ChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        if (snapshot.exists()) {
                            val requestObject =
                                snapshot.getValue(CampaignDonationObject::class.java)
                            if (requestObject != null) {
                                _addCampaignDonationRequest.value = Resource.Success(requestObject)
                            }
                        } else {
                            _addCampaignDonationRequest.value =
                                Resource.Error("Error in creating request.")
                        }
                    }

                    override fun onChildChanged(
                        snapshot: DataSnapshot,
                        previousChildName: String?
                    ) {

                    }

                    override fun onChildRemoved(snapshot: DataSnapshot) {

                    }

                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
            }
        }
    }

    fun fetchCampaignDetail(campaignID: String) {
        viewModelScope.launch {
            _campaignsDetail.value = Resource.Loading
            val databaseReference = firebaseDatabase.getReference("Campaigns")
            val query = databaseReference.orderByChild("campaignID").equalTo(campaignID)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val spShot = snapshot.children.iterator().next()
                        val campaignObject = spShot.getValue(CampaignObject::class.java)
                        if (campaignObject != null) {
                            _campaignsDetail.value = Resource.Success(campaignObject)
                        } else {
                            _campaignsDetail.value = Resource.Success(null)
                        }
                    } else {
                        _campaignsDetail.value = Resource.Success(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _campaignsDetail.value = Resource.Error(error.message)
                }
            })
        }
    }

    fun fetchDonationsForCampaign(campaignID: String = "", userID: String = "") {
        viewModelScope.launch {
            _donationList.value = Resource.Loading
            val databaseReference = firebaseDatabase.getReference("CampaignsDonation")
            val query = when {
                userID.isEmpty() -> {
                    databaseReference.orderByChild("campaignID").equalTo(campaignID)
                }
                else -> {
                    databaseReference.orderByChild("userID").equalTo(userID)
                }
            }
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val donationList = ArrayList<CampaignDonationObject>()
                        for (snapshot in dataSnapshot.children) {
                            val donation = snapshot.getValue(CampaignDonationObject::class.java)
                            if (donation != null) {
                                donationList.add(donation)
                            }
                        }
                        _donationList.value = Resource.Success(donationList)
                    } else {
                        _donationList.value = Resource.Success(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _donationList.value = Resource.Error(error.message)
                }
            })
        }
    }

}