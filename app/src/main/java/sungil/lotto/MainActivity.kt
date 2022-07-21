package sungil.lotto

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import sungil.lotto.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var idx = -1
    private var listLotto = arrayOfNulls<String>(30)
    private var lastSaved: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        loadPref()

        // 중복 추첨 체크에 따른 2가지 생성
        binding.btnNew.setOnClickListener {
            if (binding.cbIsSame.isChecked) {
                getLottoNumber1()
            } else {
                getLottoNumber2()
            }

            // 버튼을 눌렀다면 저장이 가능하게
            binding.btnSave.isEnabled = true
        }


        // 세이브 버튼
        binding.btnSave.setOnClickListener {

            // 화면에 올라간 text를 int형으로 배열에 넣고 저장 준비
            val lotto = IntArray(6)
            lotto[0] = binding.tvLotto1.text.toString().toInt()
            lotto[1] = binding.tvLotto2.text.toString().toInt()
            lotto[2] = binding.tvLotto3.text.toString().toInt()
            lotto[3] = binding.tvLotto4.text.toString().toInt()
            lotto[4] = binding.tvLotto5.text.toString().toInt()
            lotto[5] = binding.tvLotto6.text.toString().toInt()

            // strLotto에 번호를 넣고 번호에 끝이라면 , 넣음
            var strLotto = ""
            for (i in lotto.indices) {
                strLotto += lotto[i].toString()
                if (i != lotto.size - 1) strLotto += ","
            }

			// 방금 저장한 번호 일 경우
            if (strLotto.equals(lastSaved)) {
                Toast.makeText(this, "이미 저장했음", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            idx++

			// 저장공간이 꽉 찼을 경우
            if (idx == 30) {
                Toast.makeText(this, "저장공간이 없음", Toast.LENGTH_SHORT).show()
                idx--
                return@setOnClickListener
            }

			// 로또리스트에 idx에 따라 이번에 생성된 로또번호를 저장함
            listLotto[idx] = strLotto
            lastSaved = strLotto
            Toast.makeText(this, "저장완료!!", Toast.LENGTH_SHORT).show()
            Log.d("디버깅", strLotto)
            binding.btnShowList.isEnabled = true

			// 기기에 저장하는 함수
            savePref()
        }


		// 그동안 저장한 리스트 보여주기
        binding.btnShowList.setOnClickListener {

			// 그동안 저장한 횟수 == idx 만큼 문자열로 저장함
			var strListLotto: String = ""
            for (i in 0..idx) {
                strListLotto += listLotto[i]
                if (i != idx) strListLotto += "," // 나중에 잘라서 쓰기 위해 한줄의 번호가 끝날때 마다 , 넣음
            }

			// intent로 값 넘겨주기
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("strListLotto", strListLotto)
            startActivity(intent)
        }
    }

    // 로또 번호 리스트를 기기에 저장
    private fun savePref() {
        var strListLotto: String = ""
        // for 문으로 처음부터 끝까지 문자열에 더해서 저장 준비
        for (i in 0..idx) {
            strListLotto += listLotto[i]
            if (i != idx) strListLotto += "|" // 한 리스트가 끝나면 다시 잘라서 사용하기 위해 | 넣음
        }


		// 기기에 저장
        val sp = getSharedPreferences(KEY_PREF, Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putInt(KEY_INDEX, idx)
        editor.putString(KEY_LASTSAVED, lastSaved)
        editor.putString(KEY_LOTTO, strListLotto)
        editor.apply()
    }

	// 기기에 저장된 값 불러오기
    private fun loadPref() {
        val sp = getSharedPreferences(KEY_PREF, Context.MODE_PRIVATE)
        if (sp.contains(KEY_INDEX) && sp.getString(KEY_LASTSAVED, null).isNullOrEmpty().not()) {
            idx = sp.getInt(KEY_INDEX, -1)
            lastSaved = sp.getString(KEY_LASTSAVED, null)
            val strListlotto: String? = sp.getString(KEY_LOTTO, null)
            val arrListLotto: List<String>? = strListlotto?.split("|") // 하나의 문자열로 넣어뒀던 값을 split으로 잘라서 다시 배열로 만듦
            for (i in 0..idx) { 												 // 그리고 listLotto에 다시 저장해서 사용함
                listLotto[i] = arrListLotto?.get(i)
            }
            binding.btnShowList.isEnabled = true
        }
    }

	// 기본 설정 함수
    private fun initViews() {
        binding.btnSave.isEnabled = false
        binding.btnShowList.isEnabled = false
        binding.tvLotto1.setBackgroundResource(R.drawable.circle_white)
        binding.tvLotto2.setBackgroundResource(R.drawable.circle_white)
        binding.tvLotto3.setBackgroundResource(R.drawable.circle_white)
        binding.tvLotto4.setBackgroundResource(R.drawable.circle_white)
        binding.tvLotto5.setBackgroundResource(R.drawable.circle_white)
        binding.tvLotto6.setBackgroundResource(R.drawable.circle_white)
    }


	// 로또 번호 생성
    private fun getLottoNumber1() {
        val rnd = Random()
        val lotto = IntArray(6)
        for (i in lotto.indices) {
            lotto[i] = rnd.nextInt(45) + 1
        }
        lotto.sort()
        setTvNumber(lotto[0], binding.tvLotto1)
        setTvNumber(lotto[1], binding.tvLotto2)
        setTvNumber(lotto[2], binding.tvLotto3)
        setTvNumber(lotto[3], binding.tvLotto4)
        setTvNumber(lotto[4], binding.tvLotto5)
        setTvNumber(lotto[5], binding.tvLotto6)
    }

	// 로또 번호에 따라 번호 뒤 색상을 추가해 주는 함수
    private fun setTvNumber(i: Int, tv: TextView) {
        tv.text = i.toString()
        when (i) {
            in 1..10 -> tv.background = ContextCompat.getDrawable(this, R.drawable.circle_red)
            in 11..20 -> tv.background = ContextCompat.getDrawable(this, R.drawable.circle_green)
            in 21..30 -> tv.background = ContextCompat.getDrawable(this, R.drawable.circle_blue)
            in 31..40 -> tv.background = ContextCompat.getDrawable(this, R.drawable.circle_mint)
            else -> tv.background = ContextCompat.getDrawable(this, R.drawable.circle_purple)
        }
    }

	// 중복이 없는 로또 번호 생성
    private fun getLottoNumber2() {
        val rnd = Random()
        val lotto = IntArray(6)
		// 중복 제거
        while (true) {
            var isSame = false
            for (i in lotto.indices) {
                lotto[i] = rnd.nextInt(45) + 1
            }
            for (i in lotto.indices) {
                for (j in lotto.indices) {
                    if (i != j) {
                        if (lotto[i] == lotto[j]) {
                            isSame = true
                        }
                    }
                }
            }
            if (!isSame) {
                break
            }
        }
        lotto.sort()
        setTvNumber(lotto[0], binding.tvLotto1)
        setTvNumber(lotto[1], binding.tvLotto2)
        setTvNumber(lotto[2], binding.tvLotto3)
        setTvNumber(lotto[3], binding.tvLotto4)
        setTvNumber(lotto[4], binding.tvLotto5)
        setTvNumber(lotto[5], binding.tvLotto6)
    }

    companion object {
        const val KEY_PREF = "lotto_pref"
        const val KEY_LOTTO = "lotto_list"
        const val KEY_INDEX = "list_index"
        const val KEY_LASTSAVED = "last_saved_value"
    }
}