package com.astadevs.sympathyapp.ui.chats

import androidx.lifecycle.*
import com.astadevs.sympathyapp.data.Event
import com.astadevs.sympathyapp.data.Result
import com.astadevs.sympathyapp.data.model.Chat
import com.astadevs.sympathyapp.data.model.ChatWithUserInfo
import com.astadevs.sympathyapp.data.model.UserFriend
import com.astadevs.sympathyapp.data.model.UserObject
import com.astadevs.sympathyapp.data.repository.DatabaseRepository
import com.astadevs.sympathyapp.helper.FirebaseReferenceValueObserver
import com.astadevs.sympathyapp.helper.addNewItem
import com.astadevs.sympathyapp.helper.convertTwoUserIDs
import com.astadevs.sympathyapp.helper.updateItemAt
import com.astadevs.sympathyapp.ui.DefaultViewModel

class ChatsViewModelFactory(private val myUserID: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChatsViewModel(myUserID) as T
    }
}

class ChatsViewModel(val myUserID: String) : DefaultViewModel() {

    private val repository: DatabaseRepository = DatabaseRepository()
    private val firebaseReferenceObserverList = ArrayList<FirebaseReferenceValueObserver>()
    private val _updatedChatWithUserInfo = MutableLiveData<ChatWithUserInfo>()
    private val _selectedChat = MutableLiveData<Event<ChatWithUserInfo>>()

    var selectedChat: LiveData<Event<ChatWithUserInfo>> = _selectedChat
    val chatsList = MediatorLiveData<MutableList<ChatWithUserInfo>>()

    init {
        chatsList.addSource(_updatedChatWithUserInfo) { newChat ->
            val chat = chatsList.value?.find { it.mChat.info.id == newChat.mChat.info.id }
            if (chat == null) {
                chatsList.addNewItem(newChat)
            } else {
                chatsList.updateItemAt(newChat, chatsList.value!!.indexOf(chat))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        firebaseReferenceObserverList.forEach { it.clear() }
    }

    fun setupChats() {
        loadFriends()
    }

    private fun loadFriends() {
        repository.loadFriends(myUserID) { result: Result<List<UserFriend>> ->
            onResult(null, result)
            if (result is Result.Success) result.data?.forEach { loadUserInfo(it) }
        }
    }

    private fun loadUserInfo(userFriend: UserFriend) {
        repository.loadUserInfo(userFriend.userID) { result: Result<UserObject> ->
            onResult(null, result)
            if (result is Result.Success) result.data?.let { loadAndObserveChat(it) }
        }
    }

    private fun loadAndObserveChat(userInfo: UserObject) {
        val observer = FirebaseReferenceValueObserver()
        firebaseReferenceObserverList.add(observer)
        repository.loadAndObserveChat(
            convertTwoUserIDs(myUserID, userInfo.uid),
            observer
        ) { result: Result<Chat> ->
            if (result is Result.Success) {
                _updatedChatWithUserInfo.value = result.data?.let { ChatWithUserInfo(it, userInfo) }
            } else if (result is Result.Error) {
                chatsList.value?.let {
                    val newList = mutableListOf<ChatWithUserInfo>().apply { addAll(it) }
                    newList.removeIf { it2 -> result.msg.toString().contains(it2.mUserInfo.uid) }
                    chatsList.value = newList
                }
            }
        }
    }

    fun selectChatWithUserInfoPressed(chat: ChatWithUserInfo) {
        _selectedChat.value = Event(chat)
    }
}