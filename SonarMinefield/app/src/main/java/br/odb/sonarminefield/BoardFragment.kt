package br.odb.sonarminefield

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

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

        session = GameSession()
        session!!.placeRandomMines(mines)
        session!!.clearBorders()

        val root = inflater.inflate(R.layout.fragment_board, container, false)

        gameBoard = root.findViewById(R.id.gmField)
        gameBoard!!.setSession(session)
        gameBoard!!.postInvalidate()

        return root;
    }
}