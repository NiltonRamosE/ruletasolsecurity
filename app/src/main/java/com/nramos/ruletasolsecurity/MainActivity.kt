package com.nramos.ruletasolsecurity

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.nramos.ruletasolsecurity.databinding.ActivityMainBinding
import com.nramos.ruletasolsecurity.viewmodel.RuletaViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: RuletaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[RuletaViewModel::class.java]

        setupObservers()
        setupComenzarButton()

        // Cargar datos al iniciar
        viewModel.loadCategories()
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            // Mostrar/ocultar loading si lo deseas
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Snackbar.make(binding.root, "Error: $it", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun setupComenzarButton() {
        binding.btnComenzar.setOnClickListener {
            binding.btnComenzar.isEnabled = false

            // Verificar si hay datos cargados
            viewModel.categories.value?.let { categories ->
                if (categories.isNotEmpty()) {
                    animateLogoRotationAndNavigate()
                } else {
                    Snackbar.make(binding.root, "Cargando datos...", Snackbar.LENGTH_SHORT).show()
                    binding.btnComenzar.isEnabled = true
                }
            } ?: run {
                // Si no hay datos, cargarlos primero
                Snackbar.make(binding.root, "Cargando datos...", Snackbar.LENGTH_SHORT).show()
                viewModel.loadCategories()
                binding.btnComenzar.isEnabled = true
            }
        }
    }

    private fun animateLogoRotationAndNavigate() {
        val rotationAnimator = ObjectAnimator.ofFloat(
            binding.ivLogo,
            "rotation",
            0f,
            360f * 3
        ).apply {
            duration = 2000
            interpolator = AccelerateDecelerateInterpolator()
        }

        rotationAnimator.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                super.onAnimationEnd(animation)
                navigateToGameActivity()
            }
        })

        rotationAnimator.start()
    }

    private fun navigateToGameActivity() {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }
}