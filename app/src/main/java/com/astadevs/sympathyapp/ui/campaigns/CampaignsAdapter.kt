package com.astadevs.sympathyapp.ui.campaigns

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.astadevs.sympathyapp.R
import com.astadevs.sympathyapp.base.BaseAdapter
import com.astadevs.sympathyapp.databinding.ItemCampaignsListBinding
import com.astadevs.sympathyapp.data.model.CampaignObject
import com.squareup.picasso.Picasso

class CampaignsAdapter :
    BaseAdapter.ListRecyclerAdapter<CampaignObject, CampaignsAdapter.UserCampaignViewHolder>(
        object : DiffUtil.ItemCallback<CampaignObject>() {
            override fun areItemsTheSame(
                oldItem: CampaignObject,
                newItem: CampaignObject
            ) =
                oldItem.hashCode() == newItem.hashCode()

            override fun areContentsTheSame(
                oldItem: CampaignObject,
                newItem: CampaignObject
            ) =
                oldItem.toString() == newItem.toString()
        }
    ) {

    inner class UserCampaignViewHolder(
        private val binding: ItemCampaignsListBinding
    ) : BaseAdapter.BaseViewHolder<CampaignObject>(binding) {
        override fun bind(item: CampaignObject) {
            with(binding) {
                tvCampaignTitle.text = item.campaignTitle
                val amount = "Amount Requited = " + item.campaignAmount
                tvCampaignAmount.text = amount
                tvCampaignDescription.text = item.campaignDescription

                Picasso.get()
                    .load(item.campaignImageURL)
                    .placeholder(R.drawable.stub_image)
                    .error(R.drawable.stub_image)
                    .into(ivCampaignImage)

                onItemClickCallback?.let { clickCallback ->
                    itemView.setOnClickListener {
                        clickCallback.onItemClick(
                            clickTag = "itemView",
                            item = item,
                            position = adapterPosition
                        )
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = UserCampaignViewHolder(
        ItemCampaignsListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: UserCampaignViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}