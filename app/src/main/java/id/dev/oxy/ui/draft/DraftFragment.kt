package id.dev.oxy.ui.draft

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import id.dev.oxy.R
import id.dev.oxy.databinding.FragmentDraftBinding
import id.dev.oxy.ui.draft.adapter.DraftAdapter
import javax.inject.Inject

@AndroidEntryPoint
class DraftFragment : Fragment() {
    private var _binding: FragmentDraftBinding? = null
    private val binding get() = _binding!!

    private val draftViewModel: DraftViewModel by activityViewModels()

    @Inject
    lateinit var draftAdapter: DraftAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDraftBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvDraft.adapter = draftAdapter
        binding.rvDraft.addItemDecoration(
            DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        )

        draftAdapter.setOnItemClickCallback(object : DraftAdapter.OnItemClickCallback {
            override fun onItemClicked(id: Int) {
               draftViewModel.getDetailDraft(id)
            }
        })

        draftViewModel.savedDraft.observe(viewLifecycleOwner) {
            binding.apply {
                loadingState.apply {
                    if (it.isEmpty()) {
                        errorMsg.text = getString(R.string.emptyDraft)
                    } else {
                        draftAdapter.submitList(it)
                    }
                    errorMsg.isVisible = it.isEmpty()
                    progressBar.isVisible = false
                    retryButton.isVisible = false
                    rvDraft.isVisible = it.isNotEmpty()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}