package br.odb.sonarminefield

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import br.odb.sonarminefield.databinding.TitleScreenFragmentBinding

class TitleScreenFragment : Fragment() {

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		val binding = DataBindingUtil.inflate<TitleScreenFragmentBinding>(
			inflater,
			R.layout.title_screen_fragment,
			container,
			false
		)

		binding.skMines.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
			override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
				binding.tvMines.text = "Playing with ${binding.skMines.progress + 20} mines"
			}

			override fun onStartTrackingTouch(seekBar: SeekBar?) {
			}

			override fun onStopTrackingTouch(seekBar: SeekBar?) {
			}
		})

		binding.btnPlay.setOnClickListener {
			findNavController().navigate(
				TitleScreenFragmentDirections.actionTitleScreenFragmentToBoardFragment(
					binding.skMines.progress + 20,
					50
				)
			)
		}

		return binding.root
	}
}