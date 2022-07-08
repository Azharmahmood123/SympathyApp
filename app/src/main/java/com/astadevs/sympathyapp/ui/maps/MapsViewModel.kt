package com.astadevs.sympathyapp.ui.maps

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.astadevs.sympathyapp.base.Resource
import com.astadevs.sympathyapp.helper.convertTwoUserIDs
import com.astadevs.sympathyapp.data.model.Chat
import com.astadevs.sympathyapp.data.model.Message
import com.astadevs.sympathyapp.data.model.UserFriend
import com.astadevs.sympathyapp.data.repository.DatabaseRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class MapsViewModel constructor(
    private val firebaseDatabase: FirebaseDatabase
) : ViewModel() {

    private val dbRepository: DatabaseRepository = DatabaseRepository()

    private val _responseNewChat = MutableLiveData<Resource<Chat>>()
    val responseNewChat = _responseNewChat

    private val _responseAvailableChat = MutableLiveData<Resource<Chat>>()
    val responseAvailableChat = _responseAvailableChat

    fun updateNewChat(myUserID: String, otherUserID: String) {
        viewModelScope.launch {
            _responseNewChat.value = Resource.Loading
            val newChat = Chat().apply {
                info.id = convertTwoUserIDs(myUserID, otherUserID)
                lastMessage = Message(seen = true, text = "Say hello!")
            }
            val databaseReference =
                firebaseDatabase.getReference("Chats/${newChat.info.id}").setValue(newChat)
            databaseReference.addOnSuccessListener {
                dbRepository.updateNewFriend(UserFriend(myUserID), UserFriend(otherUserID))
                _responseNewChat.value = Resource.Success(newChat)

            }.addOnFailureListener {
                _responseNewChat.value = Resource.Error(it.message ?: "Unknown Error")
            }
        }
    }

    fun checkChatAvailable(myUserID: String, otherUserID: String) {
        viewModelScope.launch {
            _responseAvailableChat.value = Resource.Loading
            val chatID = convertTwoUserIDs(myUserID, otherUserID)
            val databaseReference =
                firebaseDatabase.getReference("Chats/${chatID}")
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val newChat = snapshot.getValue(Chat::class.java)
                        if (newChat != null) {
                            _responseAvailableChat.value = Resource.Success(newChat)
                        } else {
                            _responseAvailableChat.value = Resource.Success(null)
                        }
                    } else {
                        _responseAvailableChat.value = Resource.Success(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _responseAvailableChat.value = Resource.Error(error.message)
                }
            })
        }
    }

}