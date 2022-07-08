package com.astadevs.sympathyapp.helper

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener


fun <T> wrapSnapshotToClass(className: Class<T>, snap: DataSnapshot): T? {
    return snap.getValue(className)
}

fun <T> wrapSnapshotToArrayList(className: Class<T>, snap: DataSnapshot): MutableList<T> {
    val arrayList: MutableList<T> = arrayListOf()
    for (child in snap.children) {
        child.getValue(className)?.let { arrayList.add(it) }
    }
    return arrayList
}

// Always returns the same combined id when comparing the two users id's
fun convertTwoUserIDs(userID1: String, userID2: String): String {
    return if (userID1 < userID2) {
        userID2 + userID1
    } else {
        userID1 + userID2
    }
}


class FirebaseReferenceValueObserver {
    private var valueEventListener: ValueEventListener? = null
    private var dbRef: DatabaseReference? = null

    fun start(valueEventListener: ValueEventListener, reference: DatabaseReference) {
        reference.addValueEventListener(valueEventListener)
        this.valueEventListener = valueEventListener
        this.dbRef = reference
    }

    fun clear() {
        valueEventListener?.let { dbRef?.removeEventListener(it) }
        valueEventListener = null
        dbRef = null
    }
}