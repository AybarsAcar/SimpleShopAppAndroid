package com.aybarsacar.ecommercefirebase.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.aybarsacar.ecommercefirebase.models.User
import com.aybarsacar.ecommercefirebase.ui.activities.LoginActivity
import com.aybarsacar.ecommercefirebase.ui.activities.RegisterActivity
import com.aybarsacar.ecommercefirebase.ui.activities.UserProfileActivity
import com.aybarsacar.ecommercefirebase.utils.helpers.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions


/**
 * access layer of the Google Firebase Firestore services
 */
class FirestoreService {

  private val _fireStore = FirebaseFirestore.getInstance()


  fun registerUser(activity: RegisterActivity, user: User) {

    // the users is the collection name
    // if the collection is not created Firestore creates a collection with this name
    _fireStore.collection(Constants.USERS)
      .document(user.id)
      .set(user, SetOptions.merge())
      .addOnSuccessListener {

        activity.onUserRegistrationSuccess()

      }
      .addOnFailureListener {

        activity.hideLoadingProgressDialog()
        Log.e(activity.javaClass.simpleName, "Error while registering user", it)

      }
  }


  fun getUserById(activity: Activity) {

    _fireStore.collection(Constants.USERS)
      .document(getCurrentUserId())
      .get()
      .addOnSuccessListener {
        Log.i(activity.javaClass.simpleName, it.toString())

        val user = it.toObject(User::class.java)!!

        // private mode so only accessible to our application
        val sharedPreferences = activity.getSharedPreferences(Constants.MY_SHOP_PREFERENCES, Context.MODE_PRIVATE)

        // save our user to it
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(Constants.LOGGED_IN_USERNAME, "${user.firstName} ${user.lastName}")

        editor.apply()

        when (activity) {

          is LoginActivity -> {
            activity.userLoggedInSuccess(user)
          }
        }
      }
      .addOnFailureListener {

        when (activity) {

          is LoginActivity -> {
          }
        }
      }

  }


  /**
   * returns the currently logged in user's id
   * from the auth token
   */
  fun getCurrentUserId(): String {

    val currentUser = FirebaseAuth.getInstance().currentUser

    var currentUserId = ""
    if (currentUser != null) {
      currentUserId = currentUser.uid
    }

    return currentUserId
  }


  fun updateUserDetails(activity: Activity, userHashMap: HashMap<String, Any>) {

    _fireStore.collection(Constants.USERS)
      .document(getCurrentUserId())
      .update(userHashMap)
      .addOnSuccessListener {

        when (activity) {
          is UserProfileActivity -> {
            activity.onUserProfileUpdatedSuccess()
          }
        }
      }
      .addOnFailureListener {
        when (activity) {
          is UserProfileActivity -> {
            activity.hideLoadingProgressDialog()
          }
        }

        Log.e(activity.javaClass.simpleName, "Error updating the user details", it)
      }
  }
}