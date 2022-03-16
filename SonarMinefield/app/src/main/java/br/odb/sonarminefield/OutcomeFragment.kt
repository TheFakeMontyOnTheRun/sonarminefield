package br.odb.sonarminefield

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import br.odb.sonarminefield.databinding.OutcomeFragmentBinding

class OutcomeFragment : Fragment() {
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var outcome = false

        arguments?.let {
            outcome = it.getBoolean("isGameOver")
        }

        val binding = DataBindingUtil.inflate<OutcomeFragmentBinding>(
            inflater,
            R.layout.outcome_fragment,
            container,
            false
        )

        binding.tvOutcome.text = if (outcome) "Game Over!" else "Won!"

        return binding.root
    }
}