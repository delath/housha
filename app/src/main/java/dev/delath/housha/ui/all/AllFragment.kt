package dev.delath.housha.ui.all

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dev.delath.housha.R
import dev.delath.housha.databinding.FragmentAllBinding
import dev.delath.housha.util.applySystemBarInsets
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllFragment : Fragment() {

    private var _binding: FragmentAllBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AllViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        applySystemBarInsets(
            binding.rootLayout,
            binding.recyclerView,
            bottomNavView = requireActivity().findViewById(R.id.bottom_nav_view)
        )

        val adapter = AnimeAdapter { anime, isSubscribed ->
            viewModel.toggleSubscribe(anime, isSubscribed)
        }

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.swipeRefresh.setOnRefreshListener { viewModel.loadCurrentSeason() }

        binding.searchEditText.doAfterTextChanged { editable ->
            viewModel.setSearchQuery(editable?.toString().orEmpty())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        when (state) {
                            is UiState.Loading -> {
                                binding.swipeRefresh.isRefreshing = true
                                binding.textError.visibility = View.GONE
                            }
                            is UiState.Success -> {
                                binding.swipeRefresh.isRefreshing = false
                                binding.textError.visibility = View.GONE
                                adapter.submitList(state.animes)
                            }
                            is UiState.Error -> {
                                binding.swipeRefresh.isRefreshing = false
                                binding.textError.visibility = View.VISIBLE
                                binding.textError.text = state.message
                            }
                        }
                    }
                }
                launch {
                    viewModel.subscribedIds.collect { ids ->
                        adapter.setSubscribedIds(ids)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
