package me.phantomx.githubuserapp.data.entity


import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Entity(tableName = "user")
@Parcelize
data class User(
    @PrimaryKey
    @SerializedName("id")
    val id: Int,
    @SerializedName("avatar_url")
    @ColumnInfo(name = "avatar_url")
    val avatarUrl: String,
    @SerializedName("company")
    @ColumnInfo(name = "company")
    val company: String?,
    @SerializedName("followers")
    @ColumnInfo(name = "followers")
    val followers: Int,
    @SerializedName("following")
    @ColumnInfo(name = "following")
    val following: Int,
    @SerializedName("html_url")
    @ColumnInfo(name = "html_url")
    val htmlUrl: String,
    @SerializedName("location")
    @ColumnInfo(name = "location")
    val location: String?,
    @SerializedName("login")
    @ColumnInfo(name = "login")
    val login: String,
    @SerializedName("name")
    @ColumnInfo(name = "name")
    val name: String?,
    @SerializedName("public_repos")
    @ColumnInfo(name = "public_repos")
    val publicRepos: Int
) : Parcelable