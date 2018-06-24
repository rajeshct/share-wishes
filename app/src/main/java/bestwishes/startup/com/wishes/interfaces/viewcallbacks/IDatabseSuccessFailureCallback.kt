package bestwishes.startup.com.wishes.interfaces.viewcallbacks

/**
 * Created by rajesh on 10/12/17.
 */
interface IDatabseSuccessFailureCallback {
    fun onSuccess(data: Any)
    fun onError(message: String)
}