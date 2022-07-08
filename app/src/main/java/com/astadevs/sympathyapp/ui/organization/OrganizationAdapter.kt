package com.astadevs.sympathyapp.ui.organization

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.astadevs.sympathyapp.base.BaseAdapter
import com.astadevs.sympathyapp.databinding.ItemOrganizationListBinding
import com.astadevs.sympathyapp.data.model.OrganizationObject

class OrganizationAdapter :
    BaseAdapter.ListRecyclerAdapter<OrganizationObject, OrganizationAdapter.OrganizationViewHolder>(
        object : DiffUtil.ItemCallback<OrganizationObject>() {
            override fun areItemsTheSame(
                oldItem: OrganizationObject,
                newItem: OrganizationObject
            ) =
                oldItem.hashCode() == newItem.hashCode()

            override fun areContentsTheSame(
                oldItem: OrganizationObject,
                newItem: OrganizationObject
            ) =
                oldItem.toString() == newItem.toString()
        }
    ) {

    inner class OrganizationViewHolder(
        private val binding: ItemOrganizationListBinding
    ) : BaseAdapter.BaseViewHolder<OrganizationObject>(binding) {
        override fun bind(item: OrganizationObject) {
            with(binding) {
                tvOrganizationTitle.text = item.title
                tvOrganizationBio.text = item.bio

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = OrganizationViewHolder(
        ItemOrganizationListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: OrganizationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}