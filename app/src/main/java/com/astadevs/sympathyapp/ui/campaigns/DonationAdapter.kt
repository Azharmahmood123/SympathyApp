package com.astadevs.sympathyapp.ui.campaigns

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.astadevs.sympathyapp.base.BaseAdapter
import com.astadevs.sympathyapp.databinding.ItemDonationListBinding
import com.astadevs.sympathyapp.data.model.CampaignDonationObject

class DonationAdapter :
    BaseAdapter.ListRecyclerAdapter<CampaignDonationObject, DonationAdapter.DonationViewHolder>(
        object : DiffUtil.ItemCallback<CampaignDonationObject>() {
            override fun areItemsTheSame(
                oldItem: CampaignDonationObject,
                newItem: CampaignDonationObject
            ) =
                oldItem.hashCode() == newItem.hashCode()

            override fun areContentsTheSame(
                oldItem: CampaignDonationObject,
                newItem: CampaignDonationObject
            ) =
                oldItem.toString() == newItem.toString()
        }
    ) {

    inner class DonationViewHolder(
        private val binding: ItemDonationListBinding
    ) : BaseAdapter.BaseViewHolder<CampaignDonationObject>(binding) {
        override fun bind(item: CampaignDonationObject) {
            with(binding) {
                tvUserId.text = item.userID
                tvCampaignId.text = item.campaignID
                val amount = "Amount Received = " + item.requestAmount
                tvDonationAmount.text = amount

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DonationViewHolder(
        ItemDonationListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: DonationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}