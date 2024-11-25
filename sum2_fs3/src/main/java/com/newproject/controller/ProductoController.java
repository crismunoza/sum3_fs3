package com.newproject.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import com.newproject.exception.ResourceNotFoundException;
import com.newproject.model.Producto;
import com.newproject.service.ProductoService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;
    private static final String ROLE_ADMIN = "ROLE_ADMIN"; 

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public ResponseEntity<List<Producto>> obtenerTodosLosProductos() {
        List<Producto> productos = productoService.obtenerTodosLosProductos();
    
        String baseUrl = ServletUriComponentsBuilder.fromCurrentRequest().replacePath(null).build().toUriString();
    
        productos.forEach(producto -> {
            if (producto.getUrl() != null && !producto.getUrl().isEmpty()) {
                producto.setUrl(baseUrl + "/api/productos/imagenes/" + producto.getUrl());
            }
        });
    
        return ResponseEntity.ok(productos);
    }
    

    @GetMapping("/{id}")
    public ResponseEntity<Object> obtenerProductoPorId(@PathVariable Long id) {
        Optional<Producto> producto = productoService.obtenerProductoPorId(id);

        if (producto.isPresent()) {
            return ResponseEntity.ok(producto.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");
        }
    }

        @PreAuthorize("hasRole('" + ROLE_ADMIN + "')")
    @PostMapping
    public ResponseEntity<Producto> crearProducto(@Valid @RequestBody Producto producto) {
        Producto nuevoProducto = productoService.crearProducto(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
    }

        @PreAuthorize("hasRole('" + ROLE_ADMIN + "')")
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id,
            @Valid @RequestBody Producto productoActualizado) {
        Producto producto = productoService.actualizarProducto(id, productoActualizado);
        return ResponseEntity.ok(producto);
    }

        @PreAuthorize("hasRole('" + ROLE_ADMIN + "')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    
        @PreAuthorize("hasRole('" + ROLE_ADMIN + "')")
    @PostMapping("/upload")
    public ResponseEntity<Object> crearProductoConImagen(
            @RequestParam("imagen") MultipartFile imagen,
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("precio") Double precio,
            @RequestParam("stock") Integer stock,
            @RequestParam("nuevo") Integer nuevo) {
    
        try {

            String nombreArchivo = productoService.guardarImagen(imagen);
            Producto producto = new Producto();
            producto.setNombre(nombre);
            producto.setDescripcion(descripcion);
            producto.setPrecio(precio);
            producto.setStock(stock);
            producto.setNuevo(nuevo);
            producto.setUrl(nombreArchivo); 
    
            Producto nuevoProducto = productoService.crearProducto(producto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar el producto");
        }
    }
    
    @GetMapping("/imagenes/{nombreImagen}")
    public ResponseEntity<Resource> obtenerImagen(@PathVariable String nombreImagen) {
        try {
            Path rutaArchivo = Paths.get("src/main/resources/static/images").resolve(nombreImagen).normalize();
            Resource recurso = new UrlResource(rutaArchivo.toUri());
            if (!recurso.exists()) {
                throw new ResourceNotFoundException("Archivo no encontrado: " + nombreImagen);
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(rutaArchivo))
                    .body(recurso);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

}
