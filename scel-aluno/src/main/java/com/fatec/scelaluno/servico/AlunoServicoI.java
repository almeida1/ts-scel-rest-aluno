package com.fatec.scelaluno.servico;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.scelaluno.model.Aluno;
import com.fatec.scelaluno.model.AlunoRepository;
@Service
public class AlunoServicoI implements AlunoServico{
	
	Logger logger = LogManager.getLogger(this.getClass());
	@Autowired
	AlunoRepository repository;
	@Override
	public List<Aluno> consultaTodos() {
		return repository.findAll();
	}

	@Override
	public Aluno consultaPorRa(String ra) {
		logger.info(">>>>>> 2. servico consulta por isbn chamado");
		return repository.findByRa(ra);
	}

	@Override
	public Optional<Aluno> consultaPorId(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Aluno save(Aluno aluno) {
		logger.info(">>>>>> 2. servico save chamado");
		return repository.save(aluno);
	}

	@Override
	public void delete(Long id) {
		// TODO Auto-generated method stub
		
	}
}
