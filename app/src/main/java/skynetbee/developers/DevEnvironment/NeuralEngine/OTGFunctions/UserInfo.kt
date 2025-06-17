package skynetbee.developers.DevEnvironment

//
//  TABLES
//  UserInfo.kt
//
//  Created by Gowtham Bharath N on 3/3/2025.
//

class User {
    private var name: String?
    private var id: String?
    private var area: String?
    private var ownComCode: String?

    init {
        name = null
        id = null
        area = "skytest"
        ownComCode = "developer"
    }

    fun setUserInfo(name: String, id: String) {
        this.name = name
        this.id = id
    }

    fun getName(): String {
        return name ?: "Attention Required : getName is called before User Name is set"
    }

    fun getId(): String {
        return id ?: "Attention Required : getId is called before User ID is set"
    }

    fun getArea(): String {
        return area ?: "Attention Required : getArea is called before User Area is set"
    }

    fun getOwnComCode(): String {
        return ownComCode ?: "Attention Required : getOwnComCode is called before User OwnComCode is set"
    }
}
var user = User()