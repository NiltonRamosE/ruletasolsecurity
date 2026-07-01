package com.nramos.ruletasolsecurity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.nramos.ruletasolsecurity.databinding.ActivityMainBinding

/**
 * Pantalla principal de la app.
 *
 * Muestra el título de la app, el logo de la marca y el botón "COMENZAR".
 * Al pulsar el botón se lanza una animación simple de rotación sobre el logo
 * (simulando el giro de la ruleta) y, al finalizar, se navega hacia
 * [GameActivity].
 */
class MainActivity : AppCompatActivity() {

    // ViewBinding para acceder a las vistas del layout de forma segura y sin findViewById.
    private lateinit var binding: ActivityMainBinding

    companion object {
        // Duración de la animación de giro en milisegundos (2 segundos).
        private const val ROTATION_DURATION_MS = 2000L

        // Número de vueltas completas que dará el logo durante la animación.
        private const val ROTATION_DEGREES = 360f * 3 // 3 vueltas completas
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializamos ViewBinding e inflamos el layout.
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupComenzarButton()
    }

    /**
     * Configura el listener del botón "COMENZAR".
     * Al hacer clic: deshabilita el botón (evita doble clic), lanza la
     * animación de rotación del logo y, al terminar, navega a GameActivity.
     */
    private fun setupComenzarButton() {
        binding.btnComenzar.setOnClickListener {
            binding.btnComenzar.isEnabled = false
            animateLogoRotationAndNavigate()
        }
    }

    /**
     * Anima el logo con una rotación de 2 segundos usando ObjectAnimator.
     * Una vez finalizada la animación, navega hacia GameActivity.
     */
    private fun animateLogoRotationAndNavigate() {
        val rotationAnimator = ObjectAnimator.ofFloat(
            binding.ivLogo,
            "rotation",
            0f,
            ROTATION_DEGREES
        ).apply {
            duration = ROTATION_DURATION_MS
            interpolator = AccelerateDecelerateInterpolator()
        }

        rotationAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                navigateToGameActivity()
            }
        })

        rotationAnimator.start()
    }

    /**
     * Navega hacia GameActivity (pendiente de implementación).
     */
    private fun navigateToGameActivity() {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
        // No hacemos finish() aquí para permitir volver a la pantalla principal
        // con el botón "atrás" si se desea. Si prefieres que MainActivity se
        // cierre al pasar a GameActivity, descomenta la siguiente línea:
        // finish()
    }
}