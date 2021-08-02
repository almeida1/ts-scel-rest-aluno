package com.fatec.scelaluno.controller;

import java.util.*;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fatec.scelaluno.model.*;

import com.fatec.scelaluno.servico.AlunoServico;

@RestController
@RequestMapping("/api")
public class AlunoController {
	@Autowired
	AlunoServico servico;

	Logger logger = LogManager.getLogger(AlunoController.class);

	@GetMapping("/v1/alunos")
	public ResponseEntity<List<Aluno>> consultaTodos() {
		return ResponseEntity.ok().body(servico.consultaTodos());
	}

	@PostMapping("/v1/livros")
	public ResponseEntity<Object> create(@RequestBody @Valid Aluno aluno, BindingResult result) {
		ResponseEntity<Object> response = null;
		if (result.hasErrors()) {
			logger.info(">>>>>> 1. controller chamou servico save - erro detectado no bean");
			response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} else {
			logger.info(">>>>>> 1. controller chamou servico save sem erro no bean validation");
			
			Optional<Aluno> umAluno = Optional.ofNullable(servico.consultaPorRa(aluno.getRa()));
			if (umAluno.isPresent()) {
				logger.info(">>>>>> 1. controller chamou servico save aluno ja cadastrado");
				response = ResponseEntity.badRequest().body("ja cadastrado");
			} else {
				Optional<String> endereco = Optional.ofNullable(obtemEndereco(aluno.getCep()));
				if (endereco.isPresent()) {
					aluno.setEndereco(endereco.get());
					servico.save(aluno);
					response = new ResponseEntity<>(HttpStatus.CREATED);
				} else {
					logger.info(">>>>>> 1. controller cep nao localizado");
					response = ResponseEntity.badRequest().body("CEP não localizado.");
				}
			}

		}

		return response;
	}

	public String obtemEndereco(String cep) {
		RestTemplate template = new RestTemplate();
		String url = "https://viacep.com.br/ws/{cep}/json/";
		Endereco endereco = template.getForObject(url, Endereco.class, cep);
		logger.info(">>>>>>> 2. obtem endereco ==> " + endereco.toString());
		return endereco.getLogradouro();
	}
}
