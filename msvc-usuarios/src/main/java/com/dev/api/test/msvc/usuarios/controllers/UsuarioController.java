package com.dev.api.test.msvc.usuarios.controllers;

import com.dev.api.test.msvc.usuarios.models.entity.Usuario;
import com.dev.api.test.msvc.usuarios.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.util.*;

@RestController
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @GetMapping("/")
    public List<Usuario> listar(){
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id){
            Optional<Usuario> usuarioOptional = service.porId(id);
            if( usuarioOptional.isPresent() ){
                return ResponseEntity.ok().body(usuarioOptional.get());
            }
            return ResponseEntity.notFound().build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity crear(@Valid  @RequestBody Usuario usuario, BindingResult result){

        if( result.hasErrors()){
            return validar(result);
        }

        if(!usuario.getEmail().isEmpty() && service.existsByEmail(usuario.getEmail())){
            return ResponseEntity.badRequest()
                    .body(Collections
                            .singletonMap("mensaje","Ya existe un usuario con ese correo electronico"));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(usuario));
    }



    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@Valid  @RequestBody Usuario usuario, BindingResult result , @PathVariable Long id){



        if( result.hasErrors()){
            return validar(result);
        }

        Optional<Usuario> o = service.porId(id);
        if(o.isPresent()){
            Usuario usuarioDB = o.get();

            if(!usuario.getEmail().equalsIgnoreCase(usuarioDB.getEmail()) && service.porEmail(usuario.getEmail()).isPresent()){
                return ResponseEntity.badRequest()
                        .body(Collections
                                .singletonMap("mensaje","Ya existe un usuario con ese correo electronico"));
            }
            usuarioDB.setNombre(usuario.getNombre());
            usuarioDB.setEmail(usuario.getEmail());
            usuarioDB.setPassword(usuario.getPassword());
            return ResponseEntity.status( HttpStatus.CREATED).body(service.guardar(usuarioDB));
        }
        return ResponseEntity.notFound().build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id){
        Optional<Usuario> o = service.porId(id);
        if( o.isPresent()){
            service.eliminar(id);
            return ResponseEntity.noContent().build();

        }
        return ResponseEntity.notFound().build();
    }


    @GetMapping("/usuarios-curso")
    public ResponseEntity obtenerAlumnosPorCurso(@RequestParam List<Long> ids){
        return ResponseEntity.ok( service.listarPorIds(ids));
    }


    private static ResponseEntity<Map<String, String>> validar(BindingResult result) {
        Map<String,String> errores = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errores.put(err.getField() , "El Campo" + err.getField() + " " + err.getDefaultMessage());

        });
        return ResponseEntity.badRequest().body(errores);
    }

}
