package com.astadevs.sympathyapp.ui.chat

import androidx.lifecycle.*
import com.astadevs.sympathyapp.data.model.Chat
import com.astadevs.sympathyapp.data.Result
import com.astadevs.sympathyapp.data.model.Message
import com.astadevs.sympathyapp.data.model.UserObject
import com.astadevs.sympathyapp.data.remote.FirebaseReferenceChildObserver
import com.astadevs.sympathyapp.data.repository.DatabaseRepository
import com.astadevs.sympathyapp.helper.FirebaseReferenceValueObserver
import com.astadevs.sympathyapp.helper.addNewItem
import com.astadevs.sympathyapp.ui.DefaultViewModel
import com.google.firebase.auth.UserInfo

class ChatViewModelFactory(
    private val myUserID: String,
    private val otherUserID: String,
    private val chatID: String
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChatViewModel(myUserID, otherUserID, chatID) as T
    }
}

class ChatViewModel(
    private val myUserID: String,
    private val otherUserID: String,
    private val chatID: String
) : DefaultViewModel() {

    private val dbRepository: DatabaseRepository = DatabaseRepository()

    private val _otherUser: MutableLiveData<UserObject> = MutableLiveData()
    private val _addedMessage = MutableLiveData<Message>()

    private val fbRefMessagesChildObserver = FirebaseReferenceChildObserver()
    private val fbRefUserInfoObserver = FirebaseReferenceValueObserver()

    val messagesList = MediatorLiveData<MutableList<Message>>()
    val newMessageText = MutableLiveData<String?>()
    val otherUser: LiveData<UserObject> = _otherUser

    init {
        setupChat()
        checkAndUpdateLastMessageSeen()
    }

    override fun onCleared() {
        super.onCleared()
        fbRefMessagesChildObserver.clear()
        fbRefUserInfoObserver.clear()
    }

    private fun checkAndUpdateLastMessageSeen() {
        dbRepository.loadChat(chatID) { result: Result<Chat> ->
            if (result is Result.Success && result.data != null) {
                result.data.lastMessage.let {
                    if (!it.seen && it.senderID != myUserID) {
                        it.seen = true
                        dbRepository.updateChatLastMessage(chatID, it)
                    }
                }
            }
        }
    }

    private fun setupChat() {
        dbRepository.loadAndObserveUserInfo(
            otherUserID,
            fbRefUserInfoObserver
        ) { result: Result<UserObject> ->
            onResult(_otherUser, result)
            if (result is Result.Success && !fbRefMessagesChildObserver.isObserving()) {
                loadAndObserveNewMessages()
            }
        }
    }

    private fun loadAndObserveNewMessages() {
        messagesList.addSource(_addedMessage) { messagesList.addNewItem(it) }

        dbRepository.loadAndObserveMessagesAdded(
            chatID,
            fbRefMessagesChildObserver
        ) { result: Result<Message> ->
            onResult(_addedMessage, result)
        }
    }

    fun sendMessagePressed() {
        if (!newMessageText.value.isNullOrBlank()) {
            val newMsg = Message(myUserID, newMessageText.value!!)
            dbRepository.updateNewMessage(chatID, newMsg)
            dbRepository.updateChatLastMessage(chatID, newMsg)
            newMessageText.value = null
        }
    }
}