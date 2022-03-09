package br.odb.sonarminefield

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import br.odb.sonarminefield.databinding.TitleScreenFragmentBinding

class TitleScreenFragment : Fragment() {

//    private lateinit var viewModel: TitleScreenViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<TitleScreenFragmentBinding>(
            inflater,
            R.layout.title_screen_fragment,
            container,
            false
        )
//        viewModel = ViewModelProvider(this).get(TitleScreenViewModel::class.java)
        binding.btnPlay.setOnClickListener {
            findNavController().navigate(TitleScreenFragmentDirections.actionTitleScreenFragmentToBoardFragment())
        }

        return binding.root
    }
}