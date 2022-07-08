package com.astadevs.sympathyapp.ui.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.astadevs.sympathyapp.AppSettings
import com.astadevs.sympathyapp.data.EventObserver
import com.astadevs.sympathyapp.data.model.ChatWithUserInfo
import com.astadevs.sympathyapp.databinding.FragmentChatsBinding
import com.astadevs.sympathyapp.ui.dashboard.DashboardActivity

class ChatsFragment : Fragment() {

    var onChatWithUserInfo: (ChatWithUserInfo) -> Unit = {}

    private val viewModel: ChatsViewModel by viewModels { ChatsViewModelFactory(AppSettings.userUID) }
    private lateinit var viewDataBinding: FragmentChatsBinding
    private lateinit var listAdapter: ChatsListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewDataBinding =
            FragmentChatsBinding.inflate(inflater, container, false).apply { viewmodel = viewModel }
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as DashboardActivity).setSelectedNav(3)
        setupListAdapter()
        setupObservers()
    }

    private fun setupListAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = ChatsListAdapter(viewModel)
            viewDataBinding.chatsRecyclerView.adapter = listAdapter
            viewModel.setupChats()
        } else {
            throw Exception("The viewmodel is not initialized")
        }
    }

    private fun setupObservers() {
        viewModel.selectedChat.observe(viewLifecycleOwner,
            EventObserver { navigateToChat(it) })
    }

    private fun navigateToChat(chatWithUserInfo: ChatWithUserInfo) {
        onChatWithUserInfo.invoke(chatWithUserInfo)
    }
}