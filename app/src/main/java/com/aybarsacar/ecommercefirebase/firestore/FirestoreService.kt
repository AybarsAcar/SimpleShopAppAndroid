package com.aybarsacar.ecommercefirebase.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.aybarsacar.ecommercefirebase.models.CartItem
import com.aybarsacar.ecommercefirebase.models.Product
import com.aybarsacar.ecommercefirebase.models.User
import com.aybarsacar.ecommercefirebase.ui.activities.*
import com.aybarsacar.ecommercefirebase.ui.fragments.DashboardFragment
import com.aybarsacar.ecommercefirebase.ui.fragments.ProductsFragment
import com.aybarsacar.ecommercefirebase.utils.helpers.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


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


  fun uploadProductDetails(activity: AddProductActivity, product: Product) {

    // the users is the collection name
    // if the collection is not created Firestore creates a collection with this name
    _fireStore.collection(Constants.PRODUCTS)
      .document()
      .set(product, SetOptions.merge())
      .addOnSuccessListener {

        activity.onProductUploadSuccess()

      }
      .addOnFailureListener {

        activity.onProductUploadFailure()
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

          is SettingsActivity -> {
            activity.onUserDetailsSuccess(user)
          }
        }
      }
      .addOnFailureListener {

        when (activity) {

          is LoginActivity -> {
            Log.e(activity.javaClass.simpleName, "Error fetching user details", it)
          }

          is SettingsActivity -> {
            activity.onUserDetailsFailure()
            Log.e(activity.javaClass.simpleName, "Error fetching user details", it)
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


  /**
   * uploads the image to the FirebaseStorage
   * user profile image is saved with the name of their id
   * imageType: constant of Profile_Image / Product_Image
   */
  fun uploadImageToCloudStorage(activity: Activity, imageFileUri: Uri?, imageType: String) {

    var imageIdentifier: String = ""
    if (imageType == Constants.PRODUCT_IMAGE) {
      imageIdentifier = System.currentTimeMillis().toString()
    } else if (imageType == Constants.USER_PROFILE_IMAGE) {
      // we allow each user to have a single photo
      imageIdentifier = getCurrentUserId()
    }

    val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
      // each user is allowed to have a single Profile Image
      imageType + imageIdentifier + "." + Constants.getFileExtension(
        activity,
        imageFileUri
      )
    )

    sRef.putFile(imageFileUri!!)
      .addOnSuccessListener { taskSnapshot ->

        Log.e("Firebase Image URL", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

        // get the downloadable url from the task snapshot
        taskSnapshot.metadata!!.reference!!.downloadUrl
          .addOnSuccessListener {
            Log.e("Downloadable Image Url", it.toString())

            when (activity) {
              is UserProfileActivity -> {
                activity.onImageUploadSuccess(it.toString())
              }

              is AddProductActivity -> {
                activity.onImageUploadSuccess(it.toString())
              }
            }
          }

      }.addOnFailureListener {

        when (activity) {
          is UserProfileActivity -> {
            activity.onImageUploadFailure()
          }

          is AddProductActivity -> {
            activity.onImageUploadFailure()
          }
        }

        Log.e(activity.javaClass.simpleName, it.message, it)

      }
  }

  /**
   * returns a list of products created by the currently logged in user
   */
  fun getProductList(fragment: Fragment) {
    _fireStore.collection(Constants.PRODUCTS)
      .whereEqualTo(Constants.USER_ID, getCurrentUserId())
      .get()
      .addOnSuccessListener {

        val products = ArrayList<Product>()

        for (productInfo in it.documents) {
          // cast to a product
          val product = productInfo.toObject(Product::class.java)

          product!!.id = productInfo.id

          products.add(product)
        }

        when (fragment) {

          is ProductsFragment -> {
            fragment.handleGetProductListSuccess(products)
          }

        }
      }
  }


  /**
   * dashboards gets all the products
   */
  fun getDashboardItemsList(fragment: DashboardFragment) {

    _fireStore.collection(Constants.PRODUCTS)
      .get()
      .addOnSuccessListener {

        val products = ArrayList<Product>()

        for (productInfo in it.documents) {
          // cast to a product
          val product = productInfo.toObject(Product::class.java)

          product!!.id = productInfo.id

          products.add(product)

          // call an onSuccess on the fragment
          fragment.handleGetDashboardItemsListSuccess(products)
        }
      }
      .addOnFailureListener {
        // call an onFailure method on the fragment
        fragment.handleGetDashboardItemsListFailure()
        Log.e(fragment.javaClass.simpleName, "Error while loading dashboard items", it)
      }
  }


  /**
   * deletes the product given an id
   */
  fun deleteProduct(fragment: ProductsFragment, productId: String) {

    _fireStore.collection(Constants.PRODUCTS)
      .document(productId)
      .delete()
      .addOnSuccessListener {

        fragment.handleProductDeleteSuccess()

      }
      .addOnFailureListener {
        fragment.handleProductDeleteFailure()
      }
  }


  /**
   * fetches the product details based on the id
   */
  fun getProductDetails(activity: ProductDetailsActivity, productId: String) {

    _fireStore.collection(Constants.PRODUCTS)
      .document(productId)
      .get()
      .addOnSuccessListener {
        val product = it.toObject(Product::class.java)

        activity.handleProductDetailsSuccess(product!!)
      }
      .addOnFailureListener {
        Log.e(activity.javaClass.simpleName, "Error getting the product details", it)
        activity.handleProductDetailsFailure()
      }
  }


  fun addCartItems(activity: ProductDetailsActivity, itemToAdd: CartItem) {
    _fireStore.collection(Constants.CART_ITEMS)
      .document()
      .set(itemToAdd, SetOptions.merge())
      .addOnSuccessListener {
        activity.handleAddCartItemsSuccess()
      }
      .addOnFailureListener {
        Log.e(activity.javaClass.simpleName, "Error getting the product details", it)
        activity.handleAddCartItemsFailure()
      }
  }


  fun itemExists(activity: ProductDetailsActivity, productId: String) {
    _fireStore.collection(Constants.CART_ITEMS)
      .whereEqualTo(Constants.USER_ID, getCurrentUserId())
      .whereEqualTo(Constants.PRODUCT_ID, productId)
      .get()
      .addOnSuccessListener {
        if (it.documents.size > 0) {
          activity.handleItemExistsSuccess()
        } else {
          activity.hideLoadingProgressDialog()
        }
      }
      .addOnFailureListener {
        Log.e(activity.javaClass.simpleName, "Error getting the product details", it)
        activity.hideLoadingProgressDialog()
      }
  }


  /**
   * returns the list of cart items for the currently logged in user
   */
  fun getCartItemsList(activity: Activity) {
    _fireStore.collection(Constants.CART_ITEMS)
      .whereEqualTo(Constants.USER_ID, getCurrentUserId())
      .get()
      .addOnSuccessListener {

        val cartItems = ArrayList<CartItem>()

        for (item in it.documents) {
          val cartItem = item.toObject(CartItem::class.java)!!

          cartItem.id = item.id

          cartItems.add(cartItem)
        }

        when (activity) {
          is CartListActivity -> {
            activity.handleSuccessCartItemsList(cartItems)
          }
        }

      }
      .addOnFailureListener {
        Log.e(activity.javaClass.simpleName, "Error getting the product details", it)

        when (activity) {
          is CartListActivity -> {
            activity.hideLoadingProgressDialog()
          }
        }
      }
  }


  fun getAllProductsList(activity: CartListActivity) {
    _fireStore.collection(Constants.PRODUCTS)
      .get()
      .addOnSuccessListener {

        val products = ArrayList<Product>()

        for (item in it.documents) {
          val product = item.toObject(Product::class.java)!!
          product.id = item.id

          products.add(product)
        }

        activity.handleGetAllProductsListSuccess(products)
      }
      .addOnFailureListener {
        Log.e(activity.javaClass.simpleName, "Error getting the product details", it)
        activity.hideLoadingProgressDialog()
      }
  }


  fun deleteItemFromCart(context: Context, cartId: String) {

    _fireStore.collection(Constants.CART_ITEMS)
      .document(cartId)
      .delete()
      .addOnSuccessListener {

        when (context) {
          is CartListActivity -> {
            context.handleCartItemDeletedSuccess()
          }
        }

      }
      .addOnFailureListener {
        Log.e(context.javaClass.simpleName, "Error getting the product details", it)

        when (context) {
          is CartListActivity -> {
            context.hideLoadingProgressDialog()
          }
        }
      }
  }
}