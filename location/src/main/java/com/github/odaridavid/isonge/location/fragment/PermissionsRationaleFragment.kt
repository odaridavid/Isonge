package com.github.odaridavid.isonge.location.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.github.odaridavid.isonge.location.ILocationPermissionRationaleListener
import com.github.odaridavid.isonge.location.databinding.FragmentPermissionRationaleBinding


class PermissionsRationaleFragment : Fragment() {

    private lateinit var binding: FragmentPermissionRationaleBinding
    private lateinit var permissionRationaleListener: ILocationPermissionRationaleListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPermissionRationaleBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.allowPermissionButton.apply {
            setOnClickListener {
                permissionRationaleListener.onRationaleAllowPermissionRequest()
            }
        }

        binding.denyPermissionButton.apply {
            setOnClickListener {
                permissionRationaleListener.onRationaleDenyPermissionRequest()
            }
        }

        arguments?.let { bundle ->
            binding.permissionRequestTitleTextView.text =
                getString(bundle.getInt(PERMISSION_TITLE_KEY))
            binding.permissionRequestDescriptionTextView.text =
                getString(bundle.getInt(PERMISSION_TITLE_DESCRIPTION))
        }
    }

    companion object {

        private const val PERMISSION_TITLE_KEY = "perm_title"
        private const val PERMISSION_TITLE_DESCRIPTION = "perm_description"


        @JvmStatic
        fun newInstance(
            @StringRes permissionTitle: Int,
            @StringRes permissionDescription: Int,
            permissionRationaleListener: ILocationPermissionRationaleListener
        ): PermissionsRationaleFragment {
            return PermissionsRationaleFragment().apply {
                this.permissionRationaleListener = permissionRationaleListener
                arguments = Bundle().apply {
                    putInt(PERMISSION_TITLE_KEY, permissionTitle)
                    putInt(PERMISSION_TITLE_DESCRIPTION, permissionDescription)
                }
            }
        }
    }

}