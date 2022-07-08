package com.astadevs.sympathyapp.ui.campaigns

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.View
import com.astadevs.sympathyapp.AppSettings
import com.astadevs.sympathyapp.base.BaseFragment
import com.astadevs.sympathyapp.base.Resource
import com.astadevs.sympathyapp.databinding.FragmentAddCampaignBinding
import com.astadevs.sympathyapp.helper.showToast
import com.astadevs.sympathyapp.data.model.CampaignObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.InputStream


class AddCampaignFragment : BaseFragment<FragmentAddCampaignBinding>(
    FragmentAddCampaignBinding::inflate
) {

    var onQuitAddCampaignFragment: () -> Unit = {}

    private val viewModel: CampaignsViewModel by viewModel()
    private lateinit var progressUploadDialog: ProgressDialog
    private lateinit var campaignObject: CampaignObject

    private var filePath: Uri? = null

    override fun initializeControls() {
        progressUploadDialog = ProgressDialog(requireContext())
        progressUploadDialog.setTitle("Uploading...")
        progressUploadDialog.setCancelable(false)
    }

    override fun attachListeners() {
        binding.btnSaveCampaign.setOnClickListener(this)
        binding.ivCloseAddCampaign.setOnClickListener(this)
        binding.btnPickImage.setOnClickListener(this)
    }

    override fun observeViewModel() {
        viewModel.addCampaignFile.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    progressUploadDialog.show()
                }
                is Resource.Error -> {
                    progressUploadDialog.dismiss()
                    val message = it.message
                    showToast(message)
                }
                is Resource.Success -> {
                    val uploadFileObject = it.data;
                    if (uploadFileObject != null) {
                        if (uploadFileObject.status == "progress") {
                            progressUploadDialog.setMessage(uploadFileObject.fileProgress)
                        } else {
                            progressUploadDialog.dismiss()
                            val filePath = uploadFileObject.uploadedFilePath
                            campaignObject.campaignImageURL = filePath
                            viewModel.addNewCampaign(campaignObject)
                        }
                    } else {
                        progressUploadDialog.dismiss()
                    }
                }
            }
        }

        viewModel.addCampaignResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog?.show()
                }
                is Resource.Error -> {
                    progressDialog?.dismiss()
                    val message = it.message
                    showToast(message)
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    showToast("Campaign created successfully")
                    onQuitAddCampaignFragment.invoke()
                }
            }
        }
    }

    override fun onClick(view: View) {
        when (view) {
            binding.btnSaveCampaign -> {
                checkSaveCampaign()
            }
            binding.ivCloseAddCampaign -> {
                onQuitAddCampaignFragment.invoke()
            }
            binding.btnPickImage -> {
                selectImage()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            // Get the Uri of data
            filePath = data.data
            if (filePath != null) {
                val imageStream: InputStream? =
                    mActivity.contentResolver?.openInputStream(filePath!!)
                val selectedImage = BitmapFactory.decodeStream(imageStream)
                binding.feedImage.setImageBitmap(selectedImage)
            }
        } else {
            filePath = null
        }
    }

    private fun selectImage() {
        // Defining Implicit Intent to mobile gallery
        val intent = Intent()
        intent.action = Intent.ACTION_PICK
        intent.type = "image/*"
        startActivityForResult(
            Intent.createChooser(intent, "Select Image from here..."),
            PICK_IMAGE_REQUEST
        )
    }

    private fun checkSaveCampaign() {
        val title = binding.etTitle.text.toString().trim()
        if (title.isBlank()) {
            showToast("Please enter title")
            return
        }
        val description = binding.etBio.text.toString().trim()
        if (description.isBlank()) {
            showToast("Please enter title")
            return
        }
        val amount = binding.etAmountRequired.text.toString().trim()
        if (amount.isBlank()) {
            showToast("Please enter title")
            return
        }
        if (filePath == null) {
            showToast("Please select file first.")
            return
        }

        campaignObject = CampaignObject(
            organizationID = AppSettings.organizationID,
            campaignTitle = title,
            campaignDescription = description,
            campaignAmount = amount
        )
        viewModel.uploadCampaignImageFile(filePath!!)
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 22
    }
}