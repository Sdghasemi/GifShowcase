package com.hirno.gif.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.hirno.gif.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

abstract class BaseFragment<Binding : ViewBinding> : Fragment() {

    protected val sharedViewModel: MainViewModel by activityViewModel()

    private var _binding: Binding? = null
    protected val binding get() = _binding!!

    abstract fun onCreateBinding(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Binding
    final override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = onCreateBinding(inflater, container, savedInstanceState)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}