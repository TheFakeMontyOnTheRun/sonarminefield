package br.odb.sonarminefield

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.core.os.HandlerCompat.postDelayed
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

private const val ARG_NUM_MINES = "mines"

class BoardFragment : Fragment() {

    private var gameBoard: GameBoard? = null
    private var session: GameSession? = null

    private var mines: Int = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            mines = it.getString(ARG_NUM_MINES)?.toInt() ?: 20
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_board, container, false)
        val gameArgs by navArgs<BoardFragmentArgs>()
        mines = gameArgs.mines

        session = GameSession()
        session!!.placeRandomMines(mines)
        session!!.clearBorders()

        gameBoard = root.findViewById(R.id.gmField)
        gameBoard!!.setSession(session)
        gameBoard!!.postInvalidate()

        gameBoard!!.outcome.observe(viewLifecycleOwner) {
            if (it != GameBoard.GameOutcome.kPlaying) {
                Looper.myLooper()?.let { it1 ->
                    Handler(it1).postDelayed({
                        findNavController().navigate(BoardFragmentDirections.actionBoardFragmentToOutcomeFragment())
                    }, 5000)
                }
            }
        }

        root.findViewById<RadioGroup>(R.id.rdoActions).setOnCheckedChangeListener(gameBoard)

        return root;
    }
}