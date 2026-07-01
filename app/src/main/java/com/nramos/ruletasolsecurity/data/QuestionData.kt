package com.nramos.ruletasolsecurity.data

import com.nramos.ruletasolsecurity.R

object QuestionData {

    fun getCategories(): List<Category> {
        return listOf(
            Category(
                id = 1,
                name = "Conocimiento de SOL SECURITY",
                color = R.color.brand_yellow,
                questions = listOf(
                    Question(
                        id = 1,
                        text = "¿Cuál es el principal objetivo del servicio de seguridad que brinda nuestra empresa?",
                        type = QuestionType.MULTIPLE_CHOICE,
                        options = listOf(
                            "a) Solo controlar accesos",
                            "b) Proteger personas, bienes y operaciones",
                            "c) Revisar documentos únicamente"
                        ),
                        correctAnswer = "b",
                        explanation = "El objetivo principal es proteger personas, bienes y operaciones en su totalidad."
                    ),
                    Question(
                        id = 2,
                        text = "Menciona una función principal que realiza un agente de seguridad dentro de las instalaciones.",
                        type = QuestionType.FREE_RESPONSE,
                        options = null,
                        correctAnswer = "Control de accesos, prevención de riesgos, vigilancia, respuesta ante incidentes, etc.",
                        explanation = "Las funciones principales incluyen control de accesos, vigilancia, prevención de riesgos y respuesta ante incidentes."
                    ),
                    Question(
                        id = 3,
                        text = "La seguridad solo actúa cuando ocurre un incidente.",
                        type = QuestionType.TRUE_FALSE,
                        options = listOf("Verdadero", "Falso"),
                        correctAnswer = "Falso",
                        explanation = "La seguridad también previene riesgos, no solo actúa cuando ocurre un incidente."
                    ),
                    Question(
                        id = 4,
                        text = "¿Cuántos años de alianza comercial ininterrumpida tiene SOL SECURITY con TASA en el servicio de FLOTA?",
                        type = QuestionType.FREE_RESPONSE,
                        options = null,
                        correctAnswer = "19 años",
                        explanation = "SOL SECURITY tiene 19 años de alianza comercial ininterrumpida con TASA."
                    )
                )
            ),
            Category(
                id = 2,
                name = "Seguridad en la Cadena Logística Internacional",
                color = R.color.brand_dark_blue,
                questions = listOf(
                    Question(
                        id = 5,
                        text = "¿Qué busca proteger la seguridad en la cadena logística internacional?",
                        type = QuestionType.MULTIPLE_CHOICE,
                        options = listOf(
                            "a) Solo los vehículos",
                            "b) Solo la mercadería",
                            "c) Personas, procesos, información y mercancías"
                        ),
                        correctAnswer = "c",
                        explanation = "Protege personas, procesos, información y mercancías."
                    ),
                    Question(
                        id = 6,
                        text = "¿Cuál de estos riesgos afecta la cadena logística?",
                        type = QuestionType.MULTIPLE_CHOICE,
                        options = listOf(
                            "a) Robo de carga",
                            "b) Contaminación de mercancía",
                            "c) Acceso no autorizado",
                            "d) Todas las anteriores"
                        ),
                        correctAnswer = "d",
                        explanation = "Todos estos riesgos afectan la cadena logística."
                    ),
                    Question(
                        id = 7,
                        text = "La seguridad en la cadena logística es responsabilidad únicamente del área de seguridad.",
                        type = QuestionType.TRUE_FALSE,
                        options = listOf("Verdadero", "Falso"),
                        correctAnswer = "Falso",
                        explanation = "La seguridad es responsabilidad de todos, no solo del área de seguridad."
                    ),
                    Question(
                        id = 8,
                        text = "¿Qué significa reportar una actividad sospechosa a tiempo?",
                        type = QuestionType.MULTIPLE_CHOICE,
                        options = listOf(
                            "a) Generar problemas",
                            "b) Prevenir incidentes y pérdidas",
                            "c) Retrasar el trabajo"
                        ),
                        correctAnswer = "b",
                        explanation = "Reportar a tiempo previene incidentes y pérdidas."
                    ),
                    Question(
                        id = 9,
                        text = "Si observas a una persona desconocida cerca de tu área de trabajo o en una zona restringida ¿qué debes hacer?",
                        type = QuestionType.MULTIPLE_CHOICE,
                        options = listOf(
                            "a) Ignorarla",
                            "b) Acercarte y confrontarla",
                            "c) Reportar la situación al área responsable"
                        ),
                        correctAnswer = "c",
                        explanation = "Debes reportar la situación al área responsable."
                    ),
                    Question(
                        id = 10,
                        text = "Una credencial de acceso debe prestarse a un compañero si la olvidó.",
                        type = QuestionType.TRUE_FALSE,
                        options = listOf("Verdadero", "Falso"),
                        correctAnswer = "Falso",
                        explanation = "Nunca se debe prestar la credencial de acceso, es personal e intransferible."
                    )
                )
            ),
            Category(
                id = 3,
                name = "Prevención frente a la delincuencia",
                color = R.color.brand_yellow,
                questions = listOf(
                    Question(
                        id = 11,
                        text = "Si recibes una llamada sospechosa solicitando información confidencial, ¿qué debes hacer?",
                        type = QuestionType.MULTIPLE_CHOICE,
                        options = listOf(
                            "a) Brindar la información rápidamente",
                            "b) Verificar la identidad y reportar si es sospechosa",
                            "c) Colgar inmediatamente sin informar a nadie"
                        ),
                        correctAnswer = "b",
                        explanation = "Debes verificar la identidad y reportar si es sospechosa."
                    ),
                    Question(
                        id = 12,
                        text = "Compartir tu ubicación en tiempo real en redes sociales puede representar un riesgo de seguridad.",
                        type = QuestionType.TRUE_FALSE,
                        options = listOf("Verdadero", "Falso"),
                        correctAnswer = "Verdadero",
                        explanation = "Compartir ubicación en tiempo real expone tu ubicación y puede ser un riesgo de seguridad."
                    ),
                    Question(
                        id = 13,
                        text = "¿Cuál de estas acciones ayuda a prevenir robos?",
                        type = QuestionType.MULTIPLE_CHOICE,
                        options = listOf(
                            "a) Dejar objetos de valor visibles",
                            "b) Compartir claves con compañeros",
                            "c) Mantener medidas de seguridad y estar alerta"
                        ),
                        correctAnswer = "c",
                        explanation = "Mantener medidas de seguridad y estar alerta ayuda a prevenir robos."
                    ),
                    Question(
                        id = 14,
                        text = "¿Qué es una señal de posible intento de fraude o engaño?",
                        type = QuestionType.MULTIPLE_CHOICE,
                        options = listOf(
                            "a) Solicitud urgente de información confidencial",
                            "b) Comunicación formal y verificada",
                            "c) Información enviada por canales autorizados"
                        ),
                        correctAnswer = "a",
                        explanation = "Una solicitud urgente de información confidencial es señal de posible fraude."
                    ),
                    Question(
                        id = 15,
                        text = "Mantener puertas o accesos restringidos abiertos facilita la seguridad.",
                        type = QuestionType.TRUE_FALSE,
                        options = listOf("Verdadero", "Falso"),
                        correctAnswer = "Falso",
                        explanation = "Mantener accesos restringidos abiertos facilita el acceso no autorizado y pone en riesgo la seguridad."
                    ),
                    Question(
                        id = 16,
                        text = "¿Cuál es la mejor acción si encuentras un objeto abandonado en una zona no habitual?",
                        type = QuestionType.MULTIPLE_CHOICE,
                        options = listOf(
                            "a) Revisarlo personalmente",
                            "b) Moverlo a otra zona",
                            "c) Reportarlo y seguir el protocolo establecido"
                        ),
                        correctAnswer = "c",
                        explanation = "Debes reportarlo y seguir el protocolo establecido."
                    ),
                    Question(
                        id = 17,
                        text = "¿Qué medida ayuda a prevenir ser víctima de delincuentes en la vía pública?",
                        type = QuestionType.MULTIPLE_CHOICE,
                        options = listOf(
                            "a) Usar el celular mientras caminas sin prestar atención",
                            "b) Mantener atención al entorno y evitar exhibir objetos de valor",
                            "c) Llevar dinero visible"
                        ),
                        correctAnswer = "b",
                        explanation = "Mantener atención al entorno y evitar exhibir objetos de valor ayuda a prevenir ser víctima de delincuentes."
                    ),
                    Question(
                        id = 18,
                        text = "Complete la frase: 'La seguridad es responsabilidad de _______'.",
                        type = QuestionType.FREE_RESPONSE,
                        options = null,
                        correctAnswer = "Todos",
                        explanation = "La seguridad es responsabilidad de todos, cada persona juega un rol importante."
                    )
                )
            )
        )
    }
}