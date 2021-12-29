package com.example.watchnasa.ui.fragment.apod

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.example.watchnasa.R
import com.example.watchnasa.databinding.FragmentApodBinding
import com.example.watchnasa.repository.dto.ApodResponseData
import com.example.watchnasa.ui.MainActivity
import com.example.watchnasa.ui.fragment.PlanetsNavigationFragment
import com.example.watchnasa.utils.hide
import com.example.watchnasa.utils.show
import com.example.watchnasa.utils.showErrorDialog
import com.example.watchnasa.viewmodel.ApodState
import com.example.watchnasa.viewmodel.ApodViewModel
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.util.*

class APODFragment : Fragment() {

    private val viewModel: ApodViewModel by lazy {
        ViewModelProvider(this)[ApodViewModel::class.java]
    }

    private var _binding: FragmentApodBinding? = null
    private val binding: FragmentApodBinding
        get() {
            return _binding!!
        }

    // флаг нахождения на главном фрагменте
    private var isMain = true

    // переменная состояния BottomSheet
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        bottomAppbarInit()

        val observer = Observer<ApodState> { renderData(it) }
        viewModel.getLiveData().observe(viewLifecycleOwner, observer)
        viewModel.getAPODFromServer(0)

        // обработчик кнопки Википедии, открывает браузер с запросом на сайт Википедии
        wikiTextInputLayout.setEndIconOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(getString(R.string.wiki_url) + wikiEditText.text.toString())
            })
        }

        // изменяем масштаб APOD-картинки отслеживая состояние BottomSheet
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> moveFabToEnd()
                    BottomSheetBehavior.STATE_COLLAPSED -> moveFabToCenter()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                apodImageView.scaleX = 1 + slideOffset
                apodImageView.scaleY = 1 + slideOffset
            }
        })

        // при выборе одного из чипов загружаем соответсвующую картинку дня
        apodChipGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.before_yesterday_chip -> viewModel.getAPODFromServer(2)
                R.id.yesterday_chip -> viewModel.getAPODFromServer(1)
                R.id.today_chip -> viewModel.getAPODFromServer(0)
            }
        }

        // обработчик нажатия на кнопку календаря
        apodCalendarButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            apodChipGroup.clearCheck()
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    viewModel.getAPODByDateFromServer(calendar.time)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // управление кнопкой fab, кнопками BottomAppbar и состоянием BottomSheet
        apodFab.setOnClickListener {
            if (isMain) {
                moveFabToEnd()
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                moveFabToCenter()
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_bottom_appbar, menu)
    }

    // обработчик кнопок нижнего меню
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_planets_photo -> {
                parentFragmentManager.beginTransaction()
                    .add(R.id.container, PlanetsNavigationFragment.newInstance(), "")
                    .addToBackStack("PlanetsNavigationFragment")
                    .commit()
            }
            android.R.id.home -> {
                BottNavDrawingFragment().show(requireActivity().supportFragmentManager, "")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    // иницифлизация нижнего меню
    private fun bottomAppbarInit() {
        val context = activity as MainActivity
        context.setSupportActionBar(binding.apodBottomAppbar)
        setHasOptionsMenu(true)
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.bottomSheetContainer)
    }

    private fun renderData(apodState: ApodState) = with(binding) {
        when (apodState) {
            is ApodState.Loading -> {
                apodProgressBar.show()
            }
            is ApodState.Success -> {
                apodProgressBar.hide()
                showData(apodState.apodData)
            }
            is ApodState.Error -> {
                apodProgressBar.hide()
                showErrorDialog(requireContext()) { _, _ -> viewModel.getAPODFromServer(0) }
            }
        }
    }

    // метод вывода данных, полученных с сервера, на экран
    private fun showData(apodData: ApodResponseData) = with(binding) {
        if (apodData.mediaType == "image") {
            apodImageView.show()
            apodVideoButton.hide()
            apodImageView.load(apodData.url) {
                lifecycle(this@APODFragment)
                error(R.drawable.ic_baseline_broken_image_96)
                placeholder(R.drawable.ic_baseline_wallpaper_96)
            }
        } else {
            apodImageView.hide()
            apodVideoButton.show()
            apodVideoButton.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(apodData.url)
                })
            }
        }
        bottomSheet.bottomSheetTitle.text = apodData.title
        bottomSheet.bottomSheetTextView.text = apodData.explanation
        val behavior = BottomSheetBehavior.from(bottomSheet.bottomSheetContainer)
        behavior.halfExpandedRatio = 0.25f
        behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
    }

    // двигаем кнопку fab враво и меняем кнопки меню
    private fun moveFabToEnd() = with(binding) {
        isMain = false
        apodBottomAppbar.navigationIcon = null
        apodBottomAppbar.replaceMenu(R.menu.menu_bottom_search)
        apodBottomAppbar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
        apodFab.setImageResource(R.drawable.ic_baseline_arrow_back_ios_24)
    }

    // возвращаем кнопку fab в центр и меняем кнопки меню
    private fun moveFabToCenter() = with(binding) {
        isMain = true
        apodBottomAppbar.navigationIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_menu_24)
        apodBottomAppbar.replaceMenu(R.menu.menu_bottom_appbar)
        apodBottomAppbar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
        apodFab.setImageResource(R.drawable.ic_baseline_add_48)
    }


    companion object {
        fun newInstance() = APODFragment()
    }
}