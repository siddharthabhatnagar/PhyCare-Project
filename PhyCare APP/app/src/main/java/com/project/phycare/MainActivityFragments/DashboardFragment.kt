package com.project.phycare.MainActivityFragments

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.values
import com.google.firebase.ktx.Firebase
import com.project.phycare.R
import com.project.phycare.TestActivity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DashboardFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        val view=inflater.inflate(R.layout.fragment_dashboard, container, false)
        val progressBar=view.findViewById<ProgressBar>(R.id.stressProgressBar)
        val stressleveltext=view.findViewById<TextView>(R.id.stressLevelText)
        val stresstestbtn=view.findViewById<Button>(R.id.stresstest)
        val firebaseAuth=Firebase.auth
        val database=FirebaseDatabase.getInstance().reference
        var istestdone =""
        database.child("users").child(firebaseAuth.currentUser!!.uid).child("istestdone").get().addOnSuccessListener {
            istestdone=it.value.toString()
            if(istestdone=="false"){
                progressBar.progress=100
//                progressBar.progressTintList= ColorStateList.valueOf(Color.parseColor("FF0000FF"))
                stressleveltext.text="You didn't attempt the stress test"
            }
            if(istestdone=="true"){
                database.child("users").child(firebaseAuth.currentUser!!.uid).child("test_results").orderByChild("timestamp")
                    .limitToLast(1)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (resultSnap in snapshot.children) {
                                val score = resultSnap.child("score").getValue(Int::class.java)
                                val timestamp = resultSnap.child("timestamp").getValue(Long::class.java)
                                val status = when {
                                    score!!.toInt() <= 9 -> "Mild"
                                    score!!.toInt() in 10..19 -> "Moderate"
                                    else -> "Severe"
                                }
                                val progress = when {
                                    score!!.toInt() <= 9 -> 33
                                    score!!.toInt() in 10..19 ->66
                                    else -> 99
                                }

                                val suggestions = when (status) {
                                    "Mild" -> "Try journaling and light exercise."
                                    "Moderate" -> "Consider meditation and limiting screen time."
                                    "Severe" -> "Seek professional help and engage in daily meditation."
                                    else -> ""
                                }
                                progressBar.progress=progress
                                stressleveltext.text=status
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("Firebase", "Failed to read latest result: ${error.message}")
                        }
                    })
            }
        }

        stresstestbtn.setOnClickListener {
          startActivity(Intent(requireContext(),TestActivity::class.java))
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DashboardFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DashboardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}