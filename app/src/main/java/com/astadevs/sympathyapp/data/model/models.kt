package com.astadevs.sympathyapp.data.model

import com.google.firebase.database.PropertyName
import java.util.*

data class UserObject(
    var uid: String = "",
    val userType: String = "",
    val email: String = "",
    val fbrNo: String = "",
)

data class OrganizationObject(
    val userID: String = "",
    var organizationID: String = "",
    val title: String = "",
    val bio: String = "",
    val latitude: String = "",
    val longitude: String = ""
)

data class CampaignObject(
    val organizationID: String = "",
    var campaignID: String = "",
    var campaignImageURL: String = "",
    val campaignTitle: String = "",
    val campaignDescription: String = "",
    val campaignAmount: String = "0",
    var receivedAmount: String = "0",
    val campaignStatus: String = "true"
)

data class CampaignDonationObject(
    var requestID: String = "",
    val organizationID: String = "",
    val organizationLat: String = "",
    val organizationLong: String = "",
    val campaignID: String = "",
    val userID: String = "",
    val requestType: String = "",
    val requestSelfType: String = "",
    val requestAmount: String = "0",
    val requestPasscode: String = ""
)

data class UploadFileObject(
    val status: String = "",
    val fileProgress: String = "",
    val uploadedFilePath: String = ""
)


data class Chat(
    @get:PropertyName("lastMessage") @set:PropertyName("lastMessage") var lastMessage: Message = Message(),
    @get:PropertyName("info") @set:PropertyName("info") var info: ChatInfo = ChatInfo()
)

data class ChatInfo(
    @get:PropertyName("id") @set:PropertyName("id") var id: String = ""
)

data class Message(
    @get:PropertyName("senderID") @set:PropertyName("senderID") var senderID: String = "",
    @get:PropertyName("text") @set:PropertyName("text") var text: String = "",
    @get:PropertyName("epochTimeMs") @set:PropertyName("epochTimeMs") var epochTimeMs: Long = Date().time,
    @get:PropertyName("seen") @set:PropertyName("seen") var seen: Boolean = false
)

data class UserFriend(
    @get:PropertyName("userID") @set:PropertyName("userID") var userID: String = ""
)

data class ChatWithUserInfo(
    var mChat: Chat,
    var mUserInfo: UserObject
)