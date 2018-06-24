package bestwishes.startup.com.wishes.dialog

import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bestwishes.startup.com.wishes.R
import bestwishes.startup.com.wishes.interfaces.viewcallbacks.ActivityOpeartion
import kotlinx.android.synthetic.main.dialog_permission.*

/**
 * Created by rajesh on 10/12/17.
 */
class PermissionDialog : BottomSheetDialogFragment() {
    private var activityOperation: ActivityOpeartion? = null
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            activityOperation = context as ActivityOpeartion
        } catch (exp: Exception) {

        }
    }

    override fun onDetach() {
        super.onDetach()
        this.activityOperation = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_permission, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bt_submit.setOnClickListener {
            activityOperation?.let {
                dismiss()
                it.requestPermissionForApp()
            }
        }
    }
}