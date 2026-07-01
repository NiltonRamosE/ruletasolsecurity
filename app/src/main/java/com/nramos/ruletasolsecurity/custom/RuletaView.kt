package com.nramos.ruletasolsecurity.custom

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.nramos.ruletasolsecurity.R
import com.nramos.ruletasolsecurity.data.Category
import com.nramos.ruletasolsecurity.data.QuestionData
import kotlin.math.cos
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
    }

    private var categories: List<Category> = emptyList()
    private var currentRotation = 0f
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 40f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }
    private var selectedIndex = 0
    private var onCategorySelected: ((Category) -> Unit)? = null

    private var rotationAnimator: ValueAnimator? = null
    private var isSpinning = false

    init {
        categories = QuestionData.getCategories()
        // Aplicar sombra a la ruleta
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // Actualizar tamaño de texto según el tamaño de la vista
        textPaint.textSize = width * 0.08f
    }

    fun setOnCategorySelectedListener(listener: (Category) -> Unit) {
        onCategorySelected = listener
    }

    fun spin() {
        if (isSpinning || categories.isEmpty()) return
        isSpinning = true

        // Ángulo objetivo: mínimo 3 vueltas completas + un ángulo aleatorio
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
        // El indicador está en la parte superior (ángulo -PI/2)
        // La rotación actual rota todo, así que calculamos qué sector está en la parte superior
        val indicatorAngle = -PI / 2 // Apunta hacia arriba
        val angle = (indicatorAngle - currentRotation) % TWO_PI
        val normalizedAngle = if (angle < 0) angle + TWO_PI else angle
        val sectorAngle = TWO_PI / categories.size
        return (normalizedAngle / sectorAngle).toInt() % categories.size
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = (min(width, height) / 2f) * 0.9f

        if (categories.isEmpty()) {
            drawEmptyState(canvas, centerX, centerY)
            return
        }

        val sectorAngle = TWO_PI / categories.size
        val colors = listOf(
            Color.parseColor("#FAB323"),  // Amarillo
            Color.parseColor("#060A51"),  // Azul oscuro
            Color.parseColor("#FAB323")   // Amarillo
        )

        // Dibujar los sectores
        for (i in categories.indices) {
            val startAngle = i * sectorAngle + currentRotation
            val endAngle = startAngle + sectorAngle

            // Crear path para el sector
            val path = Path()
            path.moveTo(centerX, centerY)
            val startX = centerX + radius * cos(startAngle)
            val startY = centerY + radius * sin(startAngle)
            val endX = centerX + radius * cos(endAngle)
            val endY = centerY + radius * sin(endAngle)

            path.lineTo(startX, startY)
            path.addArc(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius,
                -Math.toDegrees(startAngle.toDouble()).toFloat(),
                Math.toDegrees(sectorAngle.toDouble()).toFloat()
            )
            path.close()

            // Color del sector
            paint.color = colors[i % colors.size]
            if (i == 1) {
                paint.color = Color.parseColor("#060A51")
            }
            canvas.drawPath(path, paint)

            // Dibujar texto en el sector
            val midAngle = startAngle + sectorAngle / 2
            val textRadius = radius * 0.6f
            val textX = centerX + textRadius * cos(midAngle)
            val textY = centerY + textRadius * sin(midAngle)

            // Rotar canvas para texto legible
            canvas.save()
            canvas.rotate(
                Math.toDegrees(midAngle.toDouble()).toFloat() + 90,
                textX,
                textY
            )
            textPaint.color = if (i == 1) Color.WHITE else Color.parseColor("#060A51")
            val text = if (categories[i].name.length > 20) {
                categories[i].name.substring(0, 18) + "..."
            } else {
                categories[i].name
            }
            // Dividir en líneas si es necesario
            val lines = text.split(" ")
            if (lines.size > 1) {
                val lineHeight = textPaint.textSize
                for (j in lines.indices) {
                    canvas.drawText(
                        lines[j],
                        textX,
                        textY - (lineHeight * (lines.size - 1) / 2) + (j * lineHeight),
                        textPaint
                    )
                }
            } else {
                canvas.drawText(text, textX, textY + textPaint.textSize / 3, textPaint)
            }
            canvas.restore()
        }

        // Borde exterior
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4f
        paint.color = Color.parseColor("#060A51")
        canvas.drawCircle(centerX, centerY, radius, paint)
        paint.style = Paint.Style.FILL

        // Dibujar divisiones entre sectores
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        paint.color = Color.WHITE
        for (i in categories.indices) {
            val angle = i * sectorAngle + currentRotation
            val x = centerX + radius * cos(angle)
            val y = centerY + radius * sin(angle)
            canvas.drawLine(centerX, centerY, x, y, paint)
        }
        paint.style = Paint.Style.FILL
    }

    private fun drawEmptyState(canvas: Canvas, centerX: Float, centerY: Float) {
        paint.color = Color.LTGRAY
        paint.style = Paint.Style.FILL
        canvas.drawCircle(centerX, centerY, min(width, height) / 3f, paint)
        paint.color = Color.GRAY
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        canvas.drawCircle(centerX, centerY, min(width, height) / 3f, paint)
        textPaint.textSize = 24f
        textPaint.color = Color.GRAY
        canvas.drawText("Sin datos", centerX, centerY + 8f, textPaint)
    }

    fun getSelectedCategory(): Category? {
        return if (selectedIndex in categories.indices) categories[selectedIndex] else null
    }
}