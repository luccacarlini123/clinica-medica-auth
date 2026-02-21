package br.com.mouzetech.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TipoUsuario {

	ADMIN(0, "Admin"),
	PROFISSIONAL(1, "Profissional"),
	RECEPCIONISTA(2, "Recepcionista"),
	PACIENTE(3, "Paciente");

	private int codigo;
	private String descricao;
}