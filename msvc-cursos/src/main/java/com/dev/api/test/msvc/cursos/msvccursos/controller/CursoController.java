package com.dev.api.test.msvc.cursos.msvccursos.controller;

import com.dev.api.test.msvc.cursos.msvccursos.models.Usuario;
import com.dev.api.test.msvc.cursos.msvccursos.models.entity.Curso;
import com.dev.api.test.msvc.cursos.msvccursos.services.CursoService;
import feign.FeignException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class CursoController {

    @Autowired
    private CursoService service;

    @GetMapping
    public ResponseEntity<List<Curso>> listar(){
        return ResponseEntity.ok(service.listar());
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id){
        //Optional<Curso> o = service.porId(id);      // se comenta para ir a consultar con el detalle de los usuarios
        Optional<Curso> o = service.porIdConUsuarios(id);        // nueva forma de consultar
        if( o.isPresent()){
            return ResponseEntity.ok(o.get());
        }
        return ResponseEntity.notFound().build();
    }


    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Curso curso, BindingResult result){
        if( result.hasErrors()){
            return validar(result);
        }


        Curso cursoDB = service.guardar(curso);
        return ResponseEntity.status(HttpStatus.CREATED).body(cursoDB);
    }

    @PutMapping("/{id}")
    public  ResponseEntity<?> editar(@Valid @RequestBody Curso curso, BindingResult result , @PathVariable Long id){
        Optional<Curso> o = service.porId(id);
        if( o.isPresent()){
            Curso cursoDB = o.get();
            cursoDB.setNombre(curso.getNombre());
            return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(cursoDB));
        }
        return ResponseEntity.notFound().build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id){
        Optional<Curso> o = service.porId(id);
        if( o.isPresent()){
            service.eliminar(o.get().getId());
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/asignar-usuario/{cursoId}")
    public ResponseEntity<?> asignarUsuario(@RequestBody Usuario usuario , @PathVariable Long cursoId){
        Optional<Usuario> o ;

        try {
            o = service.asignarUsuario(usuario, cursoId);
        }
        catch (FeignException.FeignClientException e){
            return  ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("Mensaje",
                            "No existe el usuario por el id o error en la comunicacion"
                                    + e.getMessage()));

        }
        if( o.isPresent() ){
            return ResponseEntity.status(HttpStatus.CREATED).body(o.get());
        }
        return ResponseEntity.notFound().build();
    }


    @PostMapping("/crear-usuario/{cursoId}")
    public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario , @PathVariable Long cursoId){
        Optional<Usuario> o ;

        try {
            o = service.crearUsuario(usuario, cursoId);
        }
        catch (FeignException.FeignClientException e){
            return  ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("Mensaje",
                            "No se pudo crear el usuario o error en la comunicacion"
                                    + e.getMessage()));

        }
        if( o.isPresent() ){
            return ResponseEntity.status(HttpStatus.CREATED).body(o.get());
        }
        return ResponseEntity.notFound().build();
    }


    @DeleteMapping("/eliminar-usuario/{cursoId}")
    public ResponseEntity<?> eliminarUsuario(@RequestBody Usuario usuario , @PathVariable Long cursoId){
        Optional<Usuario> o ;

        try {
            o = service.eliminarUsuario(usuario, cursoId);
        }
        catch (FeignException.FeignClientException e){
            return  ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("Mensaje",
                            "No existe el usuario por el id o error en la comunicacion"
                                    + e.getMessage()));

        }
        if( o.isPresent() ){
            return ResponseEntity.status(HttpStatus.OK).body(o.get());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/eliminar-curso-usuario/{id}")
    public ResponseEntity<?> eliminarCursoUsuarioPorId(@PathVariable Long id){

        service.eliminarCursoUsuarioPorId(id);
        return ResponseEntity.noContent().build();

    }




    private static ResponseEntity<Map<String, String>> validar(BindingResult result) {
        Map<String,String> errores = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errores.put(err.getField() , "El Campo" + err.getField() + " " + err.getDefaultMessage());

        });
        return ResponseEntity.badRequest().body(errores);
    }

}
