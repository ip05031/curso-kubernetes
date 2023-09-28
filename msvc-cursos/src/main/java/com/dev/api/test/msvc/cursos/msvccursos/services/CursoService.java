package com.dev.api.test.msvc.cursos.msvccursos.services;


import com.dev.api.test.msvc.cursos.msvccursos.models.Usuario;
import com.dev.api.test.msvc.cursos.msvccursos.models.entity.Curso;

import java.util.List;
import java.util.Optional;

public interface CursoService {
    List<Curso> listar();
    Optional<Curso> porId(Long id);
    Curso guardar(Curso curso);
    void eliminar(Long id);

    void eliminarCursoUsuarioPorId(Long id);

    Optional<Usuario> asignarUsuario(Usuario usuario , Long cursoId);
    Optional<Usuario> crearUsuario(Usuario usuario , Long cursoId);
    Optional<Usuario> eliminarUsuario(Usuario usuario , Long cursoId);

    Optional<Curso> porIdConUsuarios(Long id);



}
