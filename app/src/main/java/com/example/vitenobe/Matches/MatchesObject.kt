package com.example.vitenobe.Matches


class MatchesObject(var userId: String, var name: String, var profileImageUrl: String) {

    fun setUserID(userID: String?) {
        userId = userID.toString()
    }

}