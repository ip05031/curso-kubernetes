package com.dev.api.test.msvc.usuarios.services;

import com.dev.api.test.msvc.usuarios.clients.CursoClienteRest;
import com.dev.api.test.msvc.usuarios.models.entity.Usuario;
import com.dev.api.test.msvc.usuarios.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class UsuarioServiceImpl implements UsuarioService{

    @Autowired
    private UsuarioRepository repositorio;

    @Autowired
    private CursoClienteRest client;

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return (List<Usuario>) repositorio.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> porId(Long id) {
        return repositorio.findById(id);
    }

    @Override
    @Transactional
    public Usuario guardar(Usuario usuario) {
        return repositorio.save(usuario);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        repositorio.deleteById(id);
        client.eliminarCursoUsuarioPorId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarPorIds(Iterable<Long> ids) {
        return (List<Usuario>) repositorio.findAllById(ids);

    }

    @Override
    public Optional<Usuario> porEmail(String email) {
        return repositorio.porEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repositorio.existsByEmail(email);
    }


}
