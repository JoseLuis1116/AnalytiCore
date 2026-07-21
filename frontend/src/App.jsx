import { useState } from 'react'
import './App.css'

const API_URL = import.meta.env.VITE_API_URL || '/api'

function App() {
  const [texto, setTexto] = useState('')
  const [resultado, setResultado] = useState(null)
  const [cargando, setCargando] = useState(false)
  const [error, setError] = useState('')

  async function manejarEnvio(evento) {
    evento.preventDefault()

    if (!texto.trim()) {
      return
    }

    setCargando(true)
    setError('')
    setResultado(null)

    try {
      const respuesta = await fetch(`${API_URL}/jobs`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          text: texto,
        }),
      })

      const datos = await respuesta.json()

      if (!respuesta.ok) {
        throw new Error(
          datos.detail || 'No se pudo realizar el análisis'
        )
      }

      setResultado(datos)
    } catch (errorSolicitud) {
      setError(errorSolicitud.message)
    } finally {
      setCargando(false)
    }
  }

  function limpiarFormulario() {
    setTexto('')
    setResultado(null)
    setError('')
  }

  return (
    <main>
      <section>
        <h1>AnalytiCore</h1>

        <p>
          Plataforma para analizar el sentimiento de un texto y extraer
          sus palabras clave.
        </p>

        <form onSubmit={manejarEnvio}>
          <label htmlFor="texto">
            Texto para analizar
          </label>

          <textarea
            id="texto"
            name="texto"
            rows="8"
            maxLength="5000"
            placeholder="Escribe aquí el texto que deseas analizar..."
            value={texto}
            onChange={(evento) => setTexto(evento.target.value)}
          />

          <p>
            Caracteres escritos: {texto.length} / 5000
          </p>

          <button
            type="submit"
            disabled={!texto.trim() || cargando}
          >
            {cargando ? 'Analizando...' : 'Analizar texto'}
          </button>
        </form>

        {error && (
          <div className="mensaje-error">
            <strong>Error:</strong> {error}
          </div>
        )}

        {resultado && (
          <div className="resultado">
            <div className="resultado-encabezado">
              <h2>Resultado del análisis</h2>

              <span className="estado">
                {resultado.status}
              </span>
            </div>

            <div className="resultado-dato">
              <span>Sentimiento</span>
              <strong>{resultado.sentiment}</strong>
            </div>

            <div className="resultado-dato">
              <span>Palabras clave</span>

              <div className="palabras-clave">
                {resultado.keywords?.map((palabra) => (
                  <span key={palabra}>
                    {palabra}
                  </span>
                ))}
              </div>
            </div>

            <button
              type="button"
              className="boton-secundario"
              onClick={limpiarFormulario}
            >
              Analizar otro texto
            </button>
          </div>
        )}
      </section>
    </main>
  )
}

export default App