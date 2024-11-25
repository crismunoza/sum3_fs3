package com.newproject.service;

import com.newproject.model.Producto;
import com.newproject.repository.ProductoRepository;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import com.newproject.exception.FileProcessingException;
import com.newproject.exception.ResourceNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.io.IOException;
import java.nio.file.Path;


@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    protected final Path rutaDirectorio = Paths.get("src/main/resources/static/images/");

    public ProductoService(ProductoRepository productoRepository) throws IOException {
        this.productoRepository = productoRepository;
        Files.createDirectories(rutaDirectorio);
    }

    public String guardarImagen(MultipartFile archivo) throws IOException {
        String nombreArchivo = archivo.getOriginalFilename();
        if (nombreArchivo == null || nombreArchivo.isBlank()) {
            throw new IllegalArgumentException("El archivo no tiene un nombre válido");
        }
        nombreArchivo = StringUtils.cleanPath(nombreArchivo);
    
        Path destino = rutaDirectorio.resolve(nombreArchivo);
        Files.copy(archivo.getInputStream(), destino);
    
        return nombreArchivo;
    }

    public Resource cargarImagen(String nombreArchivo) throws IOException {
        Path archivo = rutaDirectorio.resolve(nombreArchivo).normalize();
        Resource recurso = new UrlResource(archivo.toUri());

        if (recurso.exists() || recurso.isReadable()) {
            return recurso;
        } else {
            throw new ResourceNotFoundException("No se pudo leer el archivo");
        }
    }

    public Resource obtenerImagen(String nombreImagen) {
        try {
            Path rutaArchivo = Paths.get("src/main/resources/static/images")
                                    .resolve(nombreImagen)
                                    .normalize();

            // Validar si la ruta está fuera del directorio permitido
            if (!rutaArchivo.startsWith("src/main/resources/static/images")) {
                throw new SecurityException("Acceso no autorizado al archivo: " + nombreImagen);
            }

            Resource recurso = new UrlResource(rutaArchivo.toUri());

            if (!recurso.exists() || !recurso.isReadable()) {
                throw new ResourceNotFoundException("Archivo no encontrado o no legible: " + nombreImagen);
            }

            return recurso;

        } catch (Exception ex) {
            // Usar la excepción personalizada para mayor claridad
            throw new FileProcessingException("Error al obtener el archivo: " + nombreImagen, ex);
        }
    }

    
 
    public List<Producto> obtenerTodosLosProductos() {
        return productoRepository.findAll();
    }
    
    public Optional<Producto> obtenerProductoPorId(Long id) {
        return productoRepository.findById(id);
    }
    
    public Producto crearProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    public Producto actualizarProducto(Long id, Producto productoActualizado) {
        return productoRepository.findById(id)
                .map(producto -> {
                    producto.setNombre(productoActualizado.getNombre());
                    producto.setPrecio(productoActualizado.getPrecio());
                    producto.setDescripcion(productoActualizado.getDescripcion());
                    producto.setStock(productoActualizado.getStock());
                    producto.setUrl(productoActualizado.getUrl());
                    producto.setNuevo(productoActualizado.getNuevo());
                    return productoRepository.save(producto);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
    }
    
    public void eliminarProducto(Long id) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

         String imagenUrl = producto.getUrl(); 

        try {
            Path imagenPath = rutaDirectorio.resolve(imagenUrl);  
            Files.deleteIfExists(imagenPath); 
        } catch (IOException e) {
            throw new ResourceNotFoundException("Error al eliminar la imagen" + e);
        }
        productoRepository.deleteById(id);
    }

}
