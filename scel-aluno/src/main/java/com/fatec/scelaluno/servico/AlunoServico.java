package com.fatec.scelaluno.servico;

import java.util.List;
import java.util.Optional;

import com.fatec.scelaluno.model.Aluno;

public interface AlunoServico {
	List<Aluno> consultaTodos();
	Aluno consultaPorRa(String ra);
	Optional<Aluno> consultaPorId(Long id);
	Aluno save(Aluno aluno);
	void delete (Long id);
}
