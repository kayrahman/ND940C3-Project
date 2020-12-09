package com.udacity

import android.animation.*
import android.animation.ValueAnimator.RESTART
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity.START
import android.view.View
import androidx.core.content.withStyledAttributes
import org.xmlpull.v1.XmlPullParser.TEXT
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var currentWidth = 0F
    private var currentSweepAngle = 0F

    private var btnBgColor = 0
    private var btnTextColor = 0
    private var circleColor = 0
    private var circleWidth = 0f

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
            when(new){
                is ButtonState.Clicked ->{
                    isClickable = false
                }
                is ButtonState.Loading ->{
                    animateButton(true)
                }

                is ButtonState.Completed ->{
                    isClickable = true
                }
            }
    }

    init
    {
        isClickable = true

        val a = context.theme.obtainStyledAttributes(attrs,
        R.styleable.LoadingButton,
                0,0
                )

        val bc = a.getColorStateList(R.styleable.LoadingButton_btnBackgroundColor)
        btnBgColor = bc?.defaultColor ?: Color.parseColor("#3e917e")

        val tc = a.getColorStateList(R.styleable.LoadingButton_btnTextColor)
        btnTextColor = tc?.defaultColor ?: Color.parseColor("#FEFEFE")

        val cc = a.getColorStateList(R.styleable.LoadingButton_circleColor)
        circleColor = cc?.defaultColor?: Color.parseColor("#db642a")

        circleWidth = a.getDimension(R.styleable.LoadingButton_circleWidth,30f)


    }

    var isDownloading = false


    fun animateButton(isTrue : Boolean){

          isDownloading = true

        val backgroundAnimator = ObjectAnimator.ofFloat(0F,widthSize.toFloat())
        backgroundAnimator.addUpdateListener {
            currentWidth = it.animatedValue as Float
            invalidate()
            //  Log.d("update_listener", it.animatedValue.toString())
        }

        val progressBarAnimator = ValueAnimator.ofFloat(0f,360f)
        progressBarAnimator.addUpdateListener {
            currentSweepAngle = it.animatedValue as Float
        }


        val set = AnimatorSet()
        set.playTogether(backgroundAnimator, progressBarAnimator)
        set.duration = (Math.random() * 2500 + 500).toLong()


        // When the animation is done, remove the created view from the container
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
               // isDownloading = false

                if(buttonState != ButtonState.Completed){
                    set.start()
                    invalidate()
                }else{
                    isDownloading = false
                    invalidate()
                }
            }
        })
        if(isTrue) {
            // Start the animation
            set.start()
        }else{
            set.cancel()
        }
        //  backgroundAnimator.start()

    }

    override fun performClick(): Boolean {
       super.performClick()
        return true
    }


    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
       // color = resources.getColor(R.color.color_light_green)
        color = btnBgColor

    }
    private val downloadPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = resources.getColor(R.color.color_dark_green)

    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = btnTextColor
        textAlign = Paint.Align.CENTER
        textSize = 40F
      //  typeface = Typeface.SANS_SERIF

    }
    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
       // style = Paint.Style.FILL
        color = circleColor

    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        Log.d("downloading","drawing")

        val rect = Rect(0,0,widthSize,heightSize)
        canvas?.drawRect(rect,paint)
      //  canvas?.drawRect(0f,0f,widthSize.toFloat(),heightSize.toFloat(),paint)


        val textDownload = "Download"
        val textDownloading = "We are downloading"
        if(!isDownloading) {
            canvas?.drawText(textDownload, rect.exactCenterX(), rect.exactCenterY(), textPaint)
        }

        if(isDownloading) {
            val rectDowload = Rect(0, 0, currentWidth.toInt(), heightSize)
          //  downloadPaint.color = resources.getColor(R.color.color_dark_yellow)
            canvas?.drawRect(rectDowload, downloadPaint)

            val text_bound = Rect()
            textPaint.getTextBounds(textDownloading,0,textDownloading.length,text_bound)
            val text_width = text_bound.width()

           // val posx = rect.width()/2 - text_bound.width()/2 - text_bound.left
            val posy = rect.height()/2 +text_bound.height()/2 - text_bound.bottom

            canvas?.drawText(textDownloading, rect.exactCenterX(), posy.toFloat(), textPaint)


            val lposition = (rect.width() - text_width)/2
            canvas?.drawArc(
                     lposition+ text_width.toFloat()+15f,
                    rect.height()/2-15f,
                     lposition+text_width.toFloat()+circleWidth,
                    rect.height()/2.toFloat()+15f,
                    -90F,currentSweepAngle,
                    true,arcPaint)

            if(currentWidth.toInt() == widthSize){
                isDownloading = false
                invalidate()
            }
        }

    }



    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    fun updateDownloadStatus(loading_state: ButtonState) {
        buttonState = loading_state
    }

}