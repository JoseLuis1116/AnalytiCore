import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { describe, expect, it } from 'vitest'
import App from './App.jsx'

describe('Aplicación AnalytiCore', () => {
  it('muestra el título principal AnalytiCore', () => {
    render(<App />)

    expect(
      screen.getByRole('heading', {
        level: 1,
        name: 'AnalytiCore',
      }),
    ).toBeInTheDocument()
  })

  it('mantiene desactivado el botón cuando no existe texto', () => {
    render(<App />)

    const boton = screen.getByRole('button', {
      name: 'Analizar texto',
    })

    expect(boton).toBeDisabled()
  })

  it('actualiza el contador cuando el usuario escribe', async () => {
    const usuario = userEvent.setup()

    render(<App />)

    const campoTexto = screen.getByLabelText('Texto para analizar')

    await usuario.type(campoTexto, 'Hola')

    expect(
      screen.getByText('Caracteres escritos: 4 / 5000'),
    ).toBeInTheDocument()
  })
})