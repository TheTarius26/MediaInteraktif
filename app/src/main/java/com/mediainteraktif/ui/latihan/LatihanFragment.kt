package com.mediainteraktif.ui.latihan

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mediainteraktif.R
import java.io.File

class LatihanFragment : Fragment() {
    private lateinit var mStorage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var db: DocumentReference

    private lateinit var tvTitle: TextView
    private lateinit var tvContent: TextView
    private lateinit var imgAnswer: ImageView
    private lateinit var imgAdd: ImageView
    private lateinit var layoutBtn: ConstraintLayout

    private var noDocument: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_latihan, container, false)
        noDocument = activity?.intent?.getIntExtra(LATIHAN_DOC, 0)!!

        mStorage = FirebaseStorage.getInstance()
        storageReference = mStorage.reference

        mFirestore = FirebaseFirestore.getInstance()
        db = mFirestore.collection("Latihan")
            .document("Latihan$noDocument")

        tvTitle = root.findViewById(R.id.latihan_txt_number)
        tvContent = root.findViewById(R.id.latihan_txt_content)
        imgAdd = root.findViewById(R.id.latihan_add_xlx)
        imgAnswer = root.findViewById(R.id.latihan_answer_xlx)
        layoutBtn = root.findViewById(R.id.latihan_layout_xlx)

        return root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvTitle.text = "Latihan Soal $noDocument"
        getQuestion()

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            imgAdd.setOnClickListener(onBtnClickListener("add"))
//            imgAnswer.setOnClickListener(onBtnClickListener("ans"))
//        } else {
//            imgAdd.visibility = View.GONE
//            imgAnswer.visibility = View.GONE
//        }

        imgAdd.setOnClickListener(onBtnClickListener("add"))
        imgAnswer.setOnClickListener(onBtnClickListener("ans"))
    }

    //    @RequiresApi(api = 24)
    fun onBtnClickListener(type: String) = View.OnClickListener {
        val nameFile = namingPrefix(noDocument)
        when (type) {
            "ans" -> {
                val pathRef = storageReference.child("Latihan/Answer/LT ${noDocument}.xlsx")
                val localFile = File.createTempFile("${nameFile}_", ".xls")
                val filePath = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().applicationContext.packageName + ".provider",
                    localFile
                )

                pathRef.getFile(localFile)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Opening File", Toast.LENGTH_SHORT).show()
                        Log.d("Location", "Download Location ${localFile.path}")
                        val i = Intent(Intent.ACTION_VIEW)
                        val iWeb = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://bit.ly/LatihanJwb$noDocument")
                        )
                        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        i.setDataAndType(filePath, "application/vnd.ms-excel")

                        val iWeb = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://bit.ly/LatihanJwb$noDocument")
                        )

                        try {
                            startActivity(i)
                        } catch (e: ActivityNotFoundException) {
                            startActivity(iWeb)
                        }
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(activity, "Error : $exception", Toast.LENGTH_SHORT).show()
                    }
                localFile.deleteOnExit()
            }
            "add" -> {
                val pathRef = storageReference.child("Latihan/jawab/LT ${noDocument} jawab.xlsx")
                val localFile = File.createTempFile("${nameFile}_", ".xlsx")
                val filePath = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().applicationContext.packageName + ".provider",
                    localFile
                )

                pathRef.getFile(localFile)
                    .addOnSuccessListener {
                        Log.d("Location", "Download Location ${localFile.path}")
                        Toast.makeText(context, "Opening File", Toast.LENGTH_SHORT).show()
                        val i = Intent(Intent.ACTION_VIEW)
                        val iWeb = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://bit.ly/LatihanAns$noDocument")
                        )
                        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        i.setDataAndType(filePath, "application/vnd.ms-excel")

                        val iWeb = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://bit.ly/LatihanAns$noDocument")
                        )
                        try {
                            startActivity(i)
                        } catch (e: ActivityNotFoundException) {
                            startActivity(iWeb)
                        }
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(activity, "Error : $exception", Toast.LENGTH_SHORT).show()
                    }
                localFile.deleteOnExit()
            }
        }
    }

    private fun namingPrefix(number: Int): String {
        var name = ""
        when (number) {
            1 -> name = "LHOBI"
            2 -> name = "BIBI"
            3 -> name = "BGBI"
            4 -> name = "BGBNI"
            5 -> name = "KKB"
        }

        return name
    }

    fun getQuestion() {
        db.get()
            .addOnSuccessListener { task ->
                tvContent.text = task["contentLatihan"].toString()
                    .replace("_n", "\n")
            }
            .addOnFailureListener { exception ->
                Log.w("LATIHAN", "FAILED TO GET DOCUMENT", exception)
            }
    }

    companion object {
        const val LATIHAN_DOC = "latihan_extra"
    }
}