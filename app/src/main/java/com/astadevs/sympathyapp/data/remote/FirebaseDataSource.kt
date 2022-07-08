package com.astadevs.sympathyapp.data.remote

import com.astadevs.sympathyapp.helper.FirebaseReferenceValueObserver
import com.astadevs.sympathyapp.data.Result
import com.astadevs.sympathyapp.data.model.Message
import com.astadevs.sympathyapp.data.model.UserFriend
import com.astadevs.sympathyapp.helper.wrapSnapshotToClass
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.database.*

class FirebaseReferenceChildObserver {
    private var valueEventListener: ChildEventListener? = null
    private var dbRef: DatabaseReference? = null
    private var isObserving: Boolean = false

    fun start(valueEventListener: ChildEventListener, reference: DatabaseReference) {
        isObserving = true
        reference.addChildEventListener(valueEventListener)
        this.valueEventListener = valueEventListener
        this.dbRef = reference
    }

    fun clear() {
        valueEventListener?.let { dbRef?.removeEventListener(it) }
        isObserving = false
        valueEventListener = null
        dbRef = null
    }

    fun isObserving(): Boolean {
        return isObserving
    }
}

class FirebaseDataSource {

    companion object {
        val dbInstance = FirebaseDatabase.getInstance()
    }

    //region Private

    private fun refToPath(path: String): DatabaseReference {
        return dbInstance.reference.child(path)
    }

    private fun attachValueListenerToTaskCompletion(src: TaskCompletionSource<DataSnapshot>): ValueEventListener {
        return (object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                src.setException(Exception(error.message))
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                src.setResult(snapshot)
            }
        })
    }

    private fun <T> attachValueListenerToBlock(
        resultClassName: Class<T>,
        b: ((Result<T>) -> Unit)
    ): ValueEventListener {
        return (object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                b.invoke(Result.Error(error.message))
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (wrapSnapshotToClass(resultClassName, snapshot) == null) {
                    b.invoke(Result.Error(msg = snapshot.key))
                } else {
                    b.invoke(Result.Success(wrapSnapshotToClass(resultClassName, snapshot)))
                }
            }
        })
    }

    private fun <T> attachChildListenerToBlock(resultClassName: Class<T>, b: ((Result<T>) -> Unit)): ChildEventListener {
        return (object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                b.invoke(Result.Success(wrapSnapshotToClass(resultClassName, snapshot)))
            }

            override fun onCancelled(error: DatabaseError) { b.invoke(Result.Error(error.message)) }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}
        })
    }

    fun updateNewFriend(myUser: UserFriend, otherUser: UserFriend) {
        refToPath("Users/${myUser.userID}/friends/${otherUser.userID}").setValue(otherUser)
        refToPath("Users/${otherUser.userID}/friends/${myUser.userID}").setValue(myUser)
    }

    fun updateLastMessage(chatID: String, message: Message) {
        refToPath("Chats/$chatID/lastMessage").setValue(message)
    }

    fun pushNewMessage(messagesID: String, message: Message) {
        refToPath("Messages/$messagesID").push().setValue(message)
    }

    fun loadUserInfoTask(userID: String): Task<DataSnapshot> {
        val src = TaskCompletionSource<DataSnapshot>()
        val listener = attachValueListenerToTaskCompletion(src)
        refToPath("Users/$userID").addListenerForSingleValueEvent(listener)
        return src.task
    }

    fun loadFriendsTask(userID: String): Task<DataSnapshot> {
        val src = TaskCompletionSource<DataSnapshot>()
        val listener = attachValueListenerToTaskCompletion(src)
        refToPath("Users/$userID/friends").addListenerForSingleValueEvent(listener)
        return src.task
    }

    fun loadChatTask(chatID: String): Task<DataSnapshot> {
        val src = TaskCompletionSource<DataSnapshot>()
        val listener = attachValueListenerToTaskCompletion(src)
        refToPath("Chats/$chatID").addListenerForSingleValueEvent(listener)
        return src.task
    }

    fun <T> attachChatObserver(resultClassName: Class<T>, chatID: String, refObs: FirebaseReferenceValueObserver, b: ((Result<T>) -> Unit)) {
        val listener = attachValueListenerToBlock(resultClassName, b)
        refObs.start(listener, refToPath("Chats/$chatID"))
    }

    fun <T> attachUserInfoObserver(resultClassName: Class<T>, userID: String, refObs: FirebaseReferenceValueObserver, b: ((Result<T>) -> Unit)) {
        val listener = attachValueListenerToBlock(resultClassName, b)
        refObs.start(listener, refToPath("Users/$userID"))
    }

    fun <T> attachMessagesObserver(resultClassName: Class<T>, messagesID: String, refObs: FirebaseReferenceChildObserver, b: ((Result<T>) -> Unit)) {
        val listener = attachChildListenerToBlock(resultClassName, b)
        refObs.start(listener, refToPath("Messages/$messagesID"))
    }
}