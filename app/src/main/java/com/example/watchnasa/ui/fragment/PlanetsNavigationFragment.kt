package com.example.watchnasa.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.watchnasa.R
import com.example.watchnasa.databinding.FragmentPlanetsNavBinding

class PlanetsNavigationFragment: Fragment() {

    private var _binding: FragmentPlanetsNavBinding? = null
    private val binding: FragmentPlanetsNavBinding
    get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanetsNavBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        launchFragment(EarthFragment.newInstance())

        binding.planetsBottomNav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.action_earth -> {
                    launchFragment(EarthFragment.newInstance())
                    true
                }
                R.id.action_mars -> {
                    launchFragment(MarsFragment.newInstance())
                    true
                }
                R.id.action_solar -> {
                    launchFragment(SolarFragment.newInstance())
                    true
                }
                else -> false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun launchFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.planet_container, fragment, "")
            .commit()
    }

    companion object {
        fun newInstance() = PlanetsNavigationFragment()
    }
}