package com.fatec.scelaluno.controller;

import java.util.*;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
	@GetMapping("v1/alunos/{ra}")
	public ResponseEntity<Aluno> findByRa(@PathVariable String ra) {
		logger.info(">>>>>> 1. controller chamou servico consulta por ra => " + ra);

		return Optional.ofNullable(servico.consultaPorRa(ra)).map(record -> ResponseEntity.ok().body(record))
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping("/v1/alunos")
	public ResponseEntity<Object> create(@RequestBody @Valid Aluno aluno, BindingResult result) {
		ResponseEntity<Object> response = null;
		if (result.hasErrors()) {
			logger.info(">>>>>> 1. controller erro detectado na entrada de dados bean validation");
			response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} else {

			Optional<Aluno> umAluno = Optional.ofNullable(servico.consultaPorRa(aluno.getRa()));
			if (umAluno.isPresent()) {
				logger.info(">>>>>> 1. controller erro ra ja cadastrado");
				response = ResponseEntity.badRequest().body("Já cadastrado.");
			} else {
				Optional<String> endereco = Optional.ofNullable(obtemEndereco(aluno.getCep()));
				if (endereco.isPresent()) {
					aluno.setEndereco(endereco.get());
					servico.save(aluno);
					response = new ResponseEntity<>(HttpStatus.CREATED);
				} else {
					logger.info(">>>>>> 1. controller erro cep nao localizado");
					response = ResponseEntity.badRequest().body("CEP não localizado.");
				}
			}

		}

		return response;
	}

	@DeleteMapping("v1/alunos/{ra}")
	public ResponseEntity<?> delete(@PathVariable String ra) {
		Optional<Aluno> umAluno = Optional.ofNullable(servico.consultaPorRa(ra));
		if (umAluno.isPresent()) {
			logger.info(">>>>>> 1. controller chamou servico delete por ra => " + ra);
			servico.delete(umAluno.get().getId());
			return ResponseEntity.ok().build();
		} else {
			logger.info(">>>>>> 1. controller chamou servico delete ra nao localizado => " + ra);
			return ResponseEntity.notFound().build();
		}

	}

	@PutMapping("v1/alunos")
	public ResponseEntity<Object> replaceAluno(@RequestBody @Valid Aluno aluno, BindingResult result) {
		ResponseEntity<Object> response = null;
		if (result.hasErrors()) {
			logger.info(">>>>>> 1. controller erro detectado na entrada de dados bean validation");
			response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} else {

			Optional<Aluno> umAluno = Optional.ofNullable(servico.consultaPorRa(aluno.getRa()));
			if (!umAluno.isPresent()) {
				logger.info(">>>>>> 1. controller erro ra não cadastrado");
				response = ResponseEntity.badRequest().body("Não cadastrado.");
			} else {
				Optional<String> endereco = Optional.ofNullable(obtemEndereco(aluno.getCep()));
				if (endereco.isPresent()) {
					aluno.setEndereco(endereco.get());
					Aluno a = umAluno.get();
					a.setNome(aluno.getNome());
					a.setEmail(aluno.getEmail());
					a.setCep(aluno.getCep());
					a.setEndereco(endereco.get());
					servico.save(a);
					response = ResponseEntity.ok(a);
				} else {
					response = ResponseEntity.badRequest().body("CEP inválido.");
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
