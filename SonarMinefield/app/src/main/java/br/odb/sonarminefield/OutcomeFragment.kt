package br.odb.sonarminefield

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import br.odb.sonarminefield.databinding.OutcomeFragmentBinding

class OutcomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        if (outcome) {
            binding.imageView.setImageResource(R.drawable.minespokedvg)
            binding.tvOutcome.text = "Game Over!"
        } else {
            binding.imageView.setImageResource(R.drawable.blanksvg)
            binding.tvOutcome.text = "Victory!"
        }

        Looper.myLooper()?.let { it1 ->
            Handler(it1).postDelayed({
                try {
                    if (isVisible && !isRemoving && !isDetached) {
                        findNavController().popBackStack()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, 5000)
        }


        return binding.root
    }
}