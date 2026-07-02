package com.nramos.ruletasolsecurity.custom

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.nramos.ruletasolsecurity.data.Category
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

class RuletaView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val PI = Math.PI.toFloat()
        private const val TWO_PI = PI * 2

        private val SECTOR_COLORS = listOf(
            Color.parseColor("#FAB323"),
            Color.parseColor("#9BC1BC"),
            Color.parseColor("#1A2170")
        )
    }

    private var categories: List<Category> = emptyList()
    private var currentRotation = 0f

    private val sectorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#FFFFFF")
        strokeWidth = 2f
    }

    private val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#060A51")
    }

    private val letterPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    private val centerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#FFFFFF")
    }

    private val centerRingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#FAB323")
        strokeWidth = 3f
    }

    private var selectedIndex = 0
    private var onCategorySelected: ((Category) -> Unit)? = null

    private var rotationAnimator: ValueAnimator? = null
    private var isSpinning = false

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    // ✅ MÉTODO PARA ACTUALIZAR CATEGORÍAS
    fun setCategories(categories: List<Category>) {
        this.categories = categories
        invalidate() // Redibujar la ruleta
    }

    fun setOnCategorySelectedListener(listener: (Category) -> Unit) {
        onCategorySelected = listener
    }

    fun spin() {
        if (isSpinning || categories.isEmpty()) {
            if (categories.isEmpty()) {
                android.util.Log.e("RuletaView", "No hay categorías para girar")
            }
            return
        }
        isSpinning = true

        val extraAngle = (Math.random() * TWO_PI).toFloat()
        val targetRotation = currentRotation + (TWO_PI * 4) + extraAngle

        rotationAnimator?.cancel()
        rotationAnimator = ValueAnimator.ofFloat(currentRotation, targetRotation).apply {
            duration = 3000
            interpolator = DecelerateInterpolator(2f)
            addUpdateListener {
                currentRotation = it.animatedValue as Float
                invalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    isSpinning = false
                    selectedIndex = getSelectedCategoryIndex()
                    if (selectedIndex in categories.indices) {
                        android.util.Log.d("RuletaView", "Categoría seleccionada: ${categories[selectedIndex].name}")
                        onCategorySelected?.invoke(categories[selectedIndex])
                    }
                }
            })
            start()
        }
    }

    fun resetSpin() {
        rotationAnimator?.cancel()
        isSpinning = false
        currentRotation = 0f
        invalidate()
    }

    fun isSpinning(): Boolean = isSpinning

    private fun getSelectedCategoryIndex(): Int {
        val indicatorAngle = -PI / 2
        val angle = (indicatorAngle - currentRotation) % TWO_PI
        val normalizedAngle = if (angle < 0) angle + TWO_PI else angle
        val sectorAngle = TWO_PI / max(categories.size, 1)
        return (normalizedAngle / sectorAngle).toInt() % max(categories.size, 1)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        letterPaint.textSize = width * 0.22f
        ringPaint.strokeWidth = width * 0.02f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = (min(width, height) / 2f) * 0.88f
        val innerRadius = radius * 0.28f

        if (categories.isEmpty()) {
            drawEmptyState(canvas, centerX, centerY)
            return
        }

        sectorPaint.setShadowLayer(18f, 0f, 8f, Color.parseColor("#33000000"))
        val sectorAngle = TWO_PI / categories.size

        val rectF = RectF(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )

        // Dibujar sectores
        for (i in categories.indices) {
            val startAngle = Math.toDegrees((i * sectorAngle + currentRotation).toDouble()).toFloat()
            val sweepAngle = Math.toDegrees(sectorAngle.toDouble()).toFloat()

            sectorPaint.color = SECTOR_COLORS[i % SECTOR_COLORS.size]
            canvas.drawArc(rectF, startAngle, sweepAngle, true, sectorPaint)
        }

        sectorPaint.clearShadowLayer()

        // Separadores
        for (i in categories.indices) {
            val angle = i * sectorAngle + currentRotation
            val startX = centerX + innerRadius * cos(angle)
            val startY = centerY + innerRadius * sin(angle)
            val endX = centerX + radius * cos(angle)
            val endY = centerY + radius * sin(angle)
            canvas.drawLine(startX, startY, endX, endY, borderPaint)
        }

        // Letras
        val letters = listOf("A", "B", "C")
        for (i in categories.indices) {
            val midAngle = (i * sectorAngle + currentRotation) + sectorAngle / 2
            val textRadius = (innerRadius + radius) / 2
            val textX = centerX + textRadius * cos(midAngle)
            val textY = centerY + textRadius * sin(midAngle)

            val letter = letters.getOrElse(i) { (i + 1).toString() }
            letterPaint.color = if (i == 0) {
                Color.parseColor("#060A51")
            } else {
                Color.WHITE
            }

            canvas.drawText(
                letter,
                textX,
                textY + (letterPaint.textSize / 3f),
                letterPaint
            )
        }

        // Centro
        canvas.drawCircle(centerX, centerY, innerRadius, centerPaint)
        canvas.drawCircle(centerX, centerY, innerRadius, centerRingPaint)
        canvas.drawCircle(centerX, centerY, radius, ringPaint)
    }

    private val emptyStatePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        color = Color.GRAY
        textSize = 24f
    }

    private fun drawEmptyState(canvas: Canvas, centerX: Float, centerY: Float) {
        sectorPaint.color = Color.LTGRAY
        sectorPaint.style = Paint.Style.FILL
        canvas.drawCircle(centerX, centerY, min(width, height) / 3f, sectorPaint)
        canvas.drawText("Cargando...", centerX, centerY + 8f, emptyStatePaint)
    }

    fun getSelectedCategory(): Category? {
        return if (selectedIndex in categories.indices) categories[selectedIndex] else null
    }
}