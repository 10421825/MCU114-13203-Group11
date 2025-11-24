package com.example.h5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.progressindicator.CircularProgressIndicator

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProgressFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProgressFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val vm: WorkViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_progress, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // 使用 Material 的圓形進度條與置中百分比
        val circle = view.findViewById<CircularProgressIndicator>(R.id.circleProgress)
        val percent = view.findViewById<TextView>(R.id.percentLabel)
        val status  = view.findViewById<TextView>(R.id.txt)

        // 初始先用不確定型的轉圈
        circle.isIndeterminate = true
        percent.text = "0%"

        vm.status.observe(viewLifecycleOwner) { s ->
            status.text = s
            // 當開始工作時，切換為可量測進度
            if (s.startsWith("Working") || s.startsWith("開始")) {
                if (circle.isIndeterminate) {
                    circle.isIndeterminate = false
                    circle.max = 100
                    circle.setProgressCompat(0, /*animated=*/true)
                    percent.text = "0%"
                }
            }
            // 當結束時，補滿 100%
            if (s.contains("結束") || s.contains("done", ignoreCase = true)) {
                circle.setProgressCompat(100, true)
                percent.text = "100%"
            }
        }

        vm.progress.observe(viewLifecycleOwner) { p ->
            if (!circle.isIndeterminate) {
                val clamped = p.coerceIn(0, 100)
                circle.setProgressCompat(clamped, /*animated=*/true)
                percent.text = "$clamped%"
                status.text = "Working… $clamped%"
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProgressFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProgressFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}