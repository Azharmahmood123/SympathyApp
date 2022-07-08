package com.astadevs.sympathyapp.data.repository

import com.astadevs.sympathyapp.data.model.UserFriend
import com.astadevs.sympathyapp.data.Result
import com.astadevs.sympathyapp.data.model.Chat
import com.astadevs.sympathyapp.data.model.Message
import com.astadevs.sympathyapp.data.model.UserObject
import com.astadevs.sympathyapp.data.remote.FirebaseDataSource
import com.astadevs.sympathyapp.data.remote.FirebaseReferenceChildObserver
import com.astadevs.sympathyapp.helper.FirebaseReferenceValueObserver
import com.astadevs.sympathyapp.helper.wrapSnapshotToArrayList
import com.astadevs.sympathyapp.helper.wrapSnapshotToClass
import com.google.firebase.auth.UserInfo

class DatabaseRepository {
    private val firebaseDatabaseService = FirebaseDataSource()

    fun updateNewFriend(myUser: UserFriend, otherUser: UserFriend) {
        firebaseDatabaseService.updateNewFriend(myUser, otherUser)
    }

    fun updateNewMessage(messagesID: String, message: Message) {
        firebaseDatabaseService.pushNewMessage(messagesID, message)
    }

    fun updateChatLastMessage(chatID: String, message: Message) {
        firebaseDatabaseService.updateLastMessage(chatID, message)
    }

    fun loadFriends(userID: String, b: ((Result<List<UserFriend>>) -> Unit)) {
        b.invoke(Result.Loading)
        firebaseDatabaseService.loadFriendsTask(userID).addOnSuccessListener {
            val friendsList = wrapSnapshotToArrayList(UserFriend::class.java, it)
            b.invoke(Result.Success(friendsList))
        }.addOnFailureListener { b.invoke(Result.Error(it.message)) }
    }

    fun loadUserInfo(userID: String, b: ((Result<UserObject>) -> Unit)) {
        firebaseDatabaseService.loadUserInfoTask(userID).addOnSuccessListener {
            b.invoke(Result.Success(wrapSnapshotToClass(UserObject::class.java, it)))
        }.addOnFailureListener { b.invoke(Result.Error(it.message)) }
    }

    fun loadAndObserveChat(chatID: String, observer: FirebaseReferenceValueObserver, b: ((Result<Chat>) -> Unit)) {
        firebaseDatabaseService.attachChatObserver(Chat::class.java, chatID, observer, b)
    }

    fun loadAndObserveUserInfo(userID: String, observer: FirebaseReferenceValueObserver, b: ((Result<UserObject>) -> Unit)) {
        firebaseDatabaseService.attachUserInfoObserver(UserObject::class.java, userID, observer, b)
    }

    fun loadAndObserveMessagesAdded(messagesID: String, observer: FirebaseReferenceChildObserver, b: ((Result<Message>) -> Unit)) {
        firebaseDatabaseService.attachMessagesObserver(Message::class.java, messagesID, observer, b)
    }

    fun loadChat(chatID: String, b: ((Result<Chat>) -> Unit)) {
        firebaseDatabaseService.loadChatTask(chatID).addOnSuccessListener {
            b.invoke(Result.Success(wrapSnapshotToClass(Chat::class.java, it)))
        }.addOnFailureListener { b.invoke(Result.Error(it.message)) }
    }
}