package id.dev.oxy.ui.history

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import id.dev.oxy.R
import id.dev.oxy.databinding.FragmentHistoryBinding
import id.dev.oxy.ui.auth.AuthActivity
import id.dev.oxy.ui.history.adapter.HistoryAdapter
import id.dev.oxy.ui.sales.adapter.LoadingStateAdapter
import id.dev.oxy.util.getErrorMessage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val historyViewModel: HistoryViewModel by activityViewModels()

    @Inject
    lateinit var historyAdapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvHistory.apply {
            adapter = historyAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter { historyAdapter.retry() }
            )
            addItemDecoration(
                DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
            )
        }

        historyViewModel.allHistoryPaging.observe(viewLifecycleOwner) {
            historyAdapter.submitData(lifecycle, it)
        }

        historyAdapter.setOnItemClickCallback(object : HistoryAdapter.OnItemClickCallback {
            override fun onItemClicked(id: Int) {
                historyViewModel.getDetailHistory(id.toString())
            }
        })

        lifecycleScope.launch {
            historyAdapter.loadStateFlow.collectLatest {
                binding.apply {
                    loadingState.apply {
                        if (it.refresh is LoadState.Error) {
                            val errorMessage = (it.refresh as LoadState.Error).error.getErrorMessage()
                            val expiredToken = errorMessage == "Unauthenticated."

                            if (expiredToken) {
                                Intent(requireActivity(), AuthActivity::class.java).run {
                                    startActivity(this)
                                    requireActivity().finish()
                                }
                                historyViewModel.expiredToken()
                            } else {
                                errorMsg.text = StringBuilder().append(errorMessage)
                            }
                        }

                        val displayEmptyMessage = (it.refresh is LoadState.NotLoading &&
                                it.append.endOfPaginationReached && historyAdapter.itemCount == 0)

                        if (displayEmptyMessage) {
                            errorMsg.text =
                                StringBuilder().append("Belum ada riwayat untuk ditampilkan")
                        }

                        progressBar.isVisible = it.refresh is LoadState.Loading
                        retryButton.isVisible = it.refresh is LoadState.Error
                        errorMsg.isVisible = it.refresh is LoadState.Error || displayEmptyMessage
                        retryButton.setOnClickListener { historyAdapter.retry() }
                    }
                    rvHistory.isVisible =
                        it.refresh !is LoadState.Loading && it.refresh !is LoadState.Error
                }
            }
        }

        binding.swipeRefresh.apply {
            setOnRefreshListener {
                historyAdapter.refresh().run {
                    binding.swipeRefresh.isRefreshing = false
                }
            }
            setColorSchemeResources(R.color.bg_login)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}