package sungil.lotto

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import sungil.lotto.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
	private lateinit var binding: ActivityResultBinding
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityResultBinding.inflate(layoutInflater)
		setContentView(binding.root)

		val strListLotto = intent.getStringExtra("strListLotto")
		val arrListLotto: List<String>? = strListLotto?.split(",")

		if (arrListLotto != null) {
			var cnt = 0
			for (s in arrListLotto) {
				cnt++
				if (cnt % 6 == 1) {
					val tvIndex = TextView(this)
					tvIndex.text = "\n${(cnt / 6 + 1).toString()}번째"
					binding.gridLayout.addView(tvIndex)
				}
				binding.gridLayout.addView(
					getTextViewWithNumber(s)
				)
			}
		}

		// 저장된 값을 초기화 하는 버튼
		binding.btnDeleteList.setOnClickListener {
			binding.gridLayout.removeAllViews()

			val sp = getSharedPreferences(MainActivity.KEY_PREF, Context.MODE_PRIVATE)
			val editor = sp.edit()
			editor.putInt(MainActivity.KEY_INDEX, -1)
			editor.putString(MainActivity.KEY_LASTSAVED, null)
			editor.putString(MainActivity.KEY_LOTTO, null)
			editor.apply()

		}
	}

	// 가져온 로또 번호 뒤에 색상을 추가해줌
	private fun getTextViewWithNumber(s: String): TextView {
		val tv = TextView(this)
		tv.text = s
		tv.setTextColor(Color.parseColor("#FFFFFF"))
		tv.gravity = Gravity.CENTER
		when (s.toInt()) {
			in 1..10 -> tv.setBackgroundResource(R.drawable.circle_red)
			in 11..20 -> tv.setBackgroundResource(R.drawable.circle_green)
			in 21..30 -> tv.setBackgroundResource(R.drawable.circle_blue)
			in 31..40 -> tv.setBackgroundResource(R.drawable.circle_mint)
			else -> tv.setBackgroundResource(R.drawable.circle_purple)
		}
		val lp = LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.WRAP_CONTENT,
			LinearLayout.LayoutParams.WRAP_CONTENT
		)
		lp.setMargins(2, 90, 2, 2)
		tv.layoutParams = lp
		return tv
	}

	// 홈 버튼을 만들지 않았으므로 뒤로가기 버튼을 누를때 intent로 화면을 강제 이동시킴으로 초기화
	override fun onBackPressed() {
		super.onBackPressed()
		val intent = Intent(this, MainActivity::class.java)
		startActivity(intent)
	}
}