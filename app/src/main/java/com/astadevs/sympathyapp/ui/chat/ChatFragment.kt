package com.astadevs.sympathyapp.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.astadevs.sympathyapp.databinding.FragmentChatBinding
import kotlinx.android.synthetic.main.fragment_chat.*

class ChatFragment : Fragment() {

    var onQuitChatFragment: () -> Unit = {}

    lateinit var userID: String
    lateinit var otherUserID: String
    lateinit var chatID: String

    private val viewModel: ChatViewModel by viewModels {
        ChatViewModelFactory(userID, otherUserID, chatID)
    }

    private lateinit var viewDataBinding: FragmentChatBinding
    private lateinit var listAdapter: MessagesListAdapter
    private lateinit var listAdapterObserver: RecyclerView.AdapterDataObserver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding =
            FragmentChatBinding.inflate(inflater, container, false).apply { viewmodel = viewModel }
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setHasOptionsMenu(true)

        viewDataBinding.ivCloseChat.setOnClickListener {
            onQuitChatFragment.invoke()
        }

        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupListAdapter()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                findNavController().popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupListAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapterObserver = (object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    messagesRecyclerView.scrollToPosition(positionStart)
                }
            })
            listAdapter = MessagesListAdapter(viewModel, userID)
            listAdapter.registerAdapterDataObserver(listAdapterObserver)
            viewDataBinding.messagesRecyclerView.adapter = listAdapter
        } else {
            throw Exception("The viewmodel is not initialized")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listAdapter.unregisterAdapterDataObserver(listAdapterObserver)
    }
}